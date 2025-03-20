<template>
  <div class="analytics-page">
    <!-- 篩選和時間範圍選擇 -->
    <div class="filter-panel card">
      <h3>銷售分析</h3>
      
      <div class="filter-form">
        <div class="form-row">
          <div class="form-group">
            <label for="timeUnit">時間單位</label>
            <select id="timeUnit" v-model="timeUnit" class="form-control">
              <option value="DAY">按日</option>
              <option value="WEEK">按周</option>
              <option value="MONTH">按月</option>
            </select>
          </div>
          
          <div class="form-group">
            <label for="timeRange">時間範圍</label>
            <select id="timeRange" v-model="timeRange" class="form-control">
              <option value="7">最近7天/周/月</option>
              <option value="14">最近14天/周/月</option>
              <option value="30">最近30天/周/月</option>
              <option value="90">最近90天/周/月</option>
              <option value="custom">自定義</option>
            </select>
          </div>
          
          <template v-if="timeRange === 'custom'">
            <div class="form-group">
              <label for="startDate">開始日期</label>
              <input 
                id="startDate" 
                v-model="startDate" 
                type="date" 
                class="form-control"
              >
            </div>
            
            <div class="form-group">
              <label for="endDate">結束日期</label>
              <input 
                id="endDate" 
                v-model="endDate" 
                type="date" 
                class="form-control"
              >
            </div>
          </template>
        </div>
        
        <div class="filter-actions">
          <button class="btn btn-primary" @click="loadAnalyticsData">
            應用
          </button>
        </div>
      </div>
    </div>
    
    <!-- 銷售概況 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">🛒</div>
        <div class="stat-content">
          <div class="stat-value">{{ analyticsData.totalOrders || 0 }}</div>
          <div class="stat-title">總訂單數</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">💵</div>
        <div class="stat-content">
          <div class="stat-value">{{ formatCurrency(analyticsData.totalRevenue || 0) }}</div>
          <div class="stat-title">總收入</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📈</div>
        <div class="stat-content">
          <div class="stat-value">{{ formatCurrency(analyticsData.averageOrderValue || 0) }}</div>
          <div class="stat-title">平均訂單價值</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🔄</div>
        <div class="stat-content">
          <div class="stat-value">{{ analyticsData.conversionRate ? (analyticsData.conversionRate * 100).toFixed(2) + '%' : '0%' }}</div>
          <div class="stat-title">轉化率</div>
        </div>
      </div>
    </div>
    
    <!-- 銷售趨勢圖表 -->
    <div class="card">
      <div class="card-header">
        <h3>銷售趨勢</h3>
        <div class="time-unit-display">{{ getTimeUnitDisplay() }}</div>
      </div>
      
      <div class="card-body">
        <div v-if="loading" class="loading-container">
          <div class="loading">載入中...</div>
        </div>
        
        <div v-else-if="!analyticsData.salesData || analyticsData.salesData.length === 0" class="empty-chart">
          <div class="empty-icon">📊</div>
          <h4>暫無數據</h4>
          <p>所選時間範圍內沒有銷售數據</p>
        </div>
        
        <div v-else class="chart-container">
          <!-- 簡易圖表實現，實際項目中可使用Chart.js等庫 -->
          <div class="sales-chart">
            <div class="chart-grid">
              <div 
                v-for="(item, index) in analyticsData.salesData" 
                :key="index" 
                class="chart-bar-container"
              >
                <div class="chart-bar-wrapper">
                  <div 
                    class="chart-bar" 
                    :style="{ height: calculateBarHeight(item.totalSales) + '%' }"
                    :title="`${item.timeLabel}: ${formatCurrency(item.totalSales)}`"
                  ></div>
                </div>
                <div class="chart-label">{{ formatChartLabel(item.timeLabel) }}</div>
              </div>
            </div>
          </div>
          
          <div class="chart-legend">
            <div class="legend-item">
              <div class="legend-color" style="background-color: #4a69bd;"></div>
              <div class="legend-label">銷售額</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 熱門商品 -->
    <div class="card">
      <div class="card-header">
        <h3>熱門商品</h3>
      </div>
      
      <div class="card-body">
        <div v-if="loading" class="loading-container">
          <div class="loading">載入中...</div>
        </div>
        
        <div v-else-if="!analyticsData.topProducts || analyticsData.topProducts.length === 0" class="empty-chart">
          <div class="empty-icon">🏆</div>
          <h4>暫無數據</h4>
          <p>所選時間範圍內沒有銷售數據</p>
        </div>
        
        <div v-else class="top-products">
          <table class="products-table">
            <thead>
              <tr>
                <th>排名</th>
                <th>商品名稱</th>
                <th>銷售數量</th>
                <th>銷售額</th>
                <th>佔比</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(product, index) in analyticsData.topProducts" :key="product.id">
                <td>{{ index + 1 }}</td>
                <td>{{ product.name }}</td>
                <td>{{ product.quantity }}</td>
                <td>{{ formatCurrency(product.totalSales) }}</td>
                <td>
                  <div class="percentage-bar-container">
                    <div 
                      class="percentage-bar" 
                      :style="{ width: calculateProductPercentage(product.totalSales) + '%' }"
                    ></div>
                    <span>{{ calculateProductPercentage(product.totalSales).toFixed(1) }}%</span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    
    <!-- 詳細數據表格 -->
    <div class="card">
      <div class="card-header">
        <h3>詳細銷售數據</h3>
        <button class="btn btn-sm btn-secondary" @click="exportToCsv">
          導出為CSV
        </button>
      </div>
      
      <div class="card-body">
        <div v-if="loading" class="loading-container">
          <div class="loading">載入中...</div>
        </div>
        
        <div v-else-if="!analyticsData.salesData || analyticsData.salesData.length === 0" class="empty-chart">
          <div class="empty-icon">📋</div>
          <h4>暫無數據</h4>
          <p>所選時間範圍內沒有銷售數據</p>
        </div>
        
        <div v-else class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>時間</th>
                <th>訂單數</th>
                <th>銷售額</th>
                <th>平均訂單價值</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in analyticsData.salesData" :key="index">
                <td>{{ item.timeLabel }}</td>
                <td>{{ item.orderCount }}</td>
                <td>{{ formatCurrency(item.totalSales) }}</td>
                <td>{{ item.orderCount ? formatCurrency(item.totalSales / item.orderCount) : formatCurrency(0) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import adminService, { AnalyticsData } from '../../services/admin'

// 篩選參數
const timeUnit = ref('DAY')
const timeRange = ref('7')
const startDate = ref('')
const endDate = ref('')

// 數據狀態
const loading = ref(true)
const analyticsData = ref<AnalyticsData>({
  totalOrders: 0,
  totalRevenue: 0,
  averageOrderValue: 0,
  conversionRate: 0,
  salesData: [],
  topProducts: []
})

// 格式化金額為貨幣形式
const formatCurrency = (amount: number) => {
  return '¥' + amount.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 獲取時間單位的顯示文本
const getTimeUnitDisplay = () => {
  const units = {
    'DAY': '日',
    'WEEK': '周',
    'MONTH': '月'
  } as Record<string, string>
  
  const ranges = {
    '7': '最近7',
    '14': '最近14',
    '30': '最近30',
    '90': '最近90',
    'custom': '自定義範圍'
  } as Record<string, string>
  
  if (timeRange.value === 'custom') {
    return `${startDate.value} 至 ${endDate.value}（按${units[timeUnit.value]}）`
  }
  
  return `${ranges[timeRange.value]}${units[timeUnit.value]}`
}

// 計算柱狀圖高度百分比
const calculateBarHeight = (value: number) => {
  if (!analyticsData.value.salesData || analyticsData.value.salesData.length === 0) return 0
  
  const maxValue = Math.max(...analyticsData.value.salesData.map(item => item.totalSales))
  if (maxValue === 0) return 0
  
  return (value / maxValue) * 100
}

// 計算產品銷售佔比
const calculateProductPercentage = (sales: number) => {
  if (!analyticsData.value.totalRevenue || analyticsData.value.totalRevenue === 0) return 0
  return (sales / analyticsData.value.totalRevenue) * 100
}

// 格式化圖表標籤
const formatChartLabel = (label: string) => {
  if (timeUnit.value === 'DAY') {
    // 格式化日期標籤，例如 "2023-05-15" -> "05-15"
    const parts = label.split('-')
    if (parts.length >= 3) {
      return `${parts[1]}-${parts[2]}`
    }
  }
  return label
}

// 載入分析數據
const loadAnalyticsData = async () => {
  loading.value = true
  
  const params: Record<string, any> = {
    timeUnit: timeUnit.value
  }
  
  if (timeRange.value === 'custom') {
    if (startDate.value && endDate.value) {
      params.startDate = startDate.value
      params.endDate = endDate.value
    } else {
      alert('請選擇開始和結束日期')
      loading.value = false
      return
    }
  } else {
    params.timeRange = Number(timeRange.value)
  }
  
  try {
    analyticsData.value = await adminService.getSalesAnalytics(params)
  } catch (error) {
    console.error('Failed to load analytics data:', error)
    alert('載入分析數據失敗，請重試')
  } finally {
    loading.value = false
  }
}

// 導出數據為CSV
const exportToCsv = () => {
  if (!analyticsData.value.salesData || analyticsData.value.salesData.length === 0) {
    alert('沒有數據可導出')
    return
  }
  
  const headers = ['時間', '訂單數', '銷售額', '平均訂單價值']
  
  const rows = analyticsData.value.salesData.map(item => {
    const avgValue = item.orderCount ? item.totalSales / item.orderCount : 0
    return [
      item.timeLabel,
      item.orderCount,
      item.totalSales.toFixed(2),
      avgValue.toFixed(2)
    ]
  })
  
  // 生成CSV內容
  const csvContent = [
    headers.join(','),
    ...rows.map(row => row.join(','))
  ].join('\n')
  
  // 創建下載鏈接
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.setAttribute('href', url)
  link.setAttribute('download', `sales_analytics_${timeUnit.value}_${new Date().toISOString().split('T')[0]}.csv`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

onMounted(() => {
  // 初始化日期範圍
  const today = new Date()
  const sevenDaysAgo = new Date()
  sevenDaysAgo.setDate(today.getDate() - 7)
  
  startDate.value = sevenDaysAgo.toISOString().split('T')[0]
  endDate.value = today.toISOString().split('T')[0]
  
  loadAnalyticsData()
})
</script>

<style scoped>
.analytics-page {
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

.card-body {
  padding: 20px;
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
  flex-wrap: wrap;
  margin-bottom: 15px;
}

.form-group {
  flex: 1;
  min-width: 200px;
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
  justify-content: flex-end;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 2rem;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 600;
  color: #4a69bd;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 1rem;
  color: #7f8c8d;
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

.empty-chart {
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

.empty-chart h4 {
  margin: 0 0 10px;
  font-size: 1.2rem;
}

.empty-chart p {
  margin: 0;
  color: #999;
}

.chart-container {
  position: relative;
  width: 100%;
  min-height: 300px;
}

.sales-chart {
  width: 100%;
  height: 300px;
  padding: 20px 0;
}

.chart-grid {
  display: flex;
  height: 100%;
  justify-content: space-between;
  align-items: flex-end;
}

.chart-bar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  height: 100%;
}

.chart-bar-wrapper {
  width: 80%;
  height: 90%;
  display: flex;
  align-items: flex-end;
}

.chart-bar {
  width: 100%;
  background-color: #4a69bd;
  border-radius: 3px 3px 0 0;
  transition: height 0.3s ease;
}

.chart-label {
  margin-top: 8px;
  font-size: 0.8rem;
  color: #7f8c8d;
  text-align: center;
}

.chart-legend {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  margin: 0 10px;
}

.legend-color {
  width: 15px;
  height: 15px;
  border-radius: 3px;
  margin-right: 8px;
}

.time-unit-display {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.products-table {
  width: 100%;
  border-collapse: collapse;
}

.products-table th,
.products-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.products-table th {
  background-color: #f8f9fa;
  font-weight: 600;
}

.percentage-bar-container {
  position: relative;
  width: 100%;
  height: 18px;
  background-color: #f1f2f6;
  border-radius: 9px;
  overflow: hidden;
  display: flex;
  align-items: center;
}

.percentage-bar {
  height: 100%;
  background-color: #4a69bd;
  border-radius: 9px 0 0 9px;
}

.percentage-bar-container span {
  position: absolute;
  right: 10px;
  font-size: 0.8rem;
  font-weight: 500;
  color: #333;
}

.table-responsive {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.data-table th {
  background-color: #f8f9fa;
  font-weight: 600;
}

.btn-sm {
  padding: 5px 10px;
  font-size: 0.85rem;
}
</style>
