import { createFileRoute, Link } from '@tanstack/react-router'
import { FileQuestion, Users } from 'lucide-react'
import { Button } from '@/components/ui/button'

export const Route = createFileRoute('/admin/')({
  component: RouteComponent,
})

function RouteComponent() {
  return (
    <div className="p-4 sm:p-6">
      <div className="mx-auto max-w-7xl">
        <h1 className="text-2xl font-semibold tracking-tight">管理后台</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          选择要管理的功能模块
        </p>

        <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <div className="flex flex-col rounded-lg border bg-background p-5">
            <div className="flex size-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
              <FileQuestion className="size-5" />
            </div>
            <h2 className="mt-4 text-lg font-medium">题目管理</h2>
            <p className="mt-1 text-sm text-muted-foreground">
              管理题库中的题目内容、标签、判题配置和样例数据
            </p>
            <Button asChild className="mt-4 w-fit">
              <Link to="/admin/questions">进入题目管理</Link>
            </Button>
          </div>

          <div className="flex flex-col rounded-lg border bg-background p-5">
            <div className="flex size-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
              <Users className="size-5" />
            </div>
            <h2 className="mt-4 text-lg font-medium">用户管理</h2>
            <p className="mt-1 text-sm text-muted-foreground">
              管理系统用户账号、角色和基本信息
            </p>
            <Button asChild className="mt-4 w-fit">
              <Link to="/admin/users">进入用户管理</Link>
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
