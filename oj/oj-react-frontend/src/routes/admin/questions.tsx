import { Outlet, createFileRoute } from '@tanstack/react-router'
import { checkAdminAndRedirect } from '@/lib/auth/accessCheck'

export const Route = createFileRoute('/admin/questions')({
  beforeLoad: checkAdminAndRedirect,
  component: Outlet,
})
