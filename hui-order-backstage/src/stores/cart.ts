import { defineStore } from 'pinia'
import { Item } from '../types/item'

interface CartItem extends Item {
  quantity: number
}

interface CartState {
  items: CartItem[]
  lastError: string | null
  lastOperation: string | null
}

export const useCartStore = defineStore('cart', {
  state: (): CartState => ({
    items: [],
    lastError: null,
    lastOperation: null
  }),
  
  getters: {
    totalItems: (state) => {
      return state.items.reduce((total, item) => total + item.quantity, 0)
    },
    
    totalPrice: (state) => {
      return state.items.reduce((total, item) => total + (item.price * item.quantity), 0)
    },
    
    isEmpty: (state) => state.items.length === 0,
    
    itemCount: (state) => state.items.length
  },
  
  actions: {
    addItem(item: Item, quantity: number = 1) {
      try {
        if (!item || !item.id) {
          this.lastError = '無效的商品資料'
          return false
        }
        
        if (quantity <= 0) {
          this.lastError = '商品數量必須大於0'
          return false
        }

        // 確保商品ID是字串類型
        const itemIdStr = String(item.id)
        const existingItem = this.items.find(i => String(i.id) === itemIdStr)
        
        if (existingItem) {
          existingItem.quantity += quantity
        } else {
          this.items.push({
            ...item,
            quantity
          })
        }
        
        this.lastOperation = `新增商品:${item.name}(${item.id}),數量:${quantity}`
        // Persist cart to localStorage
        return this.saveCart()
      } catch (error) {
        console.error('新增商品到購物車失敗:', error)
        this.lastError = `新增商品失敗: ${error instanceof Error ? error.message : '未知錯誤'}`
        return false
      }
    },
    
    removeItem(itemId: string) {
      try {
        // 檢查參數
        if (!itemId) {
          this.lastError = '無效的商品ID'
          console.error('移除商品失敗: 無效的商品ID')
          return false
        }

        console.log('呼叫 removeItem 方法，商品ID:', itemId)
        
        // 檢查購物車是否為空
        if (this.items.length === 0) {
          this.lastError = '購物車為空'
          console.warn('購物車為空，無法移除商品')
          return false
        }
        
        // 找到商品索引
        // 確保商品ID是字串類型
        const itemIdStr = String(itemId)
        console.log('比較的商品ID類型:', typeof itemIdStr)
        const index = this.items.findIndex(item => String(item.id) === itemIdStr)
        console.log('找到商品索引:', index, '目前購物車項數:', this.items.length)
        
        if (index !== -1) {
          // 取得要刪除的商品資訊
          const itemToRemove = this.items[index]
          
          // 創建新陣列，排除要刪除的商品
          const updatedItems = this.items.filter(item => String(item.id) !== itemIdStr)
          
          // 更新狀態
          this.items = updatedItems
          
          // 設定操作記錄
          this.lastOperation = `移除商品:${itemToRemove.name}(${itemId})`
          
          // 更新本地儲存
          const saveResult = this.saveCart()
          
          console.log('從購物車移除商品成功:', itemId, '目前購物車商品數:', this.items.length)
          this.verifyCartIntegrity() // 檢查購物車完整性
          return saveResult
        }
        
        this.lastError = `無法找到ID為${itemId}的商品`
        console.error('無法找到要移除的商品:', itemId)
        return false
      } catch (error) {
        this.lastError = `移除商品失敗: ${error instanceof Error ? error.message : '未知錯誤'}`
        console.error('移除商品時發生錯誤:', error)
        return false
      }
    },
    
    updateQuantity(itemId: string, quantity: number) {
      try {
        // 輸入驗證
        if (!itemId) {
          this.lastError = '無效的商品ID'
          return false
        }
        
        if (typeof quantity !== 'number' || isNaN(quantity)) {
          this.lastError = '無效的數量值'
          return false
        }

        // 確保商品ID是字串類型
        const itemIdStr = String(itemId)
        const item = this.items.find(i => String(i.id) === itemIdStr)
        
        if (item) {
          if (quantity <= 0) {
            return this.removeItem(itemId)
          } else {
            const oldQuantity = item.quantity
            item.quantity = quantity
            this.lastOperation = `更新商品:${item.name}(${itemId})數量:${oldQuantity}->${quantity}`
            return this.saveCart()
          }
        }
        
        this.lastError = `無法找到ID為${itemId}的商品`
        return false
      } catch (error) {
        this.lastError = `更新商品數量失敗: ${error instanceof Error ? error.message : '未知錯誤'}`
        console.error('更新商品數量時發生錯誤:', error)
        return false
      }
    },
    
    clearCart() {
      console.log('開始清空購物車')
      
      try {
        // 檢查購物車是否已經為空
        if (this.items.length === 0) {
          console.log('購物車已經是空的')
          return true
        }

        // 記錄操作
        const itemCount = this.items.length
        this.lastOperation = `清空購物車(${itemCount}件商品)`
        
        // 重設購物車為空陣列
        this.items = []
        
        // 直接清除本地儲存中的購物車數據
        localStorage.removeItem('cart')
        localStorage.setItem('cart', JSON.stringify([]))
        
        // 檢查是否成功清除
        const check = localStorage.getItem('cart')
        console.log('清空後的購物車狀態:', check)
        
        // 在控制台確認已清空購物車
        console.log('購物車已完全清空')
        return true
      } catch (error) {
        console.error('清空購物車時出錯:', error)
        this.lastError = `清空購物車失敗: ${error instanceof Error ? error.message : '未知錯誤'}`
        return false
      }
    },
    
    loadCart() {
      try {
        console.log('載入購物車資料')
        const saved = localStorage.getItem('cart')
        
        if (!saved) {
          console.log('本地儲存中沒有購物車資料')
          return false
        }
        
        // 嘗試解析購物車數據
        const parsedData = JSON.parse(saved)
        
        // 驗證數據是否為陣列
        if (!Array.isArray(parsedData)) {
          console.error('購物車資料格式錯誤：不是陣列')
          this.lastError = '購物車資料格式錯誤'
          return false
        }
        
        // 驗證每個商品
        const validItems = parsedData.filter(item => {
          return item && typeof item === 'object' && item.id && item.name
        })
        
        // 更新購物車
        this.items = validItems
        console.log(`成功載入${validItems.length}件商品到購物車`)
        return true
      } catch (e) {
        console.error('從localStorage載入購物車失敗', e)
        this.lastError = `載入購物車失敗: ${e instanceof Error ? e.message : '未知錯誤'}`
        return false
      }
    },
    
    saveCart() {
      try {
        // 檢查購物車數據的有效性
        const validItems = this.items.filter(item => item && item.id && item.name)
        
        // 如果有無效項，進行清理
        if (validItems.length !== this.items.length) {
          console.warn(`清理了${this.items.length - validItems.length}個無效商品`)
          this.items = validItems
        }
        
        // 將購物車數據儲存到本地
        const cartData = JSON.stringify(this.items)
        localStorage.setItem('cart', cartData)
        console.log(`購物車已儲存到本地儲存: ${this.items.length}件商品`)
        
        // 驗證儲存是否成功
        const savedData = localStorage.getItem('cart')
        if (savedData !== cartData) {
          console.warn('購物車資料儲存不匹配')
          this.lastError = '儲存購物車資料不完整'
          return false
        }
        
        return true
      } catch (error) {
        console.error('儲存購物車資料失敗', error)
        this.lastError = `儲存購物車失敗: ${error instanceof Error ? error.message : '未知錯誤'}`
        return false
      }
    },
    
    /**
     * 驗證購物車資料的完整性
     * 用於排查潛在的問題
     */
    verifyCartIntegrity() {
      try {
        console.log('驗證購物車完整性')
        
        // 1. 檢查記憶體中的購物車
        if (!Array.isArray(this.items)) {
          console.error('購物車資料不是陣列')
          return false
        }
        
        // 2. 檢查localStorage中的購物車
        const savedCart = localStorage.getItem('cart')
        if (!savedCart) {
          console.warn('localStorage中沒有購物車資料')
          return false
        }
        
        // 3. 比較記憶體和localStorage中的資料
        const parsedSavedCart = JSON.parse(savedCart)
        const memoryCartSize = this.items.length
        const storageCartSize = Array.isArray(parsedSavedCart) ? parsedSavedCart.length : -1
        
        if (memoryCartSize !== storageCartSize) {
          console.error(`購物車資料不同步: 記憶體(${memoryCartSize})項 vs 儲存(${storageCartSize})項`)
          return false
        }
        
        console.log('購物車資料驗證成功')
        return true
      } catch (error) {
        console.error('驗證購物車完整性時出錯:', error)
        return false
      }
    }
    }
  }
)
