<template>
  <div class="login-container">
    <div class="login-card">
      <h1>登錄</h1>
      <div v-if="authStore.error" class="alert alert-danger">
        {{ authStore.error }}
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="email">郵箱</label>
          <input 
            type="email" 
            id="email" 
            v-model="email" 
            class="form-control" 
            required 
            placeholder="輸入您的郵箱"
          />
        </div>
        
        <div class="form-group">
          <label for="password">密碼</label>
          <input 
            type="password" 
            id="password" 
            v-model="password" 
            class="form-control" 
            required 
            placeholder="輸入您的密碼"
          />
        </div>
        
        <button 
          type="submit" 
          class="btn login-btn" 
          :disabled="authStore.loading"
        >
          {{ authStore.loading ? '登錄中...' : '登錄' }}
        </button>
      </form>
      
      <div class="test-accounts">
        <h3>測試賬號</h3>
        <div class="account">
          <div><strong>普通用戶</strong></div>
          <div>郵箱：<span class="account-info" @click="setCredentials('test@example.com', 'password123')">test@example.com</span></div>
          <div>密码：<span class="account-info" @click="setPassword('password123')">password123</span></div>
        </div>
        
        <div class="account">
          <div><strong>管理员</strong></div>
          <div>邮箱：<span class="account-info" @click="setCredentials('admin@example.com', 'admin123')">admin@example.com</span></div>
          <div>密码：<span class="account-info" @click="setPassword('admin123')">admin123</span></div>
        </div>
        <p class="tip">点击邮箱或密码可快速填充</p>
      </div>
      
      <div class="register-link">
        还没有账号？ 
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')

const handleLogin = async () => {
  const success = await authStore.login(email.value, password.value)
  
  if (success) {
    // 重定向到请求的页面或默认为首页
    const redirectPath = route.query.redirect as string || '/'
    router.push(redirectPath)
  }
}

// 快速设置测试账号和密码
const setCredentials = (emailValue: string, passwordValue: string) => {
  email.value = emailValue
  password.value = passwordValue
}

// 只设置密码
const setPassword = (passwordValue: string) => {
  password.value = passwordValue
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 1rem;
}

.login-card {
  background: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  padding: 2rem;
  width: 100%;
  max-width: 400px;
}

.login-card h1 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.login-form {
  margin-bottom: 1rem;
}

.login-btn {
  width: 100%;
  margin-top: 1rem;
  padding: 0.75rem;
}

.register-link {
  text-align: center;
  margin-top: 1rem;
  font-size: 0.9rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.login-card {
  background: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  padding: 2rem;
  width: 100%;
  max-width: 450px;
}

.login-card h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: var(--primary-color);
}

.alert-danger {
  background-color: #ffebee;
  color: #c62828;
  padding: 0.75rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 1rem;
}

.login-btn {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  background-color: var(--primary-color);
  margin-bottom: 1rem;
}

.test-accounts {
  margin-top: 2rem;
  padding: 1rem;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.test-accounts h3 {
  font-size: 1.1rem;
  margin-bottom: 0.75rem;
  text-align: center;
}

.account {
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px dashed #ddd;
}

.account:last-child {
  margin-bottom: 0.5rem;
  border-bottom: none;
}

.account-info {
  color: var(--primary-color);
  cursor: pointer;
  font-weight: 500;
}

.account-info:hover {
  text-decoration: underline;
}

.tip {
  font-size: 0.8rem;
  color: var(--text-light);
  text-align: center;
  margin-top: 0.5rem;
}

.register-link {
  text-align: center;
  margin-top: 1.5rem;
}
</style>
