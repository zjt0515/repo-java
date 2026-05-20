import { useEffect, useState } from 'react'
import { Link } from '@tanstack/react-router'
import { ChevronLeft, List } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { listQuestionVOs } from '@/services/questionService'
import type { QuestionVO } from '@/services/questionService'

type TrainingSidebarProps = {
  currentQuestionId?: string
  onToggle?: () => void
}

function TrainingSidebar({ currentQuestionId, onToggle }: TrainingSidebarProps) {
  const [problems, setProblems] = useState<QuestionVO[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function fetchProblems() {
      try {
        const result = await listQuestionVOs({
          current: 1,
          pageSize: 20,
          sortField: 'createTime',
          sortOrder: 'descend',
        })
        setProblems(result.records ?? [])
      } catch (err) {
        // eslint-disable-next-line no-console
        console.error('获取题目列表失败:', err)
      } finally {
        setLoading(false)
      }
    }
    void fetchProblems()
  }, [])

  return (
    <aside className="hidden flex-col border-r bg-muted/25 lg:flex">
      <div className="flex h-12 shrink-0 items-center justify-between border-b px-3">
        <div className="flex items-center gap-2">
          <List className="size-4 text-muted-foreground" />
          <span className="text-sm font-medium">题目列表</span>
        </div>
        <Button
          variant="ghost"
          size="icon-sm"
          title="隐藏题单"
          onClick={onToggle}
        >
          <ChevronLeft className="size-4" />
        </Button>
      </div>
      <div className="flex-1 overflow-y-auto">
        {loading ? (
          <div className="py-8 text-center text-xs text-muted-foreground">
            加载中…
          </div>
        ) : problems.length === 0 ? (
          <div className="py-8 text-center text-xs text-muted-foreground">
            暂无题目
          </div>
        ) : (
          <nav className="space-y-0.5 p-2">
            {problems.map((problem) => {
              const isActive = String(problem.id) === currentQuestionId
              return (
                <Link
                  key={problem.id}
                  className={`block rounded-md px-3 py-2 text-sm transition-colors ${
                    isActive
                      ? 'bg-primary/10 font-medium text-primary'
                      : 'text-foreground hover:bg-muted/60'
                  }`}
                  params={{ questionId: String(problem.id) }}
                  title={problem.title || `题目 #${problem.id}`}
                  to="/problems/$questionId"
                >
                  <span className="block truncate">
                    {problem.title || `题目 #${problem.id}`}
                  </span>
                </Link>
              )
            })}
          </nav>
        )}
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

export { Metric }
export default TrainingSidebar
