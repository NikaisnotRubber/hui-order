export interface OrderItem {
  itemId: string
  itemName: string
  quantity: number
  price: number
}

export interface Order {
  id: string
  userId: string
  total: number
  items: OrderItem[]
  status: 'pending' | 'processing' | 'shipped' | 'delivered' | 'canceled'
  createdAt: string
  updatedAt: string
  shippingAddress: {
    name: string
    address: string
    city: string
  }
}
