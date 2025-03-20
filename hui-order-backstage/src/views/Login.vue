<template>
  <div class="login-container">
    <div class="login-card">
      <h1>管理員登錄</h1>
      
      <div v-if="error" class="error-message">
        {{ error }}
      </div>
      
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">電子郵件</label>
          <input 
            id="email"
            v-model="email"
            type="email"
            placeholder="輸入電子郵件"
            required
          />
        </div>
        
        <div class="form-group">
          <label for="password">密碼</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="輸入密碼"
            required
          />
        </div>
        
        <button type="submit" class="login-button" :disabled="loading">
          {{ loading ? '登錄中...' : '登錄' }}
        </button>
        
        <div class="register-link">
          沒有賬號? <router-link to="/register">註冊</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  if (!email.value || !password.value) {
    error.value = '請輸入電子郵件和密碼'
    return
  }
  
  loading.value = true
  error.value = ''
  
  try {
    const success = await authStore.login(email.value, password.value)
    
    if (success) {
      // 獲取重定向的 URL 或導航到默認頁面
      const redirectPath = route.query.redirect as string || '/'
      router.push(redirectPath)
    } else {
      error.value = authStore.error || '登錄失敗，請檢查您的憑據'
    }
  } catch (err) {
    console.error('Login error:', err)
    error.value = '登錄過程中發生錯誤'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 16px;
  transition: all 0.3s;
}

input:focus {
  border-color: #4a69bd;
  outline: none;
  box-shadow: 0 0 0 2px rgba(74, 105, 189, 0.2);
}

.login-button {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 4px;
  background-color: #4a69bd;
  color: white;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.login-button:hover {
  background-color: #3c58a8;
}

.login-button:disabled {
  background-color: #a5b1c2;
  cursor: not-allowed;
}

.error-message {
  margin-bottom: 16px;
  padding: 10px;
  background-color: #ffebee;
  color: #e53935;
  border-radius: 4px;
  font-size: 14px;
}

.register-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
}

.register-link a {
  color: #4a69bd;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
