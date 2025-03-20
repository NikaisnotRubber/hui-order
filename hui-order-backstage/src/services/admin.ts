import api from './api'
//import axios from 'axios'

export interface OrderItem {
  id: number
  itemId: number
  orderId: number
  itemName: string
  price: number
  quantity: number
}

export interface Order {
  id: number
  userId: number
  orderStatus: string
  totalAmount: number
  createdAt: string
  updatedAt: string
  paymentMethod: string
  deliveryAddress: string
  items: OrderItem[]
}

export interface OrderFilter {
  status?: string
  startDate?: string
  endDate?: string
}

export interface SalesDataPoint {
  timeLabel: string
  totalSales: number
  orderCount: number
}

export interface TopProduct {
  id: number
  name: string
  quantity: number
  totalSales: number
}

export interface AnalyticsData {
  totalRevenue: number
  totalOrders: number
  averageOrderValue: number
  conversionRate: number
  salesData: SalesDataPoint[]
  topProducts: TopProduct[]
}

export interface StatusCount {
  [key: string]: number
}

export default {
  // 獲取訂單列表，支持篩選
  async getOrders(filter?: OrderFilter): Promise<Order[]> {
    const params: Record<string, any> = {}
    
    if (filter) {
      if (filter.status) params.status = filter.status
      if (filter.startDate) params.startDate = filter.startDate
      if (filter.endDate) params.endDate = filter.endDate
    }
    
    const response = await api.get(`/orders`, { params })
    return response.data
  },
  
  // 獲取訂單詳情
  async getOrderById(orderId: number): Promise<Order> {
    const response = await api.get(`/orders/${orderId}`)
    return response.data
  },
  
  // 更新訂單狀態
  async updateOrderStatus(orderId: number, status: string): Promise<Order> {
    const response = await api.put(`/orders/${orderId}/status`, { status })
    return response.data
  },
  
  // 獲取銷售分析數據
  async getSalesAnalytics(params?: Record<string, any>): Promise<AnalyticsData> {
    const requestParams: Record<string, any> = params || {}
    
    // 設置默認時間範圍
    if (!requestParams.startDate && !requestParams.endDate && !requestParams.timeRange) {
      requestParams.timeRange = 7
    }
    
    // 設置默認時間單位
    if (!requestParams.timeUnit) {
      requestParams.timeUnit = 'DAY'
    }
    
    const response = await api.get(`/analytics/sales`, { params: requestParams })
    return response.data
  },
  
  // 獲取訂單狀態計數
  async getOrderStatusCounts(): Promise<StatusCount> {
    const response = await api.get('/analytics/status-counts')
    return response.data
  }
}
