import { createFileRoute } from '@tanstack/react-router'
import LoginPage from '@/pages/auth/LoginPage'

type LoginSearch = {
  account?: string
}

export const Route = createFileRoute('/user/login')({
  component: LoginPage,
  validateSearch: (search): LoginSearch => ({
    account: typeof search.account === 'string' ? search.account : undefined,
  }),
})
