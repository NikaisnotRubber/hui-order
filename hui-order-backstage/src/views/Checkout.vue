<template>
  <div class="checkout">
    <h1>結帳</h1>
    
    <div v-if="cartStore.isEmpty" class="empty-cart">
      <div class="card">
        <h2>Your Cart is Empty</h2>
        <p>Add items to your cart before proceeding to checkout.</p>
        <router-link to="/items" class="btn">Continue Shopping</router-link>
      </div>
    </div>
    
    <div v-else class="checkout-container">
      <div class="checkout-form">
        <div class="card">
          <h2>配送地址</h2>
          <form @submit.prevent="placeOrder">
            <div class="form-group">
              <label for="name">姓名</label>
              <input
                type="text"
                id="name"
                v-model="shippingInfo.name"
                class="form-control"
                required
                placeholder="Enter your full name"
              />
            </div>
            
            <div class="form-group">
              <label for="address">地址</label>
              <input
                type="text"
                id="address"
                v-model="shippingInfo.address"
                class="form-control"
                required
                placeholder="Enter your street address"
              />
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="city">縣市</label>
                <input
                  type="text"
                  id="city"
                  v-model="shippingInfo.city"
                  class="form-control"
                  required
                  placeholder="City"
                />
              </div>
            </div>

            <button type="submit" class="btn complete-order-btn" :disabled="loading">
              {{ loading ? 'Processing...' : '結帳' }}
            </button>
          </form>
        </div>
      </div>
      
      <div class="order-summary">
        <div class="card">
          <h2>訂單總結</h2>
          
          <div class="cart-items">
            <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
              <div class="item-info">
                <h3>{{ item.name }}</h3>
                <p>${{ item.price.toFixed(2) }} x {{ item.quantity }}</p>
              </div>
              <p class="item-total">${{ (item.price * item.quantity).toFixed(2) }}</p>
            </div>
          </div>
          
          <div class="order-totals">
            <div class="subtotal">
              <span>Subtotal</span>
              <span>${{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>
            <div class="shipping">
              <span>Shipping</span>
              <span>Free</span>
            </div>
            <div class="total">
              <span>Total</span>
              <span>${{ (cartStore.totalPrice).toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { useAuthStore } from '../stores/auth'
import { orderService } from '../services/orders'
import { OrderItem } from '../types/order'

const router = useRouter()
const cartStore = useCartStore()
const authStore = useAuthStore()
const loading = ref(false)

// Shipping information 
const shippingInfo = reactive({
  name: authStore.user?.name || '',
  address: '',
  city: '',
})

// Place order
const placeOrder = async () => {
  loading.value = true;
  
  try {
    // Convert cart items to order items
    const orderItems: OrderItem[] = cartStore.items.map(item => ({
      itemId: item.id,
      itemName: item.name,
      quantity: item.quantity,
      price: item.price
    }));
    
    // Create order using orderService
    const order = await orderService.createOrder({
      items: orderItems,
      shippingInfo: shippingInfo,
      total: cartStore.totalPrice,
      amount: cartStore.totalPrice // Add total amount field
    });
    
    // Clear the cart
    cartStore.clearCart();
    
    // Show success message
    alert(`訂單 #${order.id} 創建成功!`);
    
    // Redirect to orders page
    router.push('/orders');
  } catch (error) {
    console.error('Error placing order:', error);
    alert('訂單處理過程中出現錯誤，請稍後再試。');
  } finally {
    loading.value = false;
  }
};
// Redirect if not authenticated
onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push({
      path: '/login',
      query: { redirect: '/checkout' }
    })
  }
})
</script>

<style scoped>
.checkout {
  padding: 1rem 0;
}

h1 {
  margin-bottom: 2rem;
}

.empty-cart {
  text-align: center;
  padding: 2rem;
}

.checkout-container {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  padding: 2rem;
  margin-bottom: 1.5rem;
}

h2 {
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--border-color);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-row .form-group {
  flex: 1;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.mock-payment-info {
  color: var(--text-light);
  font-style: italic;
  margin-bottom: 1.5rem;
}

.complete-order-btn {
  width: 100%;
  padding: 0.75rem;
  font-size: 1.1rem;
  margin-top: 1rem;
}

.cart-items {
  margin-bottom: 1.5rem;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  padding: 1rem 0;
  border-bottom: 1px solid var(--border-color);
}

.cart-item:last-child {
  border-bottom: none;
}

.item-info h3 {
  margin: 0 0 0.25rem 0;
  font-size: 1rem;
}

.item-info p {
  color: var(--text-light);
  font-size: 0.9rem;
}

.item-total {
  font-weight: bold;
}

.order-totals {
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.subtotal, .shipping, .tax, .total {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.total {
  font-weight: bold;
  font-size: 1.25rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

@media (max-width: 768px) {
  .checkout-container {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>
