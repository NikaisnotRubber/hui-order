import { defineStore } from 'pinia'
import { User } from '../types/user'
import api from '../services/api'
//test mock
//import { mockLogin, mockGetCurrentUser } from '../services/mockAuth'
import { useCartStore } from './cart'
import { auth } from '../services/auth'

interface AuthState {
  user: User | null
  token: string | null
  loading: boolean
  error: string | null
}

interface LoginResponse {
  token: string
  user: User
}

interface RegisterResponse {
  token: string
  user: User
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: null,
    token: localStorage.getItem('token'),
    loading: false,
    error: null
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token && !!state.user
  },
  
  actions: {
    async login(email: string, password: string) {
      this.loading = true
      this.error = null
      
      try {
        console.log('登錄嘗試:', email)
        //const result = await mockLogin(email, password)
        const result = await auth.login(email, password)

        if (!result) {
          this.error = '登錄失敗：郵箱或密碼不正確'
          return false
        }
        
        const { token, user } = result
        
        this.user = user
        this.token = token
        
        localStorage.setItem('token', token)
        localStorage.setItem('user', JSON.stringify(user))
        
        return true
      } catch (error: any) {
        console.error('登錄錯誤:', error)
        this.error = error.message || '發生意外錯誤'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async register(name: string, email: string, password: string) {
      this.loading = true
      this.error = null
      
      try {
        // 使用服務中的 register 方法，而不是直接呼叫 API
        const result = await auth.register(name, email, password)
        
        const { token, user } = result
        
        this.user = user
        this.token = token
        
        localStorage.setItem('token', token)
        localStorage.setItem('user', JSON.stringify(user))
        
        return true
      } catch (error: any) {
        console.error('Registration error:', error)
        
        if (error.response) {
          this.error = error.response.data.message || 'Registration failed'
        } else if (error.request) {
          this.error = 'No response from server. Please try again.'
        } else {
          this.error = error.message || 'An unexpected error occurred'
        }
        
        return false
      } finally {
        this.loading = false
      }
    },
    
    async logout() {
      // Clear auth state
      this.user = null
      this.token = null
      
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      
      // 清空購物車
      const cartStore = useCartStore()
      cartStore.clearCart()
      
      console.log('使用者已登出，購物車已清空')
    },
    
    async getCurrentUser() {
      if (!this.token) return false
      
      try {
        // 使用模擬服務獲取使用者資訊
        //const user = await mockGetCurrentUser(this.token)
        const user = await auth.getCurrentUser()

        if (!user) {
          this.logout()
          return false
        }
        
        this.user = user
        return true
      } catch (error) {
        console.error('獲取當前使用者錯誤:', error)
        this.logout()
        return false
      }
    },
    
    async updateAccount(updateData: { name?: string, password?: string }) {
      if (!this.token || !this.user) return { success: false, error: '未登入' }
      
      this.loading = true
      this.error = null
      
      try {
        // 在實際應用中，這裡應該呼叫API來更新用戶資訊
        // 目前使用模擬的方式進行更新
        
        // 延遲以模擬網絡請求
        //await new Promise(resolve => setTimeout(resolve, 800))
        
        // // 更新用戶資訊
        // if (updateData.name) {
        //   this.user.name = updateData.name
        // }
        
        // // 密碼更新邏輯
        // if (updateData.password) {
        //   console.log('模擬密碼更新成功')
        //   // 實際應用中，應該呼叫API來更新密碼
        // }
        const result = await auth.updateAccount(updateData)
        
        if (result.success && updateData.name) {
          this.user.name = updateData.name
          localStorage.setItem('user', JSON.stringify(this.user))
        }
                
        return { success: true }
      } catch (error: any) {
        console.error('更新帳戶資訊錯誤:', error)
        this.error = '更新帳戶資訊時發生錯誤'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },
    
    async initializeAuth() {
      // Check if we have a token in localStorage
      const token = localStorage.getItem('token')
      
      if (token) {
        this.token = token
        
        // Try to get the current user with the token
        try {
          await this.getCurrentUser()
        } catch (error) {
          console.error('初始化認證錯誤:', error)
          this.logout()
        }
      }
    }
  }
})
