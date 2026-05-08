import { createRootRoute } from '@tanstack/react-router'
import App from '@/App'
import { restoreLoginStatus } from '@/lib/auth/accessCheck'
import { NotFoundPage } from './404'

export const Route = createRootRoute({
  beforeLoad: restoreLoginStatus,
  component: App,
  notFoundComponent: NotFoundPage,
})
