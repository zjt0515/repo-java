import { createFileRoute } from '@tanstack/react-router'
import { Home, SearchX } from 'lucide-react'
import StatusPage from '@/pages/status/StatusPage'

export const Route = createFileRoute('/404')({
  component: NotFoundPage,
})

export function NotFoundPage() {
  return (
    <StatusPage
      actions={[
        { icon: Home, label: '回到首页', to: '/' },
        { label: '返回题库', to: '/problems', variant: 'outline' },
      ]}
      code="404"
      description="这个页面不存在，可能是链接已失效、地址输入有误，或者内容已经被移动。"
      icon={SearchX}
      title="页面没有找到"
    />
  )
}
