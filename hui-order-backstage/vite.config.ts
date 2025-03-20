import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
      port: 5174, // 設置固定端口為3000
      host: '0.0.0.0',
      strictPort: true, // 如果端口已被佔用，則直接退出而不是嘗試下一個可用端口
    }
})
