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
    port: 5173, // 设置固定端口为3000
    host: '0.0.0.0',
    strictPort: true, // 如果端口已被占用，则直接退出而不是尝试下一个可用端口
  }
})
