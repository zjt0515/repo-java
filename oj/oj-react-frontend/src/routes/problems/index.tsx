import { createFileRoute } from '@tanstack/react-router'
import ProblemListPage from '@/pages/ProblemListPage'

export const Route = createFileRoute('/problems/')({
  component: ProblemListPage,
})
