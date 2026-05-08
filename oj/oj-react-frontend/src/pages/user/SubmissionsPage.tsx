import { Link } from '@tanstack/react-router'
import {
  ClipboardList,
  Code2,
  Loader2,
  RefreshCw,
  Search,
} from 'lucide-react'
import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { toast } from 'sonner'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  getQuestionRequestErrorMessage,
  getQuestionVO,
  listQuestionSubmissions,
  type QuestionVO,
  type QuestionSubmitVO,
} from '@/services/questionService'

const PAGE_SIZE = 10
const ALL_VALUE = 'all'

const languageOptions = [
  { label: '全部语言', value: ALL_VALUE },
  { label: 'Java', value: 'java' },
  { label: 'C++', value: 'cpp' },
  { label: 'Python', value: 'python' },
  { label: 'JavaScript', value: 'javascript' },
]

const statusOptions = [
  { label: '全部状态', value: ALL_VALUE },
  { label: '待判题', value: '0' },
  { label: '判题中', value: '1' },
  { label: '已判题', value: '2' },
  { label: '失败', value: '3' },
]

function SubmissionsPage() {
  const [questionId, setQuestionId] = useState('')
  const [language, setLanguage] = useState(ALL_VALUE)
  const [status, setStatus] = useState(ALL_VALUE)
  const [submittedQuestionId, setSubmittedQuestionId] = useState('')
  const [submittedLanguage, setSubmittedLanguage] = useState(ALL_VALUE)
  const [submittedStatus, setSubmittedStatus] = useState(ALL_VALUE)
  const [current, setCurrent] = useState(1)
  const [records, setRecords] = useState<QuestionSubmitVO[]>([])
  const [total, setTotal] = useState(0)
  // loading state
  const [loading, setLoading] = useState(false)

  const pages = Math.max(1, Math.ceil(total / PAGE_SIZE))

  const fetchSubmissions = useCallback(async () => {
    setLoading(true)
    try {
      const page = await listQuestionSubmissions({
        current,
        language: submittedLanguage === ALL_VALUE ? undefined : submittedLanguage,
        pageSize: PAGE_SIZE,
        questionId: submittedQuestionId.trim() || undefined,
        sortField: 'id',
        sortOrder: 'descend',
        status:
          submittedStatus === ALL_VALUE ? undefined : Number(submittedStatus),
      })

      const nextRecords = page.records ?? []
      const hydratedRecords = await hydrateSubmissionQuestions(nextRecords)

      setRecords(hydratedRecords)
      setTotal(page.total ?? 0)
    } catch (error) {
      toast.error(getQuestionRequestErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [current, submittedLanguage, submittedQuestionId, submittedStatus])

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      void fetchSubmissions()
    }, 0)

    return () => window.clearTimeout(timeout)
  }, [fetchSubmissions])

  function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setCurrent(1)
    setSubmittedQuestionId(questionId)
    setSubmittedLanguage(language)
    setSubmittedStatus(status)
  }

  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-muted/20 p-4 sm:p-6">
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-4">
        <div className="flex flex-col gap-2">
          <div className="flex items-center gap-2 text-sm font-medium text-primary">
            <ClipboardList className="size-4" />
            提交记录
          </div>
          <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <h1 className="text-2xl font-semibold tracking-tight">
                查看代码提交列表
              </h1>
              <p className="mt-1 text-sm text-muted-foreground">
                按题目、语言和判题状态筛选提交记录，快速查看耗时、内存和代码内容
              </p>
            </div>
            <Button disabled={loading} onClick={fetchSubmissions} variant="outline">
              {loading ? <Loader2 className="animate-spin" /> : <RefreshCw />}
              刷新
            </Button>
          </div>
        </div>

        <section className="rounded-lg border bg-background">
          <form
            className="grid gap-3 border-b p-3 md:grid-cols-[minmax(0,1fr)_11rem_11rem_auto]"
            onSubmit={handleSearch}
          >
            <div className="relative min-w-0">
              <Search className="pointer-events-none absolute left-2.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                className="pl-8"
                inputMode="numeric"
                onChange={(event) => setQuestionId(event.target.value)}
                placeholder="题目 ID"
                value={questionId}
              />
            </div>
            <Select value={language} onValueChange={setLanguage}>
              <SelectTrigger className="w-full bg-muted/40">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {languageOptions.map((item) => (
                  <SelectItem key={item.value} value={item.value}>
                    {item.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Select value={status} onValueChange={setStatus}>
              <SelectTrigger className="w-full bg-muted/40">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {statusOptions.map((item) => (
                  <SelectItem key={item.value} value={item.value}>
                    {item.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Button className="md:w-24" type="submit">
              查询
            </Button>
          </form>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>题目</TableHead>
                <TableHead className="hidden md:table-cell">语言</TableHead>
                <TableHead>状态</TableHead>
                <TableHead className="hidden text-right sm:table-cell">耗时</TableHead>
                <TableHead className="hidden text-right sm:table-cell">内存</TableHead>
                <TableHead className="hidden lg:table-cell">判题信息</TableHead>
                <TableHead className="w-24 text-right">代码</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell className="h-40 text-center" colSpan={7}>
                    <span className="inline-flex items-center gap-2 text-sm text-muted-foreground">
                      <Loader2 className="size-4 animate-spin" />
                      正在加载提交记录
                    </span>
                  </TableCell>
                </TableRow>
              ) : records.length > 0 ? (
                records.map((submission) => (
                  <TableRow key={submission.id}>
                    <TableCell>
                      <SubmissionQuestion submission={submission} />
                    </TableCell>
                    <TableCell className="hidden md:table-cell">
                      {formatLanguage(submission.language)}
                    </TableCell>
                    <TableCell>
                      <SubmissionStatusBadge status={submission.status} />
                    </TableCell>
                    <TableCell className="hidden text-right sm:table-cell">
                      {formatJudgeTime(submission.judgeInfo?.time)}
                    </TableCell>
                    <TableCell className="hidden text-right sm:table-cell">
                      {formatJudgeMemory(submission.judgeInfo?.memory)}
                    </TableCell>
                    <TableCell className="hidden max-w-xs truncate text-muted-foreground lg:table-cell">
                      {submission.judgeInfo?.message || '-'}
                    </TableCell>
                    <TableCell>
                      <div className="flex justify-end">
                        <SubmissionCodeDialog submission={submission} />
                      </div>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell className="h-40 text-center text-sm text-muted-foreground" colSpan={7}>
                    暂无提交记录
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>

          <div className="flex flex-col gap-3 border-t p-3 sm:flex-row sm:items-center sm:justify-between">
            <div className="text-sm text-muted-foreground">
              共 {total} 条，第 {current} / {pages} 页
            </div>
            <Pagination className="sm:mx-0 sm:w-auto">
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious
                    aria-disabled={current <= 1}
                    className={current <= 1 ? 'pointer-events-none opacity-50' : undefined}
                    href="#"
                    onClick={(event) => {
                      event.preventDefault()
                      setCurrent((page) => Math.max(1, page - 1))
                    }}
                    text="上一页"
                  />
                </PaginationItem>
                <PaginationItem>
                  <PaginationNext
                    aria-disabled={current >= pages}
                    className={current >= pages ? 'pointer-events-none opacity-50' : undefined}
                    href="#"
                    onClick={(event) => {
                      event.preventDefault()
                      setCurrent((page) => Math.min(pages, page + 1))
                    }}
                    text="下一页"
                  />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          </div>
        </section>
      </div>
    </main>
  )
}

async function hydrateSubmissionQuestions(records: QuestionSubmitVO[]) {
  const missingQuestionIds = Array.from(
    new Set(
      records
        .filter((submission) => !submission.questionVO?.title)
        .map(getSubmissionQuestionId)
        .filter((id): id is string => Boolean(id)),
    ),
  )

  if (missingQuestionIds.length === 0) {
    return records
  }

  const questionEntries = await Promise.all(
    missingQuestionIds.map(async (questionId) => {
      try {
        return [questionId, await getQuestionVO(questionId)] as const
      } catch {
        return [questionId, undefined] as const
      }
    }),
  )

  const questionsById = new Map<string, QuestionVO>(
    questionEntries.filter(
      (entry): entry is readonly [string, QuestionVO] => Boolean(entry[1]),
    ),
  )

  return records.map((submission) => {
    if (submission.questionVO?.title) {
      return submission
    }

    const questionId = getSubmissionQuestionId(submission)
    const question = questionId ? questionsById.get(questionId) : undefined

    return question ? { ...submission, questionVO: question } : submission
  })
}

function getSubmissionQuestionId(submission: QuestionSubmitVO) {
  const id = submission.questionId ?? submission.questionVO?.id

  return id === undefined || id === null ? undefined : String(id)
}

function SubmissionQuestion({ submission }: { submission: QuestionSubmitVO }) {
  const question = submission.questionVO
  const questionId = getSubmissionQuestionId(submission)
  const title = question?.title || '未命名题目'

  if (questionId) {
    return (
      <Link
        className="group block min-w-0"
        params={{ questionId }}
        to="/problems/$questionId"
      >
        <div className="truncate font-medium transition-colors group-hover:text-primary">
          {title}
        </div>
      </Link>
    )
  }

  return (
    <div className="min-w-0">
      <div className="truncate font-medium">
        {question ? title : '题目信息未返回'}
      </div>
    </div>
  )
}

function SubmissionStatusBadge({ status }: { status?: number }) {
  const item = getStatusMeta(status)

  return (
    <Badge className={item.className} variant={item.variant}>
      {item.label}
    </Badge>
  )
}

function SubmissionCodeDialog({ submission }: { submission: QuestionSubmitVO }) {
  const code = submission.code || ''

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button disabled={!code} size="sm" variant="ghost">
          <Code2 />
          查看
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-3xl">
        <DialogHeader>
          <DialogTitle>提交代码</DialogTitle>
          <DialogDescription>
            {formatLanguage(submission.language)} · {getStatusMeta(submission.status).label}
          </DialogDescription>
        </DialogHeader>
        <pre className="max-h-[60svh] overflow-auto rounded-lg border bg-muted/30 p-3 font-mono text-xs leading-5">
          {code}
        </pre>
      </DialogContent>
    </Dialog>
  )
}

function getStatusMeta(status?: number) {
  switch (status) {
    case 0:
      return {
        className: 'border-amber-500/30 bg-amber-500/10 text-amber-700 dark:text-amber-300',
        label: '待判题',
        variant: 'outline' as const,
      }
    case 1:
      return {
        className: 'border-sky-500/30 bg-sky-500/10 text-sky-700 dark:text-sky-300',
        label: '判题中',
        variant: 'outline' as const,
      }
    case 2:
      return {
        className: 'border-emerald-500/30 bg-emerald-500/10 text-emerald-700 dark:text-emerald-300',
        label: '成功',
        variant: 'outline' as const,
      }
    case 3:
      return {
        className: undefined,
        label: '失败',
        variant: 'destructive' as const,
      }
    default:
      return {
        className: undefined,
        label: '未知',
        variant: 'secondary' as const,
      }
  }
}

function formatLanguage(value?: string) {
  if (!value) {
    return '-'
  }

  return languageOptions.find((item) => item.value === value)?.label ?? value
}

function formatJudgeTime(value?: number) {
  return value === undefined || value === null ? '-' : `${value} ms`
}

function formatJudgeMemory(value?: number) {
  return value === undefined || value === null ? '-' : `${value} MB`
}

export default SubmissionsPage
