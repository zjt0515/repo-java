import { createFileRoute } from '@tanstack/react-router'
import { checkLoginAndRedirect } from '@/lib/auth/accessCheck'
import SubmissionDetailPage from '@/pages/submit/SubmissionDetailPage'

export const Route = createFileRoute('/submissions/$submissionId')({
  beforeLoad: checkLoginAndRedirect,
  component: RouteComponent,
})

function RouteComponent() {
  const { submissionId } = Route.useParams()

  return <SubmissionDetailPage submissionId={submissionId} />
}
