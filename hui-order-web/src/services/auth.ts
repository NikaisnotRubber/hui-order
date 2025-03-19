import api from './api'
import { User } from '@/types/user'

interface AuthResponse {
  token: string
  user: User
}

export const auth = {
  // Login user and get token
  async login(email: string, password: string): Promise<AuthResponse> {
    const response = await api.post('/auth/login', { email, password })
    return response.data
  },
  
  // Register new user
  async register(name: string, email: string, password: string): Promise<AuthResponse> {
    const response = await api.post('/auth/register', { name, email, password })
    console.log(response.data)
    return response.data
  },
  
  // Get current user information
  async getCurrentUser(): Promise<User> {
    const response = await api.get('/auth/me')
    return response.data
  },
  
  // Update user account information
  async updateAccount(updateData: { name?: string, password?: string }): Promise<{ success: boolean }> {
    const response = await api.put('/auth/update', updateData)
    return response.data
  }
}

export default auth