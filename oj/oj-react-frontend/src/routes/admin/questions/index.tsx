import { createFileRoute } from '@tanstack/react-router'
import QuestionAdminListPage from '@/pages/admin/QuestionAdminListPage'

export const Route = createFileRoute('/admin/questions/')({
  component: QuestionAdminListPage,
})
