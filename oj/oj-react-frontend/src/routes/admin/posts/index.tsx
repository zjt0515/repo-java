import { createFileRoute } from '@tanstack/react-router'
import PostAdminListPage from '@/pages/admin/PostAdminListPage'

export const Route = createFileRoute('/admin/posts/')({
  component: PostAdminListPage,
})
