import { createFileRoute } from '@tanstack/react-router'
import { checkLoginAndRedirect } from '@/lib/auth/accessCheck'
import ProfilePage from '@/pages/user/ProfilePage'

export const Route = createFileRoute('/user/profile')({
  beforeLoad: checkLoginAndRedirect,
  component: ProfilePage,
})
