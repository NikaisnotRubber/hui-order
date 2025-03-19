<template>
  <div class="orders-container">
    <h1>我的訂單</h1>
    <!-- Order ID Selector -->
    <OrderIdSelector 
      @change="selectOrder" 
      label="快速查看訂單"
      placeholder="選擇訂單編號"
      class="order-id-filter"
    />
    <!-- Order Filter -->
    <div class="filter-container">
      <div class="filter-label">依狀態篩選:</div>
      <div class="filter-options">
        <button 
          @click="selectedStatus = ''" 
          :class="{ active: selectedStatus === '' }"
          class="filter-btn"
        >
          全部
        </button>
        <button 
          v-for="status in orderStatuses" 
          :key="status.value" 
          @click="selectedStatus = status.value" 
          :class="{ active: selectedStatus === status.value }"
          class="filter-btn"
        >
          {{ status.label }}
        </button>
      </div>
    </div>
    
    <!-- Order List -->
    <div v-if="filteredOrders.length > 0" class="orders-list">
      <div v-for="order in filteredOrders" :key="order.id" 
          :id="`order-${order.id}`"
          class="order-card"
        >
        <div class="order-header">
          <h3>訂單編號: {{ order.id }}</h3>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusLabel(order.status) }}
          </div>
        </div>
        
        <div class="order-info">
          <div class="order-date">
            <div><strong>訂單日期:</strong> {{ formatDate(order.createdAt) }}</div>
            <div><strong>更新時間:</strong> {{ formatDate(order.updatedAt) }}</div>
          </div>
          <div class="order-total" v-if="order.total">
            <strong>總金額:</strong> NT$ {{ order.total.toLocaleString() }}
          </div>
          <div class="order-total" v-else>
            <strong>總金額:</strong> NT$ 0
          </div>
        </div>

        <div class="order-details" v-if="expandedOrderId === order.id">
          <h4>訂單明細</h4>
          <div class="item-list">
            <div v-for="item in order.items" :key="item.itemId" class="order-item">
              <div class="item-info">
                <div class="item-name">{{ item.itemName }}</div>
                <div class="item-quantity">x {{ item.quantity }}</div>
              </div>
              <div class="item-price">NT$ {{ item.price * item.quantity }}</div>
            </div>
          </div>
          
          <div class="shipping-info">
            <h4>收件資訊</h4>
            <div><strong>收件人:</strong> {{ order.shippingName }}</div>
            <div><strong>地址:</strong> {{ order.shippingAddress }}, {{ order.shippingCity }}</div>
          </div>
        </div>
        
        <div class="order-actions">
          <button 
            @click="toggleOrderDetails(order.id)" 
            class="btn-detail"
          >
            {{ expandedOrderId === order.id ? '收起詳情' : '查看詳情' }}
          </button>
          <button 
            v-if="order.status === 'pending'" 
            @click="cancelOrder(order.id)" 
            class="btn-cancel"
          >
            取消訂單
          </button>
        </div>
      </div>
    </div>
    
    <!-- Empty State -->
    <div v-else class="empty-state">
      <div class="empty-icon">📦</div>
      <h3>暫無訂單</h3>
      <p v-if="selectedStatus">您沒有{{ getStatusLabel(selectedStatus) }}的訂單</p>
      <p v-else>您還沒有任何訂單記錄</p>
      <router-link to="/items" class="btn">去逛逛</router-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { Order } from '@/types/order'
import { useAuthStore } from '@/stores/auth'
import { orderService } from '@/services/orders'
import OrderIdSelector from '@/components/OrderIdSelector.vue'

const loadOrders = async () => {
  loading.value = true
  error.value = null
  
  try {
    orders.value = await orderService.getOrders()
  } catch (err) {
    error.value = '無法載入訂單資料'
    console.error('Failed to load orders', err)
  } finally {
    loading.value = false
  }
}

const cancelOrder = async (orderId: string) => {
  loading.value = true
  error.value = null
  
  try {
    const success = await orderService.cancelOrder(orderId)
    if (success) {
      // Update local state
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = 'canceled'
        order.updatedAt = new Date().toISOString()
      }
    }
  } catch (err) {
    error.value = '無法取消訂單'
    console.error('Failed to cancel order', err)
  } finally {
    loading.value = false
  }
}
const selectOrder = (orderId: number) => {
  if (!orderId) return
  
  // 展開選定的訂單
  expandedOrderId.value = orderId.toString()
  
  // 滾動到訂單位置
  setTimeout(() => {
    const orderElement = document.getElementById(`order-${orderId}`)
    if (orderElement) {
      orderElement.scrollIntoView({ behavior: 'smooth' })
    }
  }, 100)
}
// Auth store to get user info
const authStore = useAuthStore()

