import { Settings2, Trophy } from 'lucide-react'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'

const problems = [
  { id: '1024', title: '两数之和最接近目标值', difficulty: '中等', status: '进行中' },
  { id: '1031', title: '区间合并后的最大收益', difficulty: '困难', status: '未开始' },
  { id: '0988', title: '链表中的环入口', difficulty: '简单', status: '已通过' },
  { id: '1097', title: '最短编辑路径', difficulty: '中等', status: '未开始' },
]

function TrainingSidebar() {
  return (
    <aside className="hidden border-r bg-muted/25 lg:block">
      <div className="flex h-12 items-center justify-between border-b px-4">
        <span className="text-sm font-medium">今日训练</span>
        <Button variant="ghost" size="icon-sm">
          <Settings2 />
        </Button>
      </div>
      <div className="space-y-4 p-3">
        <div className="grid grid-cols-2 gap-2">
          <Metric label="通过率" value="68%" />
          <Metric label="连续" value="12 天" />
        </div>
        <section className="rounded-lg border bg-background">
          <div className="flex items-center justify-between border-b px-3 py-2.5">
            <span className="text-sm font-medium">推荐题单</span>
            <Trophy className="size-4 text-muted-foreground" />
          </div>
          <div className="divide-y">
            {problems.map((problem) => (
              <button
                key={problem.id}
                className="flex w-full items-start gap-3 px-3 py-3 text-left transition-colors hover:bg-muted/60"
                type="button"
              >
                <Badge
                  variant="secondary"
                  className="mt-0.5 rounded-md text-muted-foreground"
                >
                  {problem.id}
                </Badge>
                <span className="min-w-0 flex-1">
                  <span className="block truncate text-sm font-medium">
                    {problem.title}
                  </span>
                  <span className="mt-1 flex items-center gap-2 text-xs text-muted-foreground">
                    <StatusDot status={problem.status} />
                    {problem.difficulty}
                  </span>
                </span>
              </button>
            ))}
          </div>
        </section>
      </div>
    </aside>
  )
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-lg border bg-background px-3 py-2">
      <div className="text-xs text-muted-foreground">{label}</div>
      <div className="mt-1 text-lg font-semibold tracking-tight">{value}</div>
    </div>
  )
}

function StatusDot({ status }: { status: string }) {
  const color =
    status === '已通过'
      ? 'bg-emerald-500'
      : status === '进行中'
        ? 'bg-amber-500'
        : 'bg-muted-foreground/40'

  return <span className={`size-1.5 rounded-full ${color}`} />
}

export { Metric }
export default TrainingSidebar
