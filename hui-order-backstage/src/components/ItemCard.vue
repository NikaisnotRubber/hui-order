<template>
  <div>
    <!-- 普通商品卡片视图 -->
    <div v-if="!showDetails" class="item-card">
      <div class="item-image">
        <img :src="item.imageUrl" :alt="item.name" />
      </div>
      <div class="item-content">
        <h3 class="item-title">{{ item.name }}</h3>
        <p class="item-price">${{ item.price.toFixed(2) }}</p>
        <p class="item-category">{{ capitalizeFirstLetter(item.category) }}</p>
        <div class="item-actions">
          <button @click="toggleDetails" class="btn btn-secondary">查看細節</button>
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
    
    <!-- 详细信息视图 -->
    <item-detail-card 
      v-else 
      :item="item" 
      @back="toggleDetails" 
    />
  </div>
</template>

<script lang="ts" setup>
import { defineProps, ref } from 'vue'
import { useCartStore } from '../stores/cart'
import { Item } from '../types/item'
import ItemDetailCard from './ItemDetailCard.vue'

const props = defineProps<{
  item: Item
}>()

const cartStore = useCartStore()

// 控制是否显示详情视图
const showDetails = ref(false)

const capitalizeFirstLetter = (string: string) => {
  return string.charAt(0).toUpperCase() + string.slice(1)
}

const addToCart = () => {
  cartStore.addItem(props.item, 1)
  
  // 显示确认消息
  
  alert(`已將 ${props.item.name} 加入購物車`)
}

// 切换详情/卡片视图
const toggleDetails = () => {
  showDetails.value = !showDetails.value
}
</script>

<style scoped>
.item-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.item-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
}

.item-image {
  height: 250px;
  width: 100%;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.item-card:hover .item-image img {
  transform: scale(1.05);
}

.item-content {
  padding: 1.5rem;
}

.item-title {
  margin: 0 0 0.5rem 0;
  font-size: 1.25rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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
  margin-bottom: 1rem;
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
