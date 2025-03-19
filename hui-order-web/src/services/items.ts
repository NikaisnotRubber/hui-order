import { Item } from '../types/item'

// Mock items data (in a real app, this would come from an API)
const mockItems: Item[] = [
  {
    id: '1',
    name: '蘿蔔絲餅',
    description: '傳統風味，外皮酥脆，內餡鮮美多汁，手工製作的美味點心。',
    price: 40,
    imageUrl: 'https://via.placeholder.com/300x200?text=蘿蔔絲餅',
    category: 'dim sum',
    stock: 0
  },
  {
    id: '2',
    name: '紅豆餅',
    description: '香甜可口的紅豆餡料，外皮鬆軟香甜，傳統手工製作。',
    price: 40,
    imageUrl: 'https://via.placeholder.com/300x200?text=紅豆餅',
    category: 'dim sum',
    stock: 15
  },
  {
    id: '3',
    name: '韭菜盒',
    description: '新鮮韭菜與豬肉的完美搭配，鮮香可口，營養豐富。',
    price: 40,
    imageUrl: 'https://via.placeholder.com/300x200?text=韭菜盒',
    category: 'dim sum',
    stock: 30
  },
  {
    id: '4',
    name: '自製辣油',
    description: '使用多種香料與辣椒精心製作，提升各種美食的風味。',
    price: 199,
    imageUrl: 'https://via.placeholder.com/300x200?text=自製辣油',
    category: 'home-made-condiment',
    stock: 20
  }
]

// Simulated delay to mimic API call
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

export const itemService = {
  // Get all items
  async getItems(): Promise<Item[]> {
    await delay(500) // Simulate network delay
    return [...mockItems]
  },
  
  // Get item by ID
  async getItemById(id: string): Promise<Item | undefined> {
    await delay(300) // Simulate network delay
    return mockItems.find(item => item.id === id)
  },
  
  // Search items
  async searchItems(query: string): Promise<Item[]> {
    await delay(400) // Simulate network delay
    const lowerCaseQuery = query.toLowerCase()
    
    return mockItems.filter(item => 
      item.name.toLowerCase().includes(lowerCaseQuery) || 
      item.description.toLowerCase().includes(lowerCaseQuery) ||
      item.category.toLowerCase().includes(lowerCaseQuery)
    )
  },
  
  // Filter items by category
  async getItemsByCategory(category: string): Promise<Item[]> {
    await delay(400) // Simulate network delay
    return mockItems.filter(item => item.category === category)
  }
}
