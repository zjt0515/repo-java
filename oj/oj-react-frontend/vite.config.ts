import path from 'node:path'
import { defineConfig, type Plugin } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { tanstackRouter } from '@tanstack/router-plugin/vite'
import { platformConfig } from './src/config/platform'

function escapeHtml(value: string) {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('"', '&quot;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
}

function platformHtmlPlugin(): Plugin {
  return {
    name: 'platform-html',
    transformIndexHtml(html) {
      return html
        .replaceAll('%PLATFORM_TITLE%', escapeHtml(platformConfig.meta.title))
        .replaceAll(
          '%PLATFORM_DESCRIPTION%',
          escapeHtml(platformConfig.meta.description),
        )
    },
  }
}

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    platformHtmlPlugin(),
    tanstackRouter({
      target: 'react',
      autoCodeSplitting: true,
    }),
    react(), tailwindcss(),

  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 7878,
    proxy: {
      '/api': {
        changeOrigin: true,
        target: 'http://localhost:8101',
      },
    },
  },
})
