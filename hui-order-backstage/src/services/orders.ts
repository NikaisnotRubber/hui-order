import api from './api'
import { Order, OrderItem } from '@/types/order'

export interface ShippingInfo {
  name: string,
  address: string,
  city: string
}

interface OrderItem {
  id?: number;
  itemId: string;
  itemName: string;
  quantity: number;
  price: number;
}

interface OrderRequest {
  items: OrderItem[];
  shippingInfo: ShippingInfo;
  total: number;
}

export const orderService = {
  // Get all orders for the current user
  async getOrders(): Promise<Order[]> {
    const response = await api.get('/api/orders')
    return response.data
  },
  // Get order IDs by user ID
  async getOrderIdsByUserId(userId: number): Promise<number[]> {
    const response = await api.get(`/api/orders/user/${userId}`)
    return response.data
  },
  // 在 orderService 中添加 createOrder 方法
  async createOrder(orderData: OrderRequest): Promise<Order> {
    try {
      // 構建請求體，確保包含訂單總價
      const orderPayload = {
        items: orderData.items.map(item => ({
          itemId: item.itemId,
          itemName: item.itemName,
          quantity: item.quantity,
          price: item.price
        })),
        shippingInfo: {
          name: orderData.shippingInfo.name,
          address: orderData.shippingInfo.address,
          city: orderData.shippingInfo.city
        },
        total: orderData.total // 明確包含總價信息
      };

      const response = await api.post('/api/orders/create', orderPayload);
      return response.data;
    } catch (error) {
      console.error('Error creating order:', error);
      throw error;
    }
  },
  // Get a specific order by ID
  async getOrderById(orderId: string): Promise<Order> {
    const response = await api.get(`/api/orders/${orderId}`)
    return response.data
  },
  // Cancel an order
  async cancelOrder(orderId: string): Promise<boolean> {
    const response = await api.put(`/api/orders/${orderId}/cancel`)
    return response.data.success
  }
}

export default orderService