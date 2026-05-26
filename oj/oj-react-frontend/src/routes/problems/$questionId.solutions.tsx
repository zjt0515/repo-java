import { Outlet, createFileRoute, useRouterState } from '@tanstack/react-router'
import SolutionListPage from '@/pages/SolutionListPage'

export const Route = createFileRoute('/problems/$questionId/solutions')({
  component: RouteComponent,
})

function RouteComponent() {
  const { questionId } = Route.useParams()
  const pathname = useRouterState({
    select: (state) => state.location.pathname,
  })
  const solutionListPath = `/problems/${questionId}/solutions`

  if (pathname.startsWith(`${solutionListPath}/`)) {
    return <Outlet />
  }

  return <SolutionListPage questionId={questionId} />
}
