import { User } from '../types/user'

// 预设的测试账号
const mockUsers = [
  {
    id: '1',
    name: '测试用户',
    email: 'test@example.com',
    // 密码: password123
    password: 'password123'
  },
  {
    id: '2',
    name: '管理员',
    email: 'admin@example.com',
    // 密码: admin123
    password: 'admin123'
  }
]

// 模拟 token 生成
const generateToken = (userId: string): string => {
  return `mock_token_${userId}_${Date.now()}`
}

// 模拟登录认证
export const mockLogin = (email: string, password: string): Promise<{ token: string, user: User } | null> => {
  return new Promise((resolve) => {
    // 模拟网络延迟
    setTimeout(() => {
      const user = mockUsers.find(u => u.email === email && u.password === password)
      
      if (user) {
        // 不返回密码字段给前端
        const { password: _, ...userWithoutPassword } = user
        const token = generateToken(user.id)
        
        resolve({
          token,
          user: userWithoutPassword as User
        })
      } else {
        resolve(null)
      }
    }, 500) // 模拟 500ms 延迟
  })
}

// 模拟获取当前用户信息
export const mockGetCurrentUser = (token: string): Promise<User | null> => {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 从 token 中提取用户 ID
      const match = token.match(/^mock_token_(\d+)_/)
      
      if (match) {
        const userId = match[1]
        const user = mockUsers.find(u => u.id === userId)
        
        if (user) {
          const { password: _, ...userWithoutPassword } = user
          resolve(userWithoutPassword as User)
        } else {
          resolve(null)
        }
      } else {
        resolve(null)
      }
    }, 300)
  })
}
