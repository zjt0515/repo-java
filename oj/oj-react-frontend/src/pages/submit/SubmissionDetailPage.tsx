import { Link } from '@tanstack/react-router'
import {
  ArrowLeft,
  Clock,
  Code2,
  Cpu,
  FileText,
  Loader2,
} from 'lucide-react'
import { useCallback, useEffect, useState } from 'react'
import { toast } from 'sonner'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  getQuestionRequestErrorMessage,
  getQuestionSubmission,
  type QuestionSubmitVO,
} from '@/services/questionService'
import {
  formatJudgeMemory,
  formatJudgeTime,
  formatLanguage,
  getResultMeta,
  getStatusMeta,
} from '@/lib/submissionDisplay'

interface SubmissionDetailPageProps {
  submissionId: string
}

function SubmissionDetailPage({ submissionId }: SubmissionDetailPageProps) {

  const [submission, setSubmission] = useState<QuestionSubmitVO | null>(null)
  const [loading, setLoading] = useState(false)

  const fetchSubmission = useCallback(async (showLoading = true) => {
    if (!submissionId) return

    if (showLoading) {
      setLoading(true)
    }
    try {
      const data = await getQuestionSubmission(submissionId)
      if (data) {
        setSubmission(data)
      } else {
        toast.error('未找到该提交记录')
      }
    } catch (error) {
      toast.error(getQuestionRequestErrorMessage(error))
    } finally {
      if (showLoading) {
        setLoading(false)
      }
    }
  }, [submissionId])

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      void fetchSubmission()
    }, 0)

    return () => window.clearTimeout(timeout)
  }, [fetchSubmission])

  // 轮询：当状态为待判题(0)或判题中(1)时，自动刷新
  useEffect(() => {
    if (!submission) return
    if (submission.status === 0 || submission.status === 1) {
      const interval = window.setInterval(() => {
        void fetchSubmission(false)
      }, 2000)
      return () => window.clearInterval(interval)
    }
  }, [submission, fetchSubmission])

  if (loading) {
    return (
      <main className="flex min-h-[calc(100svh-3.5rem)] items-center justify-center bg-muted/20">
        <div className="flex flex-col items-center gap-3">
          <Loader2 className="size-8 animate-spin text-primary" />
          <p className="text-sm text-muted-foreground">正在加载提交详情</p>
        </div>
      </main>
    )
  }

  if (!submission) {
    return (
      <main className="flex min-h-[calc(100svh-3.5rem)] items-center justify-center bg-muted/20 p-4">
        <div className="flex flex-col items-center gap-4 text-center">
          <FileText className="size-12 text-muted-foreground/50" />
          <div className="space-y-1">
            <h2 className="text-lg font-semibold">未找到提交记录</h2>
            <p className="text-sm text-muted-foreground">
              该提交记录不存在或已被删除
            </p>
          </div>
          <Button asChild variant="outline">
            <Link to="/user/submissions">
              <ArrowLeft />
              返回提交列表
            </Link>
          </Button>
        </div>
      </main>
    )
  }

  const statusMeta = getStatusMeta(submission.status)
  const resultMeta = getResultMeta(submission.judgeInfo?.message)

  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-muted/20 p-4 sm:p-6">
      <div className="mx-auto flex w-full max-w-5xl flex-col gap-6">
        {/* Header */}
        <div className="flex items-center gap-4">
          <Button asChild size="sm" variant="outline">
            <Link to="/user/submissions">
              <ArrowLeft />
              返回
            </Link>
          </Button>
          <div>
            <h1 className="text-xl font-semibold tracking-tight">
              提交详情
            </h1>
            <p className="text-sm text-muted-foreground">
              提交编号: {submission.id}
            </p>
          </div>
        </div>

        {/* Info Card */}
        <section className="rounded-lg border bg-background p-5">
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">

            {/* Language */}
            <div className="space-y-1">
              <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                <Code2 className="size-3.5" />
                语言
              </div>
              <span className="font-medium">
                {formatLanguage(submission.language)}
              </span>
            </div>

            {/* Status */}
            <div className="space-y-1">
              <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                <Clock className="size-3.5" />
                状态
              </div>
              <Badge className={statusMeta.className} variant={statusMeta.variant}>
                {statusMeta.label}
              </Badge>
            </div>

            {/* 结果 */}
            <div className="space-y-1">
              <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                <Clock className="size-3.5" />
                结果
              </div>
              <Badge className={resultMeta.className} variant={resultMeta.variant}>
                {resultMeta.label}
              </Badge>
            </div>


            {/* Submit Time */}
            <div className="space-y-1">
              <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                <Clock className="size-3.5" />
                提交时间
              </div>
              <span className="font-medium">
                {submission.createTime
                  ? new Date(submission.createTime).toLocaleString('zh-CN')
                  : '-'}
              </span>
            </div>

          </div>
        </section>

        {/* Metrics */}
        <section className="grid gap-4 sm:grid-cols-2">
          <div className="flex items-center gap-4 rounded-lg border bg-background p-5">
            <div className="flex size-10 items-center justify-center rounded-full bg-primary/10">
              <Clock className="size-5 text-primary" />
            </div>
            <div>
              <div className="text-sm text-muted-foreground">执行耗时</div>
              <div className="text-2xl font-semibold">
                {formatJudgeTime(submission.judgeInfo?.time, submission.judgeInfo?.message)}
              </div>
            </div>
          </div>
          <div className="flex items-center gap-4 rounded-lg border bg-background p-5">
            <div className="flex size-10 items-center justify-center rounded-full bg-primary/10">
              <Cpu className="size-5 text-primary" />
            </div>
            <div>
              <div className="text-sm text-muted-foreground">内存占用</div>
              <div className="text-2xl font-semibold">
                {formatJudgeMemory(submission.judgeInfo?.memory, submission.judgeInfo?.message)}
              </div>
            </div>
          </div>
        </section>

        {/* Code */}
        <section className="rounded-lg border bg-background">
          <div className="flex items-center justify-between border-b px-5 py-3">
            <div className="flex items-center gap-2 text-sm font-medium">
              <Code2 className="size-4 text-muted-foreground" />
              提交代码
            </div>
            <span className="text-xs text-muted-foreground">
              {formatLanguage(submission.language)}
            </span>
          </div>
          <div className="overflow-x-auto p-0">
            <pre className="max-h-[60svh] overflow-auto p-5 font-mono text-sm leading-6">
              {submission.code || '// 无代码'}
            </pre>
          </div>
        </section>
      </div>
    </main>
  )
}

export default SubmissionDetailPage
