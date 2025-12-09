import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],

  server: {
    port: 3000,      // 将端口改为 3000
    host: '0.0.0.0', // (可选) 加上这行，局域网内的手机也能访问你的电脑
    allowedHosts: true,
    cors: true,
  }
})
