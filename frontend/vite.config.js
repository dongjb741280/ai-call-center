import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/voxai-admin': {
        target: 'http://localhost:7100',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/voxai-admin/, '')
      },
      '/voxai-call': {
        target: 'http://localhost:7200',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/voxai-call/, '')
      }
    }
  }
})


