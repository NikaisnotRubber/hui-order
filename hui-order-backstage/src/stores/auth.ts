import { defineStore } from 'pinia'
import authService, { User } from '../services/auth'

interface AuthState {
  user: User | null
  token: string | null
  loading: boolean
  error: string | null
}

export const useAuthStore = defineStore({
  id: 'auth',
  
  state: (): AuthState => ({
    user: null,
    token: localStorage.getItem('token'),
    loading: false,
    error: null
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ADMIN'
  },
  
  actions: {
    async login(email: string, password: string) {
      this.loading = true
      this.error = null
      
      try {
        const response = await authService.login({ email, password })
        
        this.token = response.token
        this.user = response.user
        
        localStorage.setItem('token', response.token)
        
        // 設置 axios 請求頭中的認證信息
        authService.setAuthToken(response.token)
        
        return true
      } catch (error: any) {
        this.error = error.response?.data?.message || '登錄失敗'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async register(name: string, email: string, password: string) {
      this.loading = true
      this.error = null
      
      try {
        const response = await authService.register({
          name,
          email,
          password,
          role: 'ADMIN' // 設置為管理員角色
        })
        
        this.token = response.token
        this.user = response.user
        
        localStorage.setItem('token', response.token)
        
        // 設置 axios 請求頭中的認證信息
        authService.setAuthToken(response.token)
        
        return true
      } catch (error: any) {
        this.error = error.response?.data?.message || '註冊失敗'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async fetchCurrentUser() {
      if (!this.token) return
      
      try {
        this.user = await authService.getCurrentUser()
        return this.user
      } catch (error) {
        console.error('無法獲取用戶資料:', error)
        this.logout()
      }
    },
    
    logout() {
      this.user = null
      this.token = null
      authService.logout() // 這會清除localStorage和認證頭
    },
    
    async initialize() {
      if (this.token) {
        authService.setAuthToken(this.token)
        await this.fetchCurrentUser()
      }
    }
  }
})
