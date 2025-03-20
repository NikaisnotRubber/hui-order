import api from './api'

// 用戶接口定義
export interface User {
  id: number
  name: string
  email: string
  role: string
}

// 登錄請求參數
export interface LoginRequest {
  email: string
  password: string
  role: string
}

// 註冊請求參數
export interface RegisterRequest {
  name: string
  email: string
  password: string
  role: string
}

// 登錄和註冊響應
export interface AuthResponse {
  token: string
  user: User
}

/**
 * 認證服務類，提供登錄、註冊和獲取用戶資料等功能
 */
class AuthService {

  /**
   * 用戶登錄
   * @param credentials 登錄憑證（電子郵件和密碼）
   * @returns 包含令牌和用戶信息的響應
   */
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await api.post('admin/login', credentials)
    return response.data
  }

  /**
   * 註冊新用戶
   * @param userData 用戶註冊數據（姓名、電子郵件、密碼和角色）
   * @returns 包含令牌和用戶信息的響應
   */
  async register(userData: RegisterRequest): Promise<AuthResponse> {
    // 從管理後台註冊的用戶自動設置為 ADMIN 角色
    userData.role = 'ADMIN';
    const response = await api.post(`admin/register`, userData)
    return response.data
  }

  /**
   * 獲取當前登錄用戶的資料
   * @returns 用戶信息
   */
  async getCurrentUser(): Promise<User> {
    const response = await api.get(`/profile`)
    return response.data
  }

  /**
   * 登出用戶（清除本地儲存和授權頭）
   */
  logout(): void {
    localStorage.removeItem('token')
    delete api.defaults.headers.common['Authorization']
  }

  /**
   * 設置認證令牌到 api 默認頭
   * @param token JWT令牌
   */
  setAuthToken(token: string): void {
    if (token) {
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
    } else {
      delete api.defaults.headers.common['Authorization']
    }
  }
}

export default new AuthService()
