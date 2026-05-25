import { StrictMode } from 'react'
import { createRouter, RouterProvider } from '@tanstack/react-router'
import { createRoot } from 'react-dom/client'
import '@/services/openapiConfig'
import { platformConfig } from '@/config/platform'
import './index.css'
import { routeTree } from './routeTree.gen'

const router = createRouter({ routeTree })

document.title = platformConfig.meta.title

const metaDescription = document.querySelector<HTMLMetaElement>(
  'meta[name="description"]',
)

if (metaDescription) {
  metaDescription.content = platformConfig.meta.description
} else {
  const descriptionElement = document.createElement('meta')
  descriptionElement.name = 'description'
  descriptionElement.content = platformConfig.meta.description
  document.head.append(descriptionElement)
}

// Register the router instance for type safety
declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}

const rootElement = document.getElementById('root')!

if (!rootElement.innerHTML) {
  const root = createRoot(rootElement)
  root.render(
    <StrictMode>
      <RouterProvider router={router} />
    </StrictMode>,
  )
}
