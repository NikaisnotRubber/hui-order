<template>
  <div class="footer-container" :class="{ expanded: isExpanded }">
    <div class="overlay" v-if="isExpanded" @click="toggleExpand"></div>
    <div class="footer-handle" @click="toggleExpand">
      <div class="handle-icon">
        <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" :style="{ transform: isExpanded ? 'rotate(180deg)' : 'rotate(0deg)' }">
          <polyline points="6 9 12 15 18 9"></polyline>
        </svg>
      </div>
      <span>{{ isExpanded ? '收起' : '關於我們' }}</span>
    </div>

    <div class="footer-content">
      <div class="store-info">
        <h3>歡迎光臨 Hui Order</h3>
        <div class="store-details">
          <div class="detail-item">
            <div class="icon">
              <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
            </div>
            <div>
              <strong>地址：</strong>
              <span>台北市 內湖區 瑞光路 123 號</span>
            </div>
          </div>

          <div class="detail-item">
            <div class="icon">
              <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
              </svg>
            </div>
            <div>
              <strong>電話：</strong>
              <span>(02) 2222-3333</span>
            </div>
          </div>

          <div class="detail-item">
            <div class="icon">
              <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
            </div>
            <div>
              <strong>營業時間：</strong>
              <span>週一至週五 10:00 - 21:00</span>
              <br>
              <span>週六至週日 11:00 - 22:00</span>
            </div>
          </div>
        </div>
      </div>

      <div class="footer-about">
        
        <div class="copyright">
          &copy; 2009<span v-if="thisYear != 2009" v-text="`-${thisYear}`" /> Design & Coding by 老闆娘兒子
        </div>
      </div>
    </div>
  </div>
</template>
  
  <script lang="ts">
import { defineComponent, ref, computed, onMounted, onBeforeUnmount } from "vue";

export default defineComponent({
  name: "Footer",
  setup() {
    const isExpanded = ref(false);
    
    const toggleExpand = () => {
      isExpanded.value = !isExpanded.value;
      if (isExpanded.value) {
        document.body.style.overflow = 'hidden';
      } else {
        document.body.style.overflow = '';
      }
    };
    
    const thisYear = computed(() => {
      return new Date().getFullYear();
    });
    
    // Close footer on escape key press
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isExpanded.value) {
        toggleExpand();
      }
    };
    
    onMounted(() => {
      window.addEventListener('keydown', handleKeyDown);
    });
    
    onBeforeUnmount(() => {
      window.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = '';
    });
    
    return {
      isExpanded,
      toggleExpand,
      thisYear
    };
  }
});
</script>
  
  <style scoped>
.footer-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: 50px;
  background-color: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  transition: height 0.3s ease;
  overflow: hidden;
  z-index: 100;
}

.footer-container.expanded {
  height: 380px;
  overflow-y: auto;
}

.footer-container.expanded .footer-handle {
  border-bottom: 1px solid #eee;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.01);
  z-index: -1;
}

.footer-handle {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  cursor: pointer;
  padding: 0 0px;
  background-repeat: white;
  transition: background-color 0.2s ease;
}

.footer-handle:hover {
  background-color: #f5f5f5;
}

.footer-handle span {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-left: 8px;
}

.footer-handle .handle-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #801c1c;
}

.footer-handle .handle-icon svg {
  transition: transform 0.3s ease;
}

.footer-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.store-info {
  margin-bottom: 20px;
}

.store-info h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #801c1c;
  text-align: center;
}

.store-details {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
}

.detail-item .icon {
  flex-shrink: 0;
  margin-right: 10px;
  color: #801c1c;
  width: 20px;
  height: 20px;
}

.detail-item strong {
  margin-right: 5px;
}

.footer-about {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  font-size: 14px;
  color: hsl(0, 76%, 35%);
  text-align: center;
}

.footer-about .copyright {
  font-size: 12px;
  color: #101010;
}

a {
  color: #801c1c;
  text-decoration: none;
  transition: color 0.2s;
}

a:hover {
  text-decoration: underline;
  color: #801c1c;
}

/* Responsive styles */
@media (max-width: 768px) {
  .footer-container.expanded {
    height: 420px;
  }
  
  .footer-content {
    padding: 15px;
  }
  
  .store-details {
    gap: 20px;
  }
  
  .detail-item {
    flex-direction: column;
  }
  
  .detail-item .icon {
    margin-bottom: 5px;
  }
}

@media (max-height: 500px) {
  .footer-container.expanded {
    height: 80vh;
  }
}
</style>