import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import { useCartStore } from './stores/cart'
import './styles/main.css'

async function initializeApp() {
  console.log('Initializing app...')
  const app = createApp(App)
  const pinia = createPinia()

  app.use(pinia)
  app.use(router)

  const authStore = useAuthStore()
  const cartStore = useCartStore()

  await authStore.initializeAuth()

  cartStore.loadCart()

  app.mount('#app')
}

initializeApp().catch(error => {
  console.error('Failed to initialize the app:', error)
})
