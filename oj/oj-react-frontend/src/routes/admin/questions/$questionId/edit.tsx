import { createFileRoute } from '@tanstack/react-router'
import QuestionAdminEditorPage from '@/pages/admin/QuestionAdminEditorPage'

export const Route = createFileRoute('/admin/questions/$questionId/edit')({
  component: EditQuestionRoute,
})

function EditQuestionRoute() {
  const { questionId } = Route.useParams()

  return <QuestionAdminEditorPage questionId={questionId} />
}
