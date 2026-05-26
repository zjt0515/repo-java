import { createFileRoute } from '@tanstack/react-router'
import SolutionDetailPage from '@/pages/SolutionDetailPage'

export const Route = createFileRoute('/problems/$questionId/solutions/$postId')({
  component: RouteComponent,
})

function RouteComponent() {
  const { postId, questionId } = Route.useParams()

  return <SolutionDetailPage postId={postId} questionId={questionId} />
}
