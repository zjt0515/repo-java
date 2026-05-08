import { createFileRoute } from '@tanstack/react-router'
import QuestionAdminEditorPage from '@/pages/admin/QuestionAdminEditorPage'

export const Route = createFileRoute('/admin/questions/new')({
  component: QuestionAdminEditorPage,
})
