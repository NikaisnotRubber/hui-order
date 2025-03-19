<template>
    <div class="order-id-selector">
      <label v-if="label">{{ label }}</label>
      <select 
        v-model="selectedOrderId" 
        @change="$emit('change', selectedOrderId)"
        class="order-select"
      >
        <option value="">{{ placeholder }}</option>
        <option v-for="order in orders" :key="order.id" :value="order.id">
          訂單編號: {{ order.id }}
        </option>
      </select>
      <div v-if="loading" class="loading">載入中...</div>
      <div v-if="error" class="error">{{ error }}</div>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import { orderService } from '@/services/orders';
  import { Order } from '@/types/order';
  
  const props = defineProps({
    label: {
      type: String,
      default: '選擇訂單'
    },
    placeholder: {
      type: String,
      default: '請選擇訂單'
    }
  });
  
  const emit = defineEmits(['change']);
  const orders = ref<Order[]>([]);
  const selectedOrderId = ref<number | ''>('');
  const loading = ref(false);
  const error = ref<string | null>(null);
  
  const loadOrders = async () => {
    loading.value = true;
    error.value = null;
    
    try {
      // 使用現有的 getOrders 方法獲取訂單列表
      orders.value = await orderService.getOrders();
    } catch (err) {
      error.value = '無法載入訂單';
      console.error('Failed to load orders', err);
    } finally {
      loading.value = false;
    }
  };
  
  onMounted(loadOrders);
  </script>
  
  <style scoped>
  .order-id-selector {
    margin-bottom: 1rem;
  }
  
  label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
  }
  
  .order-select {
    width: 100%;
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
  }
  
  .loading, .error {
    margin-top: 0.5rem;
    font-size: 0.875rem;
  }
  
  .loading {
    color: #666;
  }
  
  .error {
    color: #e74c3c;
  }
  </style>