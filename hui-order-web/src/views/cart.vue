<template>
  <div class="cart">
    <h1>購物車</h1>
    
    <div v-if="cartStore.isEmpty" class="empty-cart">
      <div class="card">
        <h2>購物車空空如也</h2>
        <p>您的購物車中沒有商品。</p>
        <router-link to="/items" class="btn">開始購物</router-link>
      </div>
    </div>
    
    <div v-else class="cart-content">
      <div class="cart-items">
        <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
          <div class="item-image">
            <img :src="item.imageUrl" :alt="item.name" />
          </div>
          
          <div class="item-details">
            <h3>{{ item.name }}</h3>
            <p class="item-price">${{ item.price.toFixed(2) }}</p>
            <p class="item-category">{{ capitalizeFirstLetter(item.category) }}</p>
          </div>
          
          <div class="item-quantity">
            <button 
              @click="decreaseQuantity(item)" 
              class="quantity-btn"
              :disabled="item.quantity <= 1"
            >-</button>
            <span class="quantity">{{ item.quantity }}</span>
            <button 
              @click="increaseQuantity(item)" 
              class="quantity-btn"
              :disabled="item.quantity >= item.stock"
            >+</button>
          </div>
          
          <div class="item-total">
            <p>${{ (item.price * item.quantity).toFixed(2) }}</p>
          </div>
          
          <div class="item-actions">
            <button @click="removeFromCart(item.id)" class="btn btn-danger">刪除</button>
          </div>
        </div>
      </div>
      
      <div class="cart-summary">
        <div class="card">
          <h2>訂單摘要</h2>
          <div class="summary-row">
            <span>商品數量:</span>
            <span>{{ cartStore.totalItems }}</span>
          </div>
          <div class="summary-row total">
            <span>總計:</span>
            <span>${{ cartStore.totalPrice.toFixed(2) }}</span>
          </div>
          <div class="checkout-button">
            <router-link to="/checkout" class="btn btn-primary">結算</router-link>
          </div>
          <div class="clear-cart">
            <button @click="clearCart" class="btn btn-secondary">清空購物車</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted } from 'vue'
import { useCartStore } from '../stores/cart'
import { Item } from '../types/item'

const cartStore = useCartStore()

onMounted(() => {
  // 加載本地存儲的購物車數據
  cartStore.loadCart()
  console.log('已加載購物車數據:', cartStore.items)
})

const capitalizeFirstLetter = (string: string) => {
  return string.charAt(0).toUpperCase() + string.slice(1)
}

// 增加商品數量
const increaseQuantity = (item: Item & { quantity: number }) => {
  if (item.quantity < item.stock) {
    cartStore.updateQuantity(item.id, item.quantity + 1)
  }
}

// 減少商品數量
const decreaseQuantity = (item: Item & { quantity: number }) => {
  if (item.quantity > 1) {
    cartStore.updateQuantity(item.id, item.quantity - 1)
  }
}

// 從購物車移除商品
const removeFromCart = (itemId: string) => {
  /*
  if (confirm('確定要從購物車中移除此商品嗎？')) {
    console.log('嘗試移除商品:', itemId)
    const result = cartStore.removeItem(itemId)
    if (result) {
      alert('商品已成功從購物車中移除')
    } else {
      alert('移除失敗，找不到該商品')
    }
  }*/
  const result = cartStore.removeItem(itemId)

}

// 清空購物車
const clearCart = () => {
  if (confirm('確定要清空購物車嗎？此操作無法撤銷。')) {
    try {
      cartStore.clearCart()
      alert('購物車已清空')
    } catch (error) {
      console.error('清空購物車時出錯:', error)
      alert('清空購物車時發生錯誤，請重試')
    }
  }
}
</script>

<style scoped>
.cart {
  padding: 1rem 0;
}

h1 {
  margin-bottom: 2rem;
}

.empty-cart {
  text-align: center;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
}

.cart-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 2rem;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.cart-item {
  display: grid;
  grid-template-columns: 100px 2fr 1fr 1fr 80px;
  align-items: center;
  background: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  padding: 1rem;
  gap: 1rem;
}

.item-image {
  width: 100%;
  height: 80px;
  overflow: hidden;
  border-radius: 4px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-details h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
}

.item-price {
  font-weight: bold;
  color: var(--primary-color);
  margin-bottom: 0.25rem;
}

.item-category {
  font-size: 0.8rem;
  color: var(--text-light);
}

.item-quantity {
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-btn {
  background-color: #f0f0f0;
  border: none;
  width: 30px;
  height: 30px;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity {
  margin: 0 0.5rem;
  font-size: 1rem;
  width: 30px;
  text-align: center;
}

.item-total {
  font-weight: bold;
  font-size: 1.1rem;
}

.cart-summary .card {
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.summary-row.total {
  font-weight: bold;
  font-size: 1.2rem;
  color: var(--primary-color);
  border-bottom: none;
  margin-top: 1rem;
}

.checkout-button {
  margin-top: 1.5rem;
}

.checkout-button .btn {
  width: 100%;
  padding: 0.75rem;
  font-size: 1.1rem;
}

.clear-cart {
  margin-top: 1rem;
  text-align: center;
}

.clear-cart .btn {
  width: 100%;
  background-color: transparent;
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

.clear-cart .btn:hover {
  background-color: #f0f0f0;
}

@media (max-width: 768px) {
  .cart-content {
    grid-template-columns: 1fr;
  }
  
  .cart-item {
    grid-template-columns: 80px 1fr;
    grid-template-rows: auto auto auto;
  }
  
  .item-image {
    grid-row: span 3;
  }
  
  .item-quantity, .item-total, .item-actions {
    grid-column: 2;
    justify-content: flex-start;
    margin-top: 0.5rem;
  }
}
</style>
