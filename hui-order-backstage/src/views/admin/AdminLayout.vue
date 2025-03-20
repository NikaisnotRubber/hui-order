<template>
  <div class="admin-layout">
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>管理系統</h2>
      </div>
      
      <nav class="sidebar-nav">
        <router-link to="/" class="nav-item">
          <span class="icon">📊</span>
          <span>儀表板</span>
        </router-link>
        
        <router-link to="/orders" class="nav-item">
          <span class="icon">📝</span>
          <span>訂單管理</span>
        </router-link>
        
        <router-link to="/analytics" class="nav-item">
          <span class="icon">📈</span>
          <span>數據分析</span>
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <div class="user-info">
          <span>{{ user?.name }}</span>
          <small>{{ user?.role }}</small>
        </div>
        
        <button class="logout-button" @click="handleLogout">
          登出
        </button>
      </div>
    </div>
    
    <div class="main-content">
      <header class="main-header">
        <h1>{{ currentPageTitle }}</h1>
      </header>
      
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const user = computed(() => authStore.user)

// 根據當前路由路徑計算頁面標題
const currentPageTitle = computed(() => {
  const routeName = route.name?.toString() || ''
  
  switch (routeName) {
    case 'Dashboard':
      return '系統概覽'
    case 'Orders':
      return '訂單管理'
    case 'OrderDetail':
      return `訂單詳情 #${route.params.id}`
    case 'Analytics':
      return '銷售分析'
    default:
      return '管理系統'
  }
})

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(async () => {
  // 確保用戶信息已加載
  if (!authStore.user) {
    await authStore.fetchCurrentUser()
  }
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background-color: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: all 0.3s;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.router-link-active {
  background-color: rgba(255, 255, 255, 0.15);
  color: white;
  border-left: 3px solid #3498db;
}

.icon {
  margin-right: 10px;
  font-size: 1.2rem;
}

.sidebar-footer {
  padding: 15px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-info {
  margin-bottom: 10px;
}

.user-info span {
  display: block;
  font-weight: 500;
}

.user-info small {
  color: rgba(255, 255, 255, 0.6);
}

.logout-button {
  width: 100%;
  padding: 8px;
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-button:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.main-header {
  padding: 20px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.main-header h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.content-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}
</style>
