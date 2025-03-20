import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import axios from 'axios'

// 設置 axios 基本 URL
axios.defaults.baseURL = import.meta.env.VITE_API_URL || ''

async function initializeApp() {
  console.log('Initializing app...')
  const app = createApp(App)
  const pinia = createPinia()

  app.use(pinia)
  app.use(router)

  // 初始化認證狀態
  const authStore = useAuthStore()
  await authStore.initialize()

  app.mount('#app')
}

initializeApp().catch(error => {
  console.error('Failed to initialize the app:', error)
})
