/**
 * Copyright 2025 bbcdabao Team
 */

import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import VueSetupExtend from 'vite-plugin-vue-setup-extend';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import Icons from 'unplugin-icons/vite';
import IconsResolver from 'unplugin-icons/resolver';


export default defineConfig(
  ({ mode }) => {
  const isDev = mode === 'development';
  return {
    base: './',
    plugins: [
      vue(),
      !isDev && VueSetupExtend(),
      AutoImport({
        resolvers: [ElementPlusResolver()]
      }),
      Components({
        resolvers: [
          ElementPlusResolver(),
          IconsResolver({ prefix: 'i' }),
        ]
      }),
      Icons({
        autoInstall: true,
      }),
    ].filter(Boolean),
    define: {
      __VUE_OPTIONS_API__: false,
      __VUE_PROD_DEVTOOLS__: false,
      __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: false
    },
    optimizeDeps: {
      include: ['schart.js']
    },
    resolve: {
      alias: {
        '@': '/src',
        '~': '/src/assets'
      }
    },
    server: {
      proxy: {
        '/openapi': {
          target: 'http://localhost:9090/openapi',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/openapi/, '')
        },
        '/api': {
          target: 'http://localhost:9090/api',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    },
    build: {
      sourcemap: true
    }
  }
});