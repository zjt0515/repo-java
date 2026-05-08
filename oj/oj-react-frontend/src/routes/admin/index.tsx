import { createFileRoute } from '@tanstack/react-router'
import { checkAdminAndRedirect } from '@/lib/auth/accessCheck'

export const Route = createFileRoute('/admin/')({
  beforeLoad: checkAdminAndRedirect,
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/admin/"!</div>
}
