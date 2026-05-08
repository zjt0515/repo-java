import { createFileRoute } from '@tanstack/react-router'
import { ListChecks, ShieldAlert } from 'lucide-react'
import StatusPage from '@/pages/status/StatusPage'

export const Route = createFileRoute('/403')({
  component: ForbiddenPage,
})

function ForbiddenPage() {
  return (
    <StatusPage
      actions={[
        { icon: ListChecks, label: '返回题库', to: '/problems' },
        { label: '回到首页', to: '/', variant: 'outline' },
      ]}
      code="403"
      description="你当前的账号没有访问该资源的权限。如果你认为这是误判，可以联系管理员确认账号角色。"
      icon={ShieldAlert}
      title="权限不足"
      tone="danger"
    />
  )
}
