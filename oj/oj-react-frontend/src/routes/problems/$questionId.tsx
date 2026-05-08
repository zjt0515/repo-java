import { createFileRoute } from '@tanstack/react-router'
import { checkLoginAndRedirect } from '@/lib/auth/accessCheck'
import ProblemWorkspacePage from '@/pages/ProblemWorkspacePage'

export const Route = createFileRoute('/problems/$questionId')({
  beforeLoad: checkLoginAndRedirect,
  component: RouteComponent,
})

function RouteComponent() {
  const { questionId } = Route.useParams()

  return <ProblemWorkspacePage questionId={questionId} />
}
