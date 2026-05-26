import { Outlet, createFileRoute, useRouterState } from '@tanstack/react-router'
import { checkLoginAndRedirect } from '@/lib/auth/accessCheck'
import ProblemWorkspacePage from '@/pages/ProblemWorkspacePage'

export const Route = createFileRoute('/problems/$questionId')({
  beforeLoad: checkLoginAndRedirect,
  component: RouteComponent,
})

function RouteComponent() {
  const { questionId } = Route.useParams()
  const pathname = useRouterState({
    select: (state) => state.location.pathname,
  })

  if (pathname.startsWith(`/problems/${questionId}/solutions`)) {
    return <Outlet />
  }

  return <ProblemWorkspacePage questionId={questionId} />
}
