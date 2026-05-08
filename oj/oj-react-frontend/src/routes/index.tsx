import { createFileRoute } from '@tanstack/react-router'
import HomePage from '@/pages/HomePage'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'

export const Route = createFileRoute('/')({
  component: () => {
    return <>
      <HomePage/>
      <TanStackRouterDevtools />
    </>
  }
})