// State
const orders = ref<Order[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const expandedOrderId = ref<string | null>(null)
const selectedStatus = ref('')

// Order statuses for filter
const orderStatuses = [
  { label: '待處理', value: 'pending' },
  { label: '處理中', value: 'processing' },
  { label: '已出貨', value: 'shipped' },
  { label: '已送達', value: 'delivered' },
  { label: '已取消', value: 'canceled' }
]

// Computed
const filteredOrders = computed(() => {
  if (!selectedStatus.value) {
    return orders.value
  }
  return orders.value.filter(order => order.status === selectedStatus.value)
})

const toggleOrderDetails = (orderId: string) => {
  if (expandedOrderId.value === orderId) {
    expandedOrderId.value = null
  } else {
    expandedOrderId.value = orderId
  }
}

const formatDate = (dateString: string) => {
  const options: Intl.DateTimeFormatOptions = { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }
  return new Date(dateString).toLocaleDateString('zh-TW', options)
}

const getStatusLabel = (status: string) => {
  const statusObj = orderStatuses.find(s => s.value === status)
  return statusObj ? statusObj.label : status
}

const getStatusClass = (status: string) => {
  return {
    'status-pending': status === 'pending',
    'status-processing': status === 'processing',
    'status-shipped': status === 'shipped',
    'status-delivered': status === 'delivered',
    'status-canceled': status === 'canceled'
  }
}

// Load orders when component mounts
onMounted(async () => {
  await loadOrders()
})
</script>

<style scoped>
.orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  text-align: center;
}

.filter-container {
  margin-bottom: 2rem;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
}

.filter-label {
  font-weight: 600;
  margin-right: 0.5rem;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.filter-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover {
  background-color: #f1f1f1;
}

.filter-btn.active {
  background-color: #e8f4ff;
  border-color: #4a89dc;
  color: #4a89dc;
  font-weight: bold;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.order-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  transition: all 0.2s;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.order-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.order-status {
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 600;
}

.status-pending {
  background-color: #fff8e1;
  color: #ffa000;
}

.status-processing {
  background-color: #e3f2fd;
  color: #1976d2;
}

.status-shipped {
  background-color: #e8f5e9;
  color: #388e3c;
}

.status-delivered {
  background-color: #e8f5e9;
  color: #1b5e20;
}

.status-canceled {
  background-color: #fafafa;
  color: #757575;
}

.order-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.order-date {
  color: #666;
  font-size: 0.95rem;
}

.order-total {
  font-size: 1.1rem;
}

.order-details {
  background-color: #f9f9f9;
  padding: 1.5rem;
  border-radius: 6px;
  margin: 1rem 0;
}

.order-details h4 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.order-item {
  display: flex;
  justify-content: space-between;
  padding: 0.75rem 0;
  border-bottom: 1px solid #eee;
}

.order-item:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.item-name {
  font-weight: 500;
}

.item-quantity {
  color: #666;
  font-size: 0.9rem;
}

.item-price {
  font-weight: 600;
}

.shipping-info {
  margin-top: 1.5rem;
  line-height: 1.6;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

.btn-detail, .btn-cancel {
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-detail {
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  color: #555;
}

.btn-detail:hover {
  background-color: #eee;
}

.btn-cancel {
  background-color: #fff2f0;
  border: 1px solid #ffccc7;
  color: #cf1322;
}

.btn-cancel:hover {
  background-color: #fff1f0;
  border-color: #ffa39e;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 1rem;
  text-align: center;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
}

.empty-state h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.4rem;
}

.empty-state p {
  margin-bottom: 1.5rem;
  color: #666;
}

.btn {
  display: inline-block;
  background-color: #4a89dc;
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.2s;
}

.btn:hover {
  background-color: #3672c5;
}

@media (max-width: 768px) {
  .orders-container {
    padding: 1rem;
  }
  
  .order-header, .order-info {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .order-status {
    align-self: flex-start;
    margin-top: 0.5rem;
  }
}
</style>