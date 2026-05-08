import { createFileRoute } from '@tanstack/react-router'
import { Home, ServerCrash } from 'lucide-react'
import StatusPage from '@/pages/status/StatusPage'

export const Route = createFileRoute('/500')({
  component: ServerErrorPage,
})

function ServerErrorPage() {
  return (
    <StatusPage
      actions={[
        { icon: Home, label: '回到首页', to: '/' },
        { label: '返回题库', to: '/problems', variant: 'outline' },
      ]}
      code="500"
      description="服务暂时无法完成请求。你可以稍后重试，或先回到题库继续查看其他内容。"
      icon={ServerCrash}
      title="服务开小差了"
      tone="danger"
    />
  )
}
