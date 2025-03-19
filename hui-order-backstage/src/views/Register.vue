<template>
  <div class="register-container">
    <div class="register-card">
      <h1>Create Account</h1>
      <div v-if="authStore.error" class="alert alert-danger">
        {{ authStore.error }}
      </div>
      
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label for="name">Full Name</label>
          <input 
            type="text" 
            id="name" 
            v-model="name" 
            class="form-control" 
            required 
            placeholder="Enter your full name"
          />
        </div>
        
        <div class="form-group">
          <label for="email">Email</label>
          <input 
            type="email" 
            id="email" 
            v-model="email" 
            class="form-control" 
            required 
            placeholder="Enter your email"
          />
        </div>
        
        <div class="form-group">
          <label for="password">Password</label>
          <input 
            type="password" 
            id="password" 
            v-model="password" 
            class="form-control" 
            required 
            placeholder="Enter your password"
            minlength="6"
          />
        </div>
        
        <div class="form-group">
          <label for="confirmPassword">Confirm Password</label>
          <input 
            type="password" 
            id="confirmPassword" 
            v-model="confirmPassword" 
            class="form-control" 
            required 
            placeholder="Confirm your password"
          />
          <div v-if="passwordsDoNotMatch" class="text-danger">
            Passwords do not match
          </div>
        </div>
        
        <button 
          type="submit" 
          class="btn register-btn" 
          :disabled="authStore.loading || passwordsDoNotMatch"
        >
          {{ authStore.loading ? 'Creating Account...' : 'Create Account' }}
        </button>
      </form>
      
      <div class="login-link">
        Already have an account? 
        <router-link to="/login">Login</router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')

const passwordsDoNotMatch = computed(() => {
  return password.value && confirmPassword.value && password.value !== confirmPassword.value
})

const handleRegister = async () => {
  if (passwordsDoNotMatch.value) {
    return
  }
  
  const success = await authStore.register(name.value, email.value, password.value)
  
  if (success) {
    router.push('/')
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 2rem 1rem;
}

.register-card {
  background: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  padding: 2rem;
  width: 100%;
  max-width: 450px;
}

.register-card h1 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.register-form {
  margin-bottom: 1rem;
}

.register-btn {
  width: 100%;
  margin-top: 1rem;
  padding: 0.75rem;
}

.login-link {
  text-align: center;
  margin-top: 1rem;
  font-size: 0.9rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.text-danger {
  color: var(--danger-color);
  font-size: 0.875rem;
  margin-top: 0.25rem;
}
</style>
