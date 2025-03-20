<template>
  <div class="register-container">
    <div class="register-card">
      <h1>管理員註冊</h1>
      
      <div v-if="error" class="error-message">
        {{ error }}
      </div>
      
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="name">姓名</label>
          <input 
            id="name"
            v-model="name"
            type="text"
            placeholder="輸入姓名"
            required
          />
        </div>
        
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
        
        <div class="form-group">
          <label for="confirmPassword">確認密碼</label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            placeholder="再次輸入密碼"
            required
          />
        </div>
        
        <button type="submit" class="register-button" :disabled="loading">
          {{ loading ? '註冊中...' : '註冊' }}
        </button>
        
        <div class="login-link">
          已有賬號? <router-link to="/login">登錄</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const error = ref('')

const handleRegister = async () => {
  // 驗證表單
  if (!name.value || !email.value || !password.value || !confirmPassword.value) {
    error.value = '請填寫所有字段'
    return
  }
  
  if (password.value !== confirmPassword.value) {
    error.value = '兩次密碼輸入不一致'
    return
  }
  
  loading.value = true
  error.value = ''
  
  try {
    const success = await authStore.register(name.value, email.value, password.value)
    
    if (success) {
      router.push('/')
    } else {
      error.value = authStore.error || '註冊失敗，請檢查您的註冊信息'
    }
  } catch (err) {
    console.error('Registration error:', err)
    error.value = '註冊過程中發生錯誤'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.register-card {
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

.register-button {
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

.register-button:hover {
  background-color: #3c58a8;
}

.register-button:disabled {
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

.login-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
}

.login-link a {
  color: #4a69bd;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
