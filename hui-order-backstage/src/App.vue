<template>
  <div class="app-container">
    <header>
      <nav>
        <div class="logo">
          <router-link to="/">Hui Order</router-link>
        </div>
        <div class="nav-links">
          <router-link to="/cart" class="cart-link">
            <!-- 購物車圖標 -->
            <svg class="cart-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="9" cy="21" r="1"></circle>
              <circle cx="20" cy="21" r="1"></circle>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
            </svg>
            <span>購物車</span>
            <span v-if="cartItemCount > 0" class="cart-badge">{{ cartItemCount }}</span>
          </router-link>
         


          <div v-if="isLoggedIn" class="auth-controls">
            <router-link to="/account" class="user-link">
              <svg class="user-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
              <span>我的帳戶</span>
            </router-link>
            <router-link to="/orders" class="user-link">
              <svg class="user-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
                <line x1="8" y1="21" x2="16" y2="21"></line>
                <line x1="12" y1="17" x2="12" y2="21"></line>
              </svg>
              <span>我的訂單</span>
            </router-link>
            <button @click="logout" class="logout-btn">
              <svg class="logout-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                <polyline points="16 17 21 12 16 7"></polyline>
                <line x1="21" y1="12" x2="9" y2="12"></line>
              </svg>
              <span>登出</span>
            </button>
          </div>
          <router-link v-else to="/login" class="login-btn">
            <svg class="login-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path>
              <polyline points="10 17 15 12 10 7"></polyline>
              <line x1="15" y1="12" x2="3" y2="12"></line>
            </svg>
            <span>登入</span>
          </router-link>
        </div>
      </nav>
    </header>
    
    <main>
      <router-view />
    </main>
    
    <div class="about-us-container">
      <Footer class="app-footer" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { useCartStore } from './stores/cart'
import Footer from "./components/footer.vue";

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const isLoggedIn = computed(() => authStore.isAuthenticated)
const currentYear = new Date().getFullYear()

// 購物車商品總數
const cartItemCount = computed(() => cartStore.totalItems)

// 在組件掛載時載入購物車數據
onMounted(() => {
  cartStore.loadCart()
})

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

header {
  background-color: #333;
  color: white;
  padding: 1rem;
}

nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.logo a {
  font-size: 1.5rem;
  font-weight: bold;
  color: white;
  text-decoration: none;
}

.nav-links {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.nav-links a {
  color: white;
  text-decoration: none;
}

.nav-links a:hover {
  text-decoration: underline;
}

.auth-controls {
  display: flex;
  gap: 10px;
  align-items: center;
}

.user-link,
.login-btn, 
.logout-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  text-decoration: none;
  transition: all 0.2s ease;
}

.user-link {
  background-color: rgba(255, 255, 255, 0.1);
}

.login-btn {
  background-color: #4CAF50;
}

.logout-btn {
  background-color: rgba(244, 67, 54, 0.85);
}

.user-link:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.login-btn:hover {
  background-color: #45a049;
}

.logout-btn:hover {
  background-color: #f44336;
}

.user-icon,
.login-icon,
.logout-icon {
  color: white;
}

main {
  flex: 1;
  max-width: 1080px;
  margin: 0 auto;
  padding: 2rem 1rem;
  width: 100%;
}

/* 新的Footer元件樣式 */
.about-us-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
  width: 300px;
}

.app-footer {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  overflow: hidden;
}

/* 覆蓋footer.vue中的樣式，讓它成為懸浮按鈕樣式 */
.app-footer .footer-container {
  position: relative !important;
  box-shadow: none !important;
  border-radius: 8px;
}

.app-footer .footer-handle {
  border-radius: 8px;
}

/* 當展開時的樣式 */
.app-footer .footer-container.expanded {
  border-radius: 8px 8px 0 0;
}

.app-footer .footer-container.expanded .footer-content {
  max-height: 380px;
  overflow-y: auto;
}

/* 響應式樣式 */
@media (max-width: 768px) {
  .about-us-container {
    width: 100%;
    left: 0;
    bottom: 0;
    right: 0;
  }
  
  .app-footer,
  .app-footer .footer-container,
  .app-footer .footer-handle {
    border-radius: 0;
  }
}

/* 主要內容區增加底部邊距，以防止被底部寬出Footer長時擋住 */
main {
  margin-bottom: 60px;
}

/* 購物車鏈接樣式 */
.cart-link {
  position: relative;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 10px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.cart-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.cart-icon {
  color: white;
  margin-right: 3px;
}

/* 購物車徽章樣式 */
.cart-badge {
  position: absolute;
  top: -8px;
  right: -10px;
  background-color: #ff4757;
  color: white;
  font-size: 0.75rem;
  font-weight: bold;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}
</style>
