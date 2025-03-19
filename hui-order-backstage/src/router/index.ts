import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// Views
import Home from '../views/Home.vue'
import ItemList from '../views/ItemList.vue'
import Login from '../views/Login.vue'
import Register from '@/views/Register.vue'
import NotFound from '@/views/NotFound.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import Checkout from '@/views/Checkout.vue'
import cart from '@/views/cart.vue'
import ItemDetail from '@/views/ItemDetail.vue'
import Account from '@/views/Account.vue'
import Orders from '@/views/orders.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/items',
    name: 'ItemList',
    component: ItemList
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },

  {
    path: '/register',
    name: 'register',
    component: Register
  },
  {
    path: '/not-found',
    name: 'not-found',
    component: NotFound
  },
  {
    path: '/checkout',
    name: 'checkout',
    component: Checkout
  },
  {
    path: '/cart',
    name: 'cart',
    component: cart
  },
  {
    path: '/item-detail', 
    name: 'item-detail',
    component: ItemDetail
  },
  {
    path: '/account',
    name: 'account',
    component: Account,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/orders',
    name: 'orders',
    component: Orders,
    meta: {
      requiresAuth: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for protected routes
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
