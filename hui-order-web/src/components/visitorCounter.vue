<template>
  <div class="visitor-counter-container">
    <div class="visitor-counter">您是今天第 {{ visitorCount }} 位享用美食的客人</div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'

const visitorCount = ref(0)

onMounted(() => {
  // Load and increment visitor count
  let count = localStorage.getItem('dailyVisitorCount')
  let lastDate = localStorage.getItem('visitorCountDate')
  const today = new Date().toDateString()
  
  // Reset count if it's a new day
  if (lastDate !== today) {
    count = '0'
    localStorage.setItem('visitorCountDate', today)
  }
  
  // Increment and save the count
  visitorCount.value = parseInt(count || '0') + 1
  localStorage.setItem('dailyVisitorCount', visitorCount.value.toString())
})
</script>

<style scoped>
.visitor-counter-container {
  margin-top: 15px;
  display: flex;
  justify-content: center;
}

.visitor-counter {
  background-color: rgba(0, 0, 0, 0.5);
  color: gold;
  padding: 8px 15px;
  border-radius: 20px;
  font-size: 0.9rem;
}

@keyframes pulse {
  0% {
    opacity: 0.7;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.7;
  }
}
</style>