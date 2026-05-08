import { createFileRoute } from '@tanstack/react-router'
import { LockKeyhole, LogIn } from 'lucide-react'
import StatusPage from '@/pages/status/StatusPage'

export const Route = createFileRoute('/401')({
  component: UnauthorizedPage,
})

function UnauthorizedPage() {
  return (
    <StatusPage
      actions={[
        { icon: LogIn, label: '去登录', to: '/user/login' },
        { label: '返回题库', to: '/problems', variant: 'outline' },
      ]}
      code="401"
      description="当前操作需要登录后才能继续。请先登录账号，再回到刚才的页面完成操作。"
      icon={LockKeyhole}
      title="需要登录"
      tone="warning"
    />
  )
}
