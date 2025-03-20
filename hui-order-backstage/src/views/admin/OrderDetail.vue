<template>
  <div class="order-detail-page">
    <div class="page-header">
      <router-link to="/orders" class="back-link">
        &larr; 返回訂單列表
      </router-link>
      
      <div v-if="loading" class="loading">載入中...</div>
    </div>
    
    <div v-if="!loading && !order" class="error-state">
      <h3>找不到訂單</h3>
      <p>無法找到指定的訂單資訊。</p>
      <router-link to="/orders" class="btn btn-primary">
        返回訂單列表
      </router-link>
    </div>
    
    <template v-if="!loading && order">
      <!-- 訂單基本信息卡片 -->
      <div class="card">
        <div class="card-header">
          <h3>訂單 #{{ order.id }} 詳情</h3>
          <span class="status-badge" :class="getStatusClass(order.orderStatus)">
            {{ translateStatus(order.orderStatus) }}
          </span>
        </div>
        
        <div class="card-body">
          <div class="order-summary">
            <div class="summary-item">
              <div class="summary-label">訂單日期</div>
              <div class="summary-value">{{ formatDate(order.createdAt) }}</div>
            </div>
            
            <div class="summary-item">
              <div class="summary-label">客戶ID</div>
              <div class="summary-value">{{ order.userId }}</div>
            </div>
            
            <div class="summary-item">
              <div class="summary-label">訂單金額</div>
              <div class="summary-value">{{ formatCurrency(order.totalAmount) }}</div>
            </div>
            
            <div class="summary-item">
              <div class="summary-label">付款方式</div>
              <div class="summary-value">{{ translatePaymentMethod(order.paymentMethod) }}</div>
            </div>
            
            <div class="summary-item">
              <div class="summary-label">更新時間</div>
              <div class="summary-value">{{ formatDate(order.updatedAt) }}</div>
            </div>
          </div>
          
          <div class="order-actions" v-if="canUpdateStatus(order.orderStatus)">
            <button class="btn btn-primary" @click="openStatusModal">
              更新訂單狀態
            </button>
          </div>
        </div>
      </div>
      
      <!-- 送貨信息卡片 -->
      <div class="card">
        <div class="card-header">
          <h3>送貨信息</h3>
        </div>
        
        <div class="card-body">
          <div class="delivery-address">
            <h4>送貨地址</h4>
            <p>{{ order.deliveryAddress || '無送貨地址' }}</p>
          </div>
        </div>
      </div>
      
      <!-- 商品列表卡片 -->
      <div class="card">
        <div class="card-header">
          <h3>訂購商品</h3>
          <span class="item-count">{{ order.items?.length || 0 }} 件商品</span>
        </div>
        
        <div class="card-body">
          <div v-if="!order.items || order.items.length === 0" class="empty-items">
            無商品資訊
          </div>
          
          <table v-else class="items-table">
            <thead>
              <tr>
                <th>商品名稱</th>
                <th>單價</th>
                <th>數量</th>
                <th>小計</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in order.items" :key="item.id">
                <td>{{ item.itemName }}</td>
                <td>{{ formatCurrency(item.price) }}</td>
                <td>{{ item.quantity }}</td>
                <td>{{ formatCurrency(item.price * item.quantity) }}</td>
              </tr>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="3" class="total-label">總計</td>
                <td class="total-value">{{ formatCurrency(order.totalAmount) }}</td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </template>
    
    <!-- 狀態更新彈窗 -->
    <div v-if="showStatusModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>更新訂單狀態</h3>
          <button class="close-btn" @click="showStatusModal = false">&times;</button>
        </div>
        
        <div class="modal-body">
          <p>訂單 #{{ order?.id }} 當前狀態：{{ translateStatus(order?.orderStatus || '') }}</p>
          
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
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import adminService, { Order } from '../../services/admin'

const route = useRoute()
const router = useRouter()
const orderId = computed(() => Number(route.params.id))

// 頁面狀態
const loading = ref(true)
const order = ref<Order | null>(null)

// 狀態更新彈窗相關
const showStatusModal = ref(false)
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
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
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
  if (!order.value) return []
  
  const currentStatus = order.value.orderStatus
  
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

// 載入訂單詳情
const loadOrderDetail = async () => {
  loading.value = true
  
  try {
    order.value = await adminService.getOrderById(orderId.value)
  } catch (error) {
    console.error('Failed to load order details:', error)
    order.value = null
  } finally {
    loading.value = false
  }
}

// 打開狀態更新彈窗
const openStatusModal = () => {
  newStatus.value = ''
  showStatusModal.value = true
}

// 更新訂單狀態
const updateOrderStatus = async () => {
  if (!order.value || !newStatus.value) return
  
  statusUpdating.value = true
  
  try {
    const updatedOrder = await adminService.updateOrderStatus(
      order.value.id,
      newStatus.value
    )
    
    // 更新本地訂單數據
    order.value = updatedOrder
    showStatusModal.value = false
  } catch (error) {
    console.error('Failed to update order status:', error)
    alert('更新訂單狀態失敗，請重試')
  } finally {
    statusUpdating.value = false
  }
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
  padding: 15px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  color: #4a69bd;
  text-decoration: none;
  font-weight: 500;
}

.back-link:hover {
  text-decoration: underline;
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

.card-body {
  padding: 20px;
}

.order-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.summary-item {
  margin-bottom: 15px;
}

.summary-label {
  font-weight: 500;
  color: #7f8c8d;
  margin-bottom: 5px;
}

.summary-value {
  font-size: 1.1rem;
}

.order-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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

.delivery-address h4 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 1rem;
}

.delivery-address p {
  margin: 0;
  line-height: 1.5;
}

.items-table {
  width: 100%;
  border-collapse: collapse;
}

.items-table th,
.items-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.items-table th {
  font-weight: 600;
  color: #2c3e50;
  background-color: #f8f9fa;
}

.items-table tfoot td {
  border-bottom: none;
  padding-top: 15px;
}

.total-label {
  text-align: right;
  font-weight: 600;
}

.total-value {
  font-weight: 600;
  font-size: 1.1rem;
  color: #4a69bd;
}

.item-count {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.empty-items {
  padding: 20px;
  text-align: center;
  color: #7f8c8d;
  font-style: italic;
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 50px 0;
  text-align: center;
}

.error-state h3 {
  margin-bottom: 10px;
}

.error-state p {
  margin-bottom: 20px;
  color: #7f8c8d;
}

.loading {
  color: #7f8c8d;
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

.form-group {
  margin-bottom: 15px;
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

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}
</style>
