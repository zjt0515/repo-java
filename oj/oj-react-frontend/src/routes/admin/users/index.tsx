import { createFileRoute } from '@tanstack/react-router'
import UserAdminListPage from '@/pages/admin/UserAdminListPage'

export const Route = createFileRoute('/admin/users/')({
  component: UserAdminListPage,
})
