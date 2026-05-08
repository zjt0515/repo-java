import { createFileRoute } from '@tanstack/react-router'
import { checkLoginAndRedirect } from '@/lib/auth/accessCheck'
import SubmissionsPage from '@/pages/user/SubmissionsPage'

export const Route = createFileRoute('/user/submissions')({
  beforeLoad: checkLoginAndRedirect,
  component: SubmissionsPage,
})
