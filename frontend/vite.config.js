import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      // 开发环境：Java接口转发到8080
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 开发环境：Coze接口转发到目标地址
      '/coze-api': {
        target: 'https://s9y6dkw5z8.coze.site',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/coze-api/, ''),
        // 支持SSE（Server-Sent Events）
        configure: (proxy, options) => {
          proxy.on('proxyReq', (proxyReq, req, res) => {
            proxyReq.setHeader('Accept', 'text/event-stream');
          });
        }
      }
    }
  },
  base: './',
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false
  }
})