<template>
  <div class="item-detail-card">
    <div class="item-image">
      <img :src="item.imageUrl" :alt="item.name" />
    </div>
    
    <div class="item-content">
      <h3 class="item-title">{{ item.name }}</h3>
      <p class="item-price">${{ item.price.toFixed(2) }}</p>
      <p class="item-category">{{ capitalizeFirstLetter(item.category) }}</p>
      
      <div class="stock-info" :class="{ 'low-stock': item.stock < 5 && item.stock > 0 }">
        <span v-if="item.stock > 0">庫存: {{ item.stock }} 件</span>
        <span v-else class="out-of-stock">已售完</span>
      </div>
      
      <div class="item-description">
        <h4>商品介紹</h4>
        <p>{{ item.description }}</p>
      </div>
      
      <div class="item-actions">
        <button @click="$emit('back')" class="btn btn-secondary">返回</button>
        <button 
          @click="addToCart" 
          class="btn"
          :disabled="item.stock <= 0"
        >
          {{ item.stock > 0 ? '加入購物車' : '已售完' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits } from 'vue'
import { useCartStore } from '../stores/cart'
import { Item } from '../types/item'

const props = defineProps<{
  item: Item
}>()

defineEmits<{
  (e: 'back'): void
}>()

const cartStore = useCartStore()

const capitalizeFirstLetter = (string: string) => {
  return string.charAt(0).toUpperCase() + string.slice(1)
}

const addToCart = () => {
  cartStore.addItem(props.item, 1)
  
  // 顯示確認訊息
  alert(`已將 ${props.item.name} 加入購物車`)
}
</script>

<style scoped>
.item-detail-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.item-image {
  height: 200px;
  width: 100%;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-content {
  padding: 1.5rem;
}

.item-title {
  margin: 0 0 0.5rem 0;
  font-size: 1.25rem;
}

.item-price {
  font-size: 1.25rem;
  font-weight: bold;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.item-category {
  font-size: 0.9rem;
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

.item-description h4 {
  font-size: 1rem;
  margin-bottom: 0.5rem;
}

.item-actions {
  display: flex;
  gap: 0.5rem;
}

.item-actions .btn {
  flex: 1;
  text-align: center;
  font-size: 0.9rem;
}

.item-actions .btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
