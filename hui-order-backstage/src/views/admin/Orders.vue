<template>
  <div class="orders-page">
    <!-- 篩選面板 -->
    <div class="filter-panel card">
      <h3>訂單篩選</h3>
      <div class="filter-form">
        <div class="form-row">
          <div class="form-group">
            <label for="status">訂單狀態</label>
            <select id="status" v-model="filters.status" class="form-control">
              <option value="">全部狀態</option>
              <option value="PENDING">待付款</option>
              <option value="PAID">已付款</option>
              <option value="PROCESSING">處理中</option>
              <option value="SHIPPED">已發貨</option>
              <option value="DELIVERED">已送達</option>
              <option value="CANCELLED">已取消</option>
            </select>
          </div>
          
          <div class="form-group">
            <label for="startDate">開始日期</label>
            <input 
              id="startDate" 
              v-model="filters.startDate" 
              type="date" 
              class="form-control"
            >
          </div>
          
          <div class="form-group">
            <label for="endDate">結束日期</label>
            <input 
              id="endDate" 
              v-model="filters.endDate" 
              type="date" 
              class="form-control"
            >
          </div>
        </div>
        
        <div class="filter-actions">
          <button class="btn btn-primary" @click="applyFilters">應用篩選</button>
          <button class="btn btn-secondary" @click="resetFilters">重置</button>
        </div>
      </div>
    </div>
    
    <!-- 訂單列表 -->
    <div class="card">
      <div class="card-header">
        <h3>訂單列表</h3>
        <div class="order-count">共 {{ orders.length }} 個訂單</div>
      </div>
      
      <div v-if="loading" class="loading-container">
        <div class="loading">載入中...</div>
      </div>
      
      <div v-else-if="!orders.length" class="empty-state">
        <div class="empty-icon">📝</div>
        <h4>暫無訂單</h4>
        <p>當前篩選條件下沒有找到訂單記錄</p>
      </div>
      
      <div v-else class="table-responsive">
        <table class="orders-table">
          <thead>
            <tr>
              <th>訂單編號</th>
              <th>下單日期</th>
              <th>客戶ID</th>
              <th>訂單金額</th>
              <th>付款方式</th>
              <th>訂單狀態</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id">
              <td>#{{ order.id }}</td>
              <td>{{ formatDate(order.createdAt) }}</td>
              <td>{{ order.userId }}</td>
              <td>{{ formatCurrency(order.totalAmount) }}</td>
              <td>{{ translatePaymentMethod(order.paymentMethod) }}</td>
              <td>
                <span class="status-badge" :class="getStatusClass(order.orderStatus)">
                  {{ translateStatus(order.orderStatus) }}
                </span>
              </td>
              <td>
                <div class="actions">
                  <router-link :to="`/orders/${order.id}`" class="btn btn-sm btn-primary">
                    詳情
                  </router-link>
                  <button 
                    v-if="canUpdateStatus(order.orderStatus)" 
                    class="btn btn-sm btn-secondary"
                    @click="openStatusModal(order)"
                  >
                    更新狀態
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <!-- 狀態更新彈窗 -->
    <div v-if="showStatusModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>更新訂單狀態</h3>
          <button class="close-btn" @click="showStatusModal = false">&times;</button>
        </div>
        
        <div class="modal-body">
          <p>訂單 #{{ selectedOrder?.id }} 當前狀態：{{ translateStatus(selectedOrder?.orderStatus || '') }}</p>
          
          <div class="form-group">
            <label for="newStatus">選擇新狀態</label>
            <select id="newStatus" v-model="newStatus" class="form-control">
              <option v-for="status in availableStatuses" :key="status" :value="status">
                {{ translateStatus(status) }}
              </option>
            </select>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showStatusModal = false">取消</button>
          <button class="btn btn-primary" @click="updateOrderStatus" :disabled="statusUpdating">
            {{ statusUpdating ? '更新中...' : '確認更新' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import adminService, { Order } from '../../services/admin'

// 頁面狀態
const loading = ref(true)
const orders = ref<Order[]>([])
const filters = reactive({
  status: '',
  startDate: '',
  endDate: ''
})

// 狀態更新彈窗相關
const showStatusModal = ref(false)
const selectedOrder = ref<Order | null>(null)
const newStatus = ref('')
const statusUpdating = ref(false)

// 格式化金額為貨幣形式
const formatCurrency = (amount: number) => {
  return '¥' + amount.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 獲取狀態的CSS類
const getStatusClass = (status: string) => {
  return `status-${status.toLowerCase()}`
}

// 將狀態英文翻譯為中文
const translateStatus = (status: string) => {
  const translations: Record<string, string> = {
    'PENDING': '待付款',
    'PAID': '已付款',
    'PROCESSING': '處理中',
    'SHIPPED': '已發貨',
    'DELIVERED': '已送達',
    'CANCELLED': '已取消'
  }
  return translations[status] || status
}

// 翻譯付款方式
const translatePaymentMethod = (method: string) => {
  const translations: Record<string, string> = {
    'CREDIT_CARD': '信用卡',
    'ALIPAY': '支付寶',
    'WECHAT_PAY': '微信支付',
    'CASH': '現金',
    'BANK_TRANSFER': '銀行轉賬'
  }
  return translations[method] || method
}

// 檢查訂單狀態是否可以更新
const canUpdateStatus = (status: string) => {
  // 已取消的訂單不能再更新狀態
  return status !== 'CANCELLED'
}

// 根據當前狀態計算可用的下一個狀態選項
const availableStatuses = computed(() => {
  if (!selectedOrder.value) return []
  
  const currentStatus = selectedOrder.value.orderStatus
  
  switch (currentStatus) {
    case 'PENDING':
      return ['PAID', 'CANCELLED']
    case 'PAID':
      return ['PROCESSING', 'CANCELLED']
    case 'PROCESSING':
      return ['SHIPPED', 'CANCELLED']
    case 'SHIPPED':
      return ['DELIVERED', 'CANCELLED']
    case 'DELIVERED':
      return ['CANCELLED']
    default:
      return []
  }
})

// 載入訂單數據
const loadOrders = async () => {
  loading.value = true
  
  try {
    orders.value = await adminService.getOrders({
      status: filters.status,
      startDate: filters.startDate,
      endDate: filters.endDate
    })
  } catch (error) {
    console.error('Failed to load orders:', error)
  } finally {
    loading.value = false
  }
}

// 應用篩選條件
const applyFilters = () => {
  loadOrders()
}

// 重置篩選條件
const resetFilters = () => {
  filters.status = ''
  filters.startDate = ''
  filters.endDate = ''
  loadOrders()
}

// 打開狀態更新彈窗
const openStatusModal = (order: Order) => {
  selectedOrder.value = order
  newStatus.value = ''
  showStatusModal.value = true
}

// 更新訂單狀態
const updateOrderStatus = async () => {
  if (!selectedOrder.value || !newStatus.value) return
  
  statusUpdating.value = true
  
  try {
    const updatedOrder = await adminService.updateOrderStatus(
      selectedOrder.value.id,
      newStatus.value
    )
    
    // 更新本地訂單數據
    const index = orders.value.findIndex(o => o.id === updatedOrder.id)
    if (index !== -1) {
      orders.value[index] = updatedOrder
    }
    
    showStatusModal.value = false
  } catch (error) {
    console.error('Failed to update order status:', error)
    alert('更新訂單狀態失敗，請重試')
  } finally {
    statusUpdating.value = false
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
  padding: 15px;
}

.card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.card-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.filter-panel {
  padding: 20px;
}

.filter-panel h3 {
  margin-top: 0;
  margin-bottom: 15px;
}

.form-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.form-group {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-control {
  display: block;
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.filter-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.table-responsive {
  overflow-x: auto;
}

.orders-table {
  width: 100%;
  border-collapse: collapse;
}

.orders-table th,
.orders-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.orders-table th {
  background-color: #f8f9fa;
  font-weight: 600;
}

.status-badge {
  display: inline-block;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.status-pending {
  background-color: #fff7e6;
  color: #f39c12;
}

.status-paid {
  background-color: #e6f7ff;
  color: #3498db;
}

.status-processing {
  background-color: #f2e6ff;
  color: #9b59b6;
}

.status-shipped {
  background-color: #e6fff2;
  color: #2ecc71;
}

.status-delivered {
  background-color: #e6ffe6;
  color: #27ae60;
}

.status-cancelled {
  background-color: #ffe6e6;
  color: #e74c3c;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-sm {
  padding: 5px 10px;
  font-size: 0.85rem;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.loading {
  color: #888;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
  text-align: center;
  color: #888;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.empty-state h4 {
  margin: 0 0 10px;
  font-size: 1.2rem;
}

.empty-state p {
  margin: 0;
  color: #999;
}

/* 彈窗樣式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #888;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}
</style>
