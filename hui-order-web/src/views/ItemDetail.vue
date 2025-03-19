<template>
  <div class="item-detail">
    <div v-if="loading" class="loading">
      <p>Loading item details...</p>
    </div>
    
    <div v-else-if="error" class="error">
      <p>{{ error }}</p>
      <router-link to="/items" class="btn">Back to Items</router-link>
    </div>
    
    <div v-else-if="item" class="item-container">
      <div class="item-image-container">
        <img :src="item.imageUrl" :alt="item.name" class="item-image" />
      </div>
      
      <div class="item-info">
        <h1>{{ item.name }}</h1>
        <p class="item-price">${{ item.price.toFixed(2) }}</p>
        <p class="item-category">Category: {{ capitalizeFirstLetter(item.category) }}</p>
        <div class="stock-info" :class="{ 'low-stock': item.stock < 5 && item.stock > 0 }">
          <span v-if="item.stock > 0">{{ item.stock }} in stock</span>
          <span v-else class="out-of-stock">Out of stock</span>
        </div>
        
        <div class="item-description">
          <h2>Description</h2>
          <p>{{ item.description }}</p>
        </div>
        
        <div class="quantity-selector" v-if="item.stock > 0">
          <button 
            @click="decreaseQuantity" 
            class="quantity-btn"
            :disabled="quantity <= 1"
          >-</button>
          <span class="quantity">{{ quantity }}</span>
          <button 
            @click="increaseQuantity" 
            class="quantity-btn"
            :disabled="quantity >= item.stock"
          >+</button>
        </div>
        
        <div class="action-buttons">
          <button 
            @click="addToCart" 
            class="btn add-to-cart"
            :disabled="item.stock <= 0"
          >
            {{ item.stock > 0 ? 'Add to Cart' : 'Out of Stock' }}
          </button>
          
          <button 
            v-if="isAuthenticated"
            @click="checkout" 
            class="btn buy-now"
            :disabled="item.stock <= 0"
          >
            Buy Now
          </button>
          
          <router-link 
            v-else
            to="/login" 
            class="btn login-to-buy"
          >
            Login to Buy
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { itemService } from '../services/items'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'
import { Item } from '../types/item'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const item = ref<Item | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)
const quantity = ref(1)

const isAuthenticated = computed(() => authStore.isAuthenticated)

// Fetch item details on component mount
onMounted(async () => {
  const id = route.params.id as string
  
  if (!id) {
    error.value = 'Item ID is required'
    loading.value = false
    return
  }
  
  try {
    const fetchedItem = await itemService.getItemById(id)
    
    if (!fetchedItem) {
      error.value = 'Item not found'
    } else {
      item.value = fetchedItem
    }
  } catch (err) {
    console.error('Error fetching item:', err)
    error.value = 'Failed to load item details'
  } finally {
    loading.value = false
  }
})

// Helper function to capitalize first letter
const capitalizeFirstLetter = (string: string) => {
  return string.charAt(0).toUpperCase() + string.slice(1)
}

// Quantity control functions
const increaseQuantity = () => {
  if (item.value && quantity.value < item.value.stock) {
    quantity.value++
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

// Add to cart function
const addToCart = () => {
  if (item.value) {
    cartStore.addItem(item.value, quantity.value)
    
    // Show a confirmation message
    alert(`Added ${quantity.value} ${item.value.name} to your cart`)
  }
}

// Buy now function
const checkout = () => {
  if (item.value) {
    // Clear cart first and add only this item
    cartStore.clearCart()
    cartStore.addItem(item.value, quantity.value)
    
    // Redirect to checkout
    router.push('/checkout')
  }
}
</script>

<style scoped>
.item-detail {
  padding: 1rem 0;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
}

.error .btn {
  margin-top: 1rem;
}

.item-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  padding: 2rem;
}

.item-image-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.item-image {
  width: 100%;
  max-width: 500px;
  height: auto;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.item-image:hover {
  transform: scale(1.02);
}

.item-info {
  display: flex;
  flex-direction: column;
}

.item-info h1 {
  margin-bottom: 0.5rem;
  font-size: 2rem;
}

.item-price {
  font-size: 1.75rem;
  font-weight: bold;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.item-category {
  color: var(--text-light);
  margin-bottom: 0.5rem;
}

.stock-info {
  font-size: 0.9rem;
  padding: 0.25rem 0.5rem;
  background: #e8f5e9;
  color: #2e7d32;
  border-radius: 4px;
  display: inline-block;
  margin-bottom: 1rem;
}

.low-stock {
  background: #fff3e0;
  color: #e65100;
}

.out-of-stock {
  background: #ffebee;
  color: #c62828;
}

.item-description {
  margin-bottom: 1.5rem;
}

.item-description h2 {
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
}

.quantity-selector {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.quantity-btn {
  background: #f5f5f5;
  border: 1px solid var(--border-color);
  width: 2rem;
  height: 2rem;
  font-size: 1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 4px;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity {
  padding: 0 1rem;
  font-size: 1.1rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
}

.add-to-cart, .buy-now, .login-to-buy {
  flex: 1;
  padding: 0.75rem;
  font-size: 1rem;
}

.add-to-cart {
  background-color: var(--primary-color);
}

.buy-now {
  background-color: var(--secondary-color);
}

.login-to-buy {
  background-color: var(--accent-color);
  text-align: center;
}

/* Responsive styles */
@media (max-width: 768px) {
  .item-container {
    grid-template-columns: 1fr;
  }
  
  .item-image-container {
    margin-bottom: 1.5rem;
  }
}
</style>
