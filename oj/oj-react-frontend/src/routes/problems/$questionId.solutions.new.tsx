import { createFileRoute } from '@tanstack/react-router'
import SolutionEditorPage from '@/pages/SolutionEditorPage'

export const Route = createFileRoute('/problems/$questionId/solutions/new')({
  component: RouteComponent,
})

function RouteComponent() {
  const { questionId } = Route.useParams()

  return <SolutionEditorPage questionId={questionId} />
}
