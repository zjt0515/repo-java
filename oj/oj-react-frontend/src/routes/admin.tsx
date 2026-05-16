import { createFileRoute } from '@tanstack/react-router'
import { checkAdminAndRedirect } from '@/lib/auth/accessCheck'
import AdminLayout from '@/layouts/AdminLayout'

export const Route = createFileRoute('/admin')({
  beforeLoad: checkAdminAndRedirect,
  component: AdminLayout,
})
