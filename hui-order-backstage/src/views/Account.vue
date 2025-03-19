<template>
  <div class="account-container">
    <h1>我的帳戶</h1>
    
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>載入中...</p>
    </div>
    
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button @click="resetForm" class="btn-secondary">重試</button>
    </div>
    
    <form v-else @submit.prevent="updateAccount" class="account-form">
      <div class="form-group">
        <label for="name">姓名</label>
        <input 
          type="text" 
          id="name" 
          v-model="form.name" 
          required
          class="form-input"
        >
        <p v-if="errors.name" class="error-text">{{ errors.name }}</p>
      </div>
      
      <div class="form-group">
        <label for="email">電子郵件</label>
        <input 
          type="email" 
          id="email" 
          v-model="form.email" 
          required
          class="form-input"
          disabled
        >
        <p class="help-text">電子郵件地址不可更改</p>
      </div>
      
      <div class="form-group">
        <label for="password">新密碼 (選填)</label>
        <input 
          type="password" 
          id="password" 
          v-model="form.password"
          class="form-input"
          placeholder="留空表示不更改密碼"
        >
        <p v-if="errors.password" class="error-text">{{ errors.password }}</p>
      </div>
      
      <div class="form-group">
        <label for="confirmPassword">確認新密碼</label>
        <input 
          type="password" 
          id="confirmPassword" 
          v-model="form.confirmPassword"
          class="form-input"
          :disabled="!form.password"
        >
        <p v-if="errors.confirmPassword" class="error-text">{{ errors.confirmPassword }}</p>
      </div>
      
      <div v-if="updateSuccess" class="success-message">
        帳戶資訊已更新成功！
      </div>
      
      <div class="form-actions">
        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? '更新中...' : '更新帳戶資訊' }}
        </button>
        <button type="button" @click="resetForm" class="btn-secondary">
          重設
        </button>
      </div>
    </form>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

// 表單數據
const form = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// 狀態變數
const loading = ref(false)
const error = ref('')
const updateSuccess = ref(false)
const errors = reactive({
  name: '',
  password: '',
  confirmPassword: ''
})

// 計算屬性：檢查用戶是否已經登入
const isLoggedIn = computed(() => authStore.isAuthenticated)

// 重置表單錯誤
const resetErrors = () => {
  errors.name = ''
  errors.password = ''
  errors.confirmPassword = ''
  error.value = ''
}

// 初始化表單數據
const initFormData = () => {
  if (authStore.user) {
    form.name = authStore.user.name
    form.email = authStore.user.email
    form.password = ''
    form.confirmPassword = ''
  }
}

// 重置表單
const resetForm = () => {
  resetErrors()
  initFormData()
  updateSuccess.value = false
}

// 驗證表單
const validateForm = (): boolean => {
  resetErrors()
  let isValid = true
  
  if (!form.name.trim()) {
    errors.name = '姓名不能為空'
    isValid = false
  }
  
  if (form.password) {
    if (form.password.length < 6) {
      errors.password = '密碼長度必須至少為6個字符'
      isValid = false
    }
    
    if (form.password !== form.confirmPassword) {
      errors.confirmPassword = '兩次輸入的密碼不一致'
      isValid = false
    }
  }
  
  return isValid
}

// 更新帳戶資訊
const updateAccount = async () => {
  // 檢查是否已登入
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  
  // 表單驗證
  if (!validateForm()) {
    return
  }
  
  loading.value = true
  updateSuccess.value = false
  
  try {
    // 準備更新數據
    const updateData = {
      name: form.name
    }
    
    // 如果用戶輸入了新密碼，將其加入更新數據
    if (form.password) {
      Object.assign(updateData, { password: form.password })
    }
    
    // 呼叫authStore的updateAccount方法
    const result = await authStore.updateAccount(updateData)
    
    if (result.success) {
      // 顯示成功消息
      updateSuccess.value = true
    } else {
      // 如果更新失敗，顯示錯誤
      error.value = result.error || '更新帳戶資訊時發生錯誤'
    }
    
  } catch (err: any) {
    console.error('更新帳戶資訊錯誤:', err)
    error.value = '更新帳戶資訊時發生錯誤'
  } finally {
    loading.value = false
  }
}

// 組件掛載時初始化
onMounted(() => {
  // 檢查用戶是否已登入
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  
  initFormData()
})
</script>

<style scoped>
.account-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h1 {
  margin-bottom: 2rem;
  color: #333;
  font-size: 1.8rem;
  text-align: center;
}

.account-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

label {
  font-weight: bold;
  color: #555;
}

.form-input {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-input:focus {
  border-color: #4CAF50;
  outline: none;
}

.form-input:disabled {
  background-color: #f9f9f9;
  cursor: not-allowed;
}

.help-text {
  font-size: 0.85rem;
  color: #777;
  margin-top: 0.25rem;
}

.error-text {
  color: #f44336;
  font-size: 0.85rem;
  margin-top: 0.25rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.btn-primary {
  padding: 0.75rem 1.5rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
  flex: 1;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-primary:disabled {
  background-color: #a5d6a7;
  cursor: not-allowed;
}

.btn-secondary {
  padding: 0.75rem 1.5rem;
  background-color: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 2rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #4CAF50;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-message {
  padding: 1rem;
  background-color: #ffebee;
  border: 1px solid #ffcdd2;
  border-radius: 4px;
  color: #c62828;
  margin-bottom: 1.5rem;
}

.success-message {
  padding: 1rem;
  background-color: #e8f5e9;
  border: 1px solid #c8e6c9;
  border-radius: 4px;
  color: #2e7d32;
  margin-bottom: 1.5rem;
  text-align: center;
}
</style>
