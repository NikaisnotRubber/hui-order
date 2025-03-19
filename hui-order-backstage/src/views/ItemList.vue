<template>
  <div class="item-list">
    <div class="filters">
      <h1>我們的產品</h1>
      
      <div class="search-bar">
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="Search items..." 
          class="form-control"
          @input="handleSearch"
        />
      </div>
      
      <div class="category-filter">
        <button 
          @click="selectedCategory = ''" 
          :class="['filter-btn', selectedCategory === '' ? 'active' : '']"
        >
          All
        </button>
        <button 
          v-for="category in categories" 
          :key="category"
          @click="selectedCategory = category"
          :class="['filter-btn', selectedCategory === category ? 'active' : '']"
        >
          {{ capitalizeFirstLetter(category) }}
        </button>
      </div>
    </div>
    
    <div v-if="loading" class="loading">
      <p>Loading items...</p>
    </div>
    
    <div v-else-if="displayedItems.length === 0" class="no-items">
      <p>No items found matching your criteria.</p>
    </div>
    
    <div v-else class="item-grid">
      <item-card
        v-for="item in displayedItems"
        :key="item.id"
        :item="item"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { itemService } from '../services/items'
import { Item } from '../types/item'
import ItemCard from '../components/ItemCard.vue'

const route = useRoute()
const router = useRouter()

const items = ref<Item[]>([])
const loading = ref(true)
const searchQuery = ref('')
const selectedCategory = ref('')
const categories = ['dim sum', '自製醬料']

// Load items on component mount
onMounted(async () => {
  try {
    // Check if category is provided in the URL
    const categoryParam = route.query.category as string
    if (categoryParam && categories.includes(categoryParam)) {
      selectedCategory.value = categoryParam
    }
    
    // Load all items
    items.value = await itemService.getItems()
  } catch (error) {
    console.error('Failed to load items', error)
  } finally {
    loading.value = false
  }
})

// Update URL when category changes
watch(selectedCategory, (newCategory) => {
  router.push({
    path: route.path,
    query: newCategory ? { category: newCategory } : {}
  })
})

// Filtered items based on search and category
const displayedItems = computed(() => {
  let filtered = items.value
  
  // Filter by category if selected
  if (selectedCategory.value) {
    filtered = filtered.filter(item => item.category === selectedCategory.value)
  }
  
  // Filter by search query if not empty
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(item => 
      item.name.toLowerCase().includes(query) || 
      item.description.toLowerCase().includes(query)
    )
  }
  
  return filtered
})

// Utils
const capitalizeFirstLetter = (string: string) => {
  return string.charAt(0).toUpperCase() + string.slice(1)
}

// Debounce search to avoid too many filters being applied while typing
let searchTimeout: number | null = null
const handleSearch = () => {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  
  searchTimeout = setTimeout(() => {
    // No additional action needed, the computed property will update automatically
    searchTimeout = null
  }, 300) as unknown as number
}
</script>

<style scoped>
.item-list {
  width: 100%;
}

.filters {
  margin-bottom: 2rem;
}

h1 {
  margin-bottom: 1.5rem;
}

.search-bar {
  margin-bottom: 1.5rem;
}

.category-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 2rem;
}

.filter-btn {
  background-color: white;
  border: 1px solid var(--border-color);
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-btn:hover {
  background-color: #f5f5f5;
}

.filter-btn.active {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.item-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.loading, .no-items {
  text-align: center;
  padding: 2rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: var(--shadow);
}

@media (max-width: 768px) {
  .item-grid {
    grid-template-columns: 1fr;
  }
}
</style>
