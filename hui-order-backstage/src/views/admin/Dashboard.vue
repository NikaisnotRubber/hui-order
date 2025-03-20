<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ orderStats.totalOrders || 0 }}</div>
        <div class="stat-title">總訂單數</div>
      </div>
      
      <div class="stat-card">
        <div class="stat-value">{{ formatCurrency(orderStats.totalRevenue || 0) }}</div>
        <div class="stat-title">總收入</div>
      </div>
      
      <div class="stat-card">
        <div class="stat-value">{{ formatCurrency(orderStats.averageOrderValue || 0) }}</div>
        <div class="stat-title">平均訂單價值</div>
      </div>
    </div>
    
    <div class="row">
      <div class="col-8">
        <div class="card">
          <h3>近期銷售趨勢</h3>
          <div class="chart-container">
            <div v-if="loading" class="loading">載入中...</div>
            <div v-else-if="!salesData.length" class="no-data">暫無數據</div>
            <div v-else class="sales-chart">
              <!-- 圖表實際上放在這裡，這裡只簡單顯示數據 -->
              <div class="chart-table">
                <div class="chart-row header">
                  <div class="chart-cell">日期</div>
                  <div class="chart-cell">訂單數</div>
                  <div class="chart-cell">銷售額</div>
                </div>
                <div v-for="(item, index) in salesData.slice(0, 7)" :key="index" class="chart-row">
                  <div class="chart-cell">{{ item.timeLabel }}</div>
                  <div class="chart-cell">{{ item.orderCount }}</div>
                  <div class="chart-cell">{{ formatCurrency(item.totalSales) }}</div>
                </div>
              </div>
            </div>
          </div>
          <div class="chart-actions">
            <router-link to="/analytics" class="btn btn-primary">查看詳細分析</router-link>
          </div>
        </div>
      </div>
      
      <div class="col-4">
        <div class="card">
          <h3>訂單狀態分佈</h3>
          <div class="status-chart">
            <div v-if="loading" class="loading">載入中...</div>
            <div v-else-if="!orderStatusCounts" class="no-data">暫無數據</div>
            <div v-else>
              <div v-for="(count, status) in orderStatusCounts" :key="status" class="status-item">
                <div class="status-label">{{ translateStatus(status) }}</div>
                <div class="status-bar-container">
                  <div 
                    class="status-bar" 
                    :style="{ width: calculatePercentage(count) + '%', background: getStatusColor(status) }"
                  ></div>
                  <span class="status-count">{{ count }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="chart-actions">
            <router-link to="/orders" class="btn btn-primary">管理訂單</router-link>
          </div>
        </div>
      </div>
    </div>
    
    <div class="card mt-3">
      <h3>最近訂單</h3>
      <div class="recent-orders">
        <div v-if="loading" class="loading">載入中...</div>
        <div v-else-if="!recentOrders.length" class="no-data">暫無訂單</div>
        <table v-else class="orders-table">
          <thead>
            <tr>
              <th>訂單編號</th>
              <th>訂單日期</th>
              <th>客戶</th>
              <th>金額</th>
              <th>狀態</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in recentOrders" :key="order.id">
              <td>#{{ order.id }}</td>
              <td>{{ formatDate(order.createdAt) }}</td>
              <td>用戶 #{{ order.userId }}</td>
              <td>{{ formatCurrency(order.totalAmount) }}</td>
              <td>
                <span class="status-badge" :class="getStatusClass(order.orderStatus)">
                  {{ translateStatus(order.orderStatus) }}
                </span>
              </td>
              <td>
                <router-link :to="`/orders/${order.id}`" class="btn btn-sm btn-primary">
                  查看
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="card-actions">
        <router-link to="/orders" class="btn btn-secondary">查看全部訂單</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import adminService, { Order, SalesDataPoint } from '../../services/admin'

const loading = ref(true)
const recentOrders = ref<Order[]>([])
const salesData = ref<SalesDataPoint[]>([])
const orderStatusCounts = ref<Record<string, number>>({})
const orderStats = ref({
  totalOrders: 0,
  totalRevenue: 0,
  averageOrderValue: 0
})

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

// 計算百分比
const calculatePercentage = (count: number) => {
  const total = Object.values(orderStatusCounts.value).reduce((sum, count) => sum + count, 0)
  return total > 0 ? (count / total) * 100 : 0
}

// 獲取狀態的顏色
const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    'PENDING': '#f39c12',
    'PAID': '#3498db',
    'PROCESSING': '#9b59b6',
    'SHIPPED': '#2ecc71',
    'DELIVERED': '#27ae60',
    'CANCELLED': '#e74c3c'
  }
  return colors[status] || '#95a5a6'
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

// 載入數據
const loadData = async () => {
  loading.value = true
  
  try {
    // 獲取最近訂單
    const orders = await adminService.getOrders()
    recentOrders.value = orders.slice(0, 5)  // 只顯示最近5個訂單
    
    // 獲取銷售數據
    const analytics = await adminService.getSalesAnalytics()
    salesData.value = analytics.salesData
    
    // 更新訂單統計
    orderStats.value = {
      totalOrders: analytics.totalOrders,
      totalRevenue: analytics.totalRevenue,
      averageOrderValue: analytics.averageOrderValue
    }
    
    // 獲取訂單狀態分佈
    orderStatusCounts.value = await adminService.getOrderStatusCounts()
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard {
  padding: 15px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.stat-value {
  font-size: 2rem;
  font-weight: 600;
  color: #4a69bd;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 1rem;
  color: #7f8c8d;
}

.row {
  display: flex;
  margin: 0 -10px;
  margin-bottom: 20px;
}

.col-8 {
  width: 66.66%;
  padding: 0 10px;
}

.col-4 {
  width: 33.33%;
  padding: 0 10px;
}

.card {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.card h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 1.2rem;
  color: #2c3e50;
}

.chart-container, .status-chart {
  min-height: 200px;
}

.chart-actions, .card-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.status-item {
  margin-bottom: 15px;
}

.status-label {
  margin-bottom: 5px;
  font-weight: 500;
}

.status-bar-container {
  display: flex;
  align-items: center;
  height: 25px;
  background-color: #f1f2f6;
  border-radius: 4px;
  overflow: hidden;
}

.status-bar {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.status-count {
  margin-left: 10px;
  font-weight: 500;
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
  font-weight: 600;
  color: #2c3e50;
  background-color: #f8f9fa;
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

.btn-sm {
  padding: 5px 10px;
  font-size: 0.85rem;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #95a5a6;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #95a5a6;
  font-style: italic;
}

.chart-table {
  width: 100%;
  border-collapse: collapse;
}

.chart-row {
  display: flex;
  border-bottom: 1px solid #eee;
}

.chart-row.header {
  font-weight: 600;
  background-color: #f8f9fa;
}

.chart-cell {
  flex: 1;
  padding: 12px 15px;
  text-align: left;
}
</style>
