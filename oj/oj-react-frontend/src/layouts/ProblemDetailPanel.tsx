import { Link } from '@tanstack/react-router'
import { BookOpen, ChevronRight, FileText, Loader2 } from 'lucide-react'
import { MarkdownViewer } from '@/components/markdown'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  formatAcceptRate,
  parseQuestionTags,
} from '@/lib/question-utils'
import type { Question } from '@/services/questionService'
import { Metric } from './TrainingSidebar'

type ProblemDetailPanelProps = {
  error?: string
  loading?: boolean
  onToggleSidebar?: () => void
  question?: Question
  questionId?: string
  showSidebarToggle?: boolean
}

type JudgeCaseItem = {
  input?: string
  output?: string
}

const fallbackQuestion: Question = {
  acceptedNum: 16900,
  content: '给定一个整数数组和一个目标值，找出数组中两个数的和，使其最接近目标值，并返回这个和。',
  favourNum: 3200,
  id: '1024',
  submitNum: 24800,
  tags: '["中等","双指针","排序"]',
  title: '两数之和最接近目标值',
}

const fallbackTestCases = [
  { name: '示例 1', input: '[2, 7, 11, 15], 10', output: '9' },
  { name: '边界值', input: '[-4, -1, 6, 9], 1', output: '2' },
]

function ProblemDetailPanel({
  error,
  loading = false,
  onToggleSidebar,
  question,
  questionId,
  showSidebarToggle = false,
}: ProblemDetailPanelProps) {
  const displayQuestion = question ?? fallbackQuestion
  const tags = parseQuestionTags(displayQuestion.tags)
  const testCases = parseJudgeCases(displayQuestion.judgeCase, !question)

  return (
    <section className="border-r">
      <div className="flex h-12 items-center justify-between gap-2 border-b px-4">
        <div className="flex items-center gap-2">
          {showSidebarToggle && (
            <Button
              variant="ghost"
              size="icon-sm"
              title="显示题单"
              onClick={onToggleSidebar}
            >
              <ChevronRight className="size-4" />
            </Button>
          )}
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <BookOpen className="size-4" />
            <span>题目详情</span>
          </div>
        </div>
        {questionId ? (
          <Button asChild size="sm" variant="ghost">
            <Link
              params={{ questionId }}
              to="/problems/$questionId/solutions"
            >
              <FileText />
              题解
            </Link>
          </Button>
        ) : null}
      </div>
      <div className="space-y-5 p-4 sm:p-5">
        {loading ? (
          <div className="flex min-h-64 items-center justify-center text-sm text-muted-foreground">
            <Loader2 className="mr-2 size-4 animate-spin" />
            正在加载题目
          </div>
        ) : error ? (
          <div className="rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
            {error}
          </div>
        ) : (
          <>
            <div className="space-y-3">
              <div className="flex flex-wrap items-center gap-2">
                {tags.length > 0 ? (
                  tags.map((tag) => (
                    <Badge className="rounded-md" key={tag} variant="secondary">
                      {tag}
                    </Badge>
                  ))
                ) : (
                  <Badge className="rounded-md" variant="outline">
                    无标签
                  </Badge>
                )}
              </div>
              <h1 className="text-2xl font-semibold tracking-tight">
                {displayQuestion.title || '未命名题目'}
              </h1>
              <MarkdownViewer value={displayQuestion.content} />
            </div>

            <div className="grid grid-cols-3 gap-2">
              <Metric label="提交" value={formatMetric(displayQuestion.submitNum)} />
              <Metric label="通过" value={formatMetric(displayQuestion.acceptedNum)} />
              <Metric
                label="通过率"
                value={formatAcceptRate(
                  displayQuestion.acceptedNum,
                  displayQuestion.submitNum,
                )}
              />
            </div>

            <Tabs defaultValue="examples" className="gap-3">
              <TabsList>
                <TabsTrigger value="examples">
                  <FileText />
                  示例
                </TabsTrigger>
                <TabsTrigger value="limits">限制</TabsTrigger>
                <TabsTrigger value="notes">提示</TabsTrigger>
              </TabsList>
              <TabsContent value="examples" className="space-y-2">
                {testCases.length > 0 ? (
                  testCases.map((item, index) => (
                    <div
                      className="rounded-lg border bg-muted/20 p-3"
                      key={item.name}
                    >
                      <div className="text-sm font-medium">
                        {item.name || `示例 ${index + 1}`}
                      </div>
                      <dl className="mt-2 grid gap-2 text-sm">
                        <div>
                          <dt className="text-xs text-muted-foreground">输入</dt>
                          <dd className="mt-1 whitespace-pre-wrap font-mono text-xs">
                            {item.input || '-'}
                          </dd>
                        </div>
                        <div>
                          <dt className="text-xs text-muted-foreground">输出</dt>
                          <dd className="mt-1 whitespace-pre-wrap font-mono text-xs">
                            {item.output || '-'}
                          </dd>
                        </div>
                      </dl>
                    </div>
                  ))
                ) : (
                  <div className="rounded-lg border bg-muted/20 p-3 text-sm text-muted-foreground">
                    暂无公开样例
                  </div>
                )}
              </TabsContent>
              <TabsContent
                className="rounded-lg border bg-muted/20 p-3"
                value="limits"
              >
                <ul className="space-y-2 text-sm text-muted-foreground">
                  {formatJudgeConfig(displayQuestion.judgeConfig).map((item) => (
                    <li key={item}>{item}</li>
                  ))}
                </ul>
              </TabsContent>
              <TabsContent
                className="rounded-lg border bg-muted/20 p-3"
                value="notes"
              >
                <p className="text-sm leading-6 text-muted-foreground">
                  提交前建议先使用样例运行代码，确认输入输出格式和边界情况。
                </p>
              </TabsContent>
            </Tabs>
          </>
        )}
      </div>
    </section>
  )
}

function parseJudgeCases(value?: string, useFallback = false) {
  if (!value) {
    return useFallback ? fallbackTestCases : []
  }

  try {
    const parsed: unknown = JSON.parse(value)

    if (Array.isArray(parsed)) {
      return parsed.map((item, index) => {
        const judgeCase = item as JudgeCaseItem

        return {
          input: judgeCase.input ?? '',
          name: `示例 ${index + 1}`,
          output: judgeCase.output ?? '',
        }
      })
    }
  } catch {
    return []
  }

  return []
}

function formatJudgeConfig(value?: string) {
  if (!value) {
    return ['暂无限制信息']
  }

  try {
    const parsed = JSON.parse(value) as {
      memoryLimit?: number
      stackLimit?: number
      timeLimit?: number
    }
    const items = [
      parsed.timeLimit ? `时间限制：${parsed.timeLimit} ms` : '',
      parsed.memoryLimit ? `内存限制：${parsed.memoryLimit} MB` : '',
      parsed.stackLimit ? `栈限制：${parsed.stackLimit} MB` : '',
    ].filter(Boolean)

    return items.length > 0 ? items : ['暂无限制信息']
  } catch {
    return ['暂无限制信息']
  }
}

function formatMetric(value?: number) {
  if (!value) {
    return '0'
  }

  if (value >= 1000) {
    return `${(value / 1000).toFixed(1)}k`
  }

  return String(value)
}

export default ProblemDetailPanel
