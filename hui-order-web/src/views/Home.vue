<template>
  <div class="home">
    <section class="hero">
      <div class="hero-content">
        <h1>歡迎來到"徽"姑娘小食堂</h1>
        <h3>北方風味，匠心手作</h3>
        <div class="action-container">
          <router-link to="/items" class="btn">Shop Now</router-link>
        </div>
        <visitorCounter />
          <!-- <div class="visitor-counter">您是今天第 {{ visitorCount }} 位享用美食的客人</div> -->
        
      </div>
    </section>
    
    <section class="featured-items">
      <h2>北方下午小點</h2>
      <div class="item-grid">
        <item-card
          v-for="item in featuredItems"
          :key="item.id"
          :item="item"
        />
      </div>
    </section>
  
    <!--
    <section class="categories">
      <h2>Shop by Category</h2>
      <div class="category-list">
        <div 
          v-for="category in categories" 
          :key="category.name" 
          class="category-card"
          @click="goToCategory(category.value)"
        >
          <div class="category-icon">{{ category.icon }}</div>
          <h3>{{ category.name }}</h3>
        </div>
      </div>
    </section>
    -->
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { itemService } from '../services/items'
import { Item } from '../types/item'
import ItemCard from '../components/ItemCard.vue'
import VisitorCounter from '../components/visitorCounter.vue'
const router = useRouter()
const featuredItems = ref<Item[]>([])

const categories = [
  { name: '下午小點', value: '下午小點', icon: '📱' },
  { name: '自製醬料', value: '自製醬料', icon: '👕' },
  { name: 'Home', value: 'home', icon: '🏠' }
]

onMounted(async () => {
  try {
    const items = await itemService.getItems()
    // Get 3 random items as featured
    featuredItems.value = items
      .sort(() => 0.5 - Math.random())
      .slice(0, 3)
      
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
  } catch (error) {
    console.error('Failed to load featured items', error)
  }
})

const goToCategory = (category: string) => {
  router.push({ 
    path: '/items', 
    query: { category }
  })
}
</script>

<style scoped>
.hero {
  background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('https://via.placeholder.com/1200x400');
  background-size: cover;
  background-position: center;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  margin-bottom: 3rem;
  border-radius: 8px;
}

.hero-content {
  color: white;
  max-width: 600px;
  padding: 2rem;
}

.hero h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

.hero .btn {
  color: white;
  font-size: 1.3rem;
  font-weight: 1500;
  padding: 0.6rem 1.2rem;
  margin-bottom: 1rem;
  
  border-radius: 30px;
  
  margin-top: 1rem;
  display: inline-block;
  animation: pulse 2s infinite;
}

.action-container {
  margin-bottom: 2rem;
  width: 100%;
  padding-top: 0.5rem;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

.featured-items, .categories {
  margin-bottom: 3rem;
}

.featured-items h2, .categories h2 {
  text-align: center;
  margin-bottom: 2rem;
  font-size: 2rem;
}

.item-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.category-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

.category-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
  padding: 1.5rem;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.category-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.category-card h3 {
  margin: 0;
  font-size: 1.2rem;
}

@media (max-width: 768px) {
  .hero {
    height: 300px;
  }
  
  .hero h1 {
    font-size: 2rem;
  }
  
  .hero p {
    font-size: 1rem;
  }
  
  .item-grid, .category-list {
    grid-template-columns: 1fr;
  }
}
</style>
