import {
  BookOpen,
  Loader2,
  RefreshCw,
  Search,
  SlidersHorizontal,
} from 'lucide-react'
import { Link } from '@tanstack/react-router'
import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react'
import { toast } from 'sonner'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Skeleton } from '@/components/ui/skeleton'
import {
  formatAcceptRate,
  parseQuestionTagInput,
  parseQuestionTags,
} from '@/lib/question-utils'
import {
  getQuestionRequestErrorMessage,
  listQuestionVOs,
  type QuestionVO,
} from '@/services/questionService'

const PAGE_SIZE = 12

function ProblemListPage() {
  const [keyword, setKeyword] = useState('')
  const [tagInput, setTagInput] = useState('')
  const [submittedKeyword, setSubmittedKeyword] = useState('')
  const [submittedTagInput, setSubmittedTagInput] = useState('')
  const [current, setCurrent] = useState(1)
  const [records, setRecords] = useState<QuestionVO[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)

  const pages = Math.max(1, Math.ceil(total / PAGE_SIZE))
  const submittedTags = useMemo(
    () => parseQuestionTagInput(submittedTagInput),
    [submittedTagInput],
  )

  const fetchQuestions = useCallback(async () => {
    setLoading(true)
    try {
      const page = await listQuestionVOs({
        current,
        pageSize: PAGE_SIZE,
        sortField: 'createTime',
        sortOrder: 'descend',
        tags: submittedTags.length > 0 ? submittedTags : undefined,
        title: submittedKeyword.trim() || undefined,
      })

      setRecords(page.records ?? [])
      setTotal(page.total ?? 0)
    } catch (error) {
      toast.error(getQuestionRequestErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [current, submittedKeyword, submittedTags])

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      void fetchQuestions()
    }, 0)

    return () => window.clearTimeout(timeout)
  }, [fetchQuestions])

  function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setCurrent(1)
    setSubmittedKeyword(keyword)
    setSubmittedTagInput(tagInput)
  }

  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-muted/20 p-4 sm:p-6">
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-4">
        <div className="flex flex-col gap-2">
          <div className="flex items-center gap-2 text-sm font-medium text-primary">
            <BookOpen className="size-4" />
            题库
          </div>
          <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <h1 className="text-2xl font-semibold tracking-tight">查找练习题目</h1>
              <p className="mt-1 text-sm text-muted-foreground">
                按标题和标签筛选题目，快速查看提交量、通过量和通过率
              </p>
            </div>
            <Button disabled={loading} onClick={fetchQuestions} variant="outline">
              {loading ? <Loader2 className="animate-spin" /> : <RefreshCw />}
              刷新
            </Button>
          </div>
        </div>

        <section className="rounded-lg border bg-background">
          <form
            className="grid gap-3 border-b p-3 md:grid-cols-[minmax(0,1fr)_minmax(14rem,20rem)_auto]"
            onSubmit={handleSearch}
          >
            <div className="relative min-w-0">
              <Search className="pointer-events-none absolute left-2.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                className="pl-8"
                onChange={(event) => setKeyword(event.target.value)}
                placeholder="搜索题目标题"
                value={keyword}
              />
            </div>
            <div className="relative min-w-0">
              <SlidersHorizontal className="pointer-events-none absolute left-2.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                className="pl-8"
                onChange={(event) => setTagInput(event.target.value)}
                placeholder="标签，例如：数组 动态规划"
                value={tagInput}
              />
            </div>
            <Button className="md:w-24" type="submit">
              查询
            </Button>
          </form>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>题目</TableHead>
                <TableHead className="hidden md:table-cell">标签</TableHead>
                <TableHead className="hidden text-right sm:table-cell">提交</TableHead>
                <TableHead className="hidden text-right sm:table-cell">通过率</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {loading ? (
                <ProblemListSkeleton />
              ) : records.length > 0 ? (
                records.map((question) => (
                  <TableRow key={question.id}>
                    <TableCell>
                      <div className="max-w-2xl">
                        {question.id ? (
                          <Link
                            className="group block min-w-0"
                            params={{ questionId: String(question.id) }}
                            to="/problems/$questionId"
                          >
                            <div className="truncate font-medium transition-colors group-hover:text-primary">
                              {question.title || '未命名题目'}
                            </div>
                            <div className="mt-1 truncate text-xs text-muted-foreground">
                              {question.content || '暂无题目描述'}
                            </div>
                          </Link>
                        ) : (
                          <>
                            <div className="truncate font-medium">
                              {question.title || '未命名题目'}
                            </div>
                            <div className="mt-1 truncate text-xs text-muted-foreground">
                              {question.content || '暂无题目描述'}
                            </div>
                          </>
                        )}
                      </div>
                    </TableCell>
                    <TableCell className="hidden md:table-cell">
                      <ProblemTagList value={question.tags} />
                    </TableCell>
                    <TableCell className="hidden text-right sm:table-cell">
                      {question.submitNum ?? 0}
                    </TableCell>
                    <TableCell className="hidden text-right sm:table-cell">
                      {formatAcceptRate(question.acceptedNum, question.submitNum)}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell className="h-40 text-center text-sm text-muted-foreground" colSpan={4}>
                    暂无题目
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

function ProblemListSkeleton() {
  return Array.from({ length: 6 }, (_, index) => (
    <TableRow key={index}>
      <TableCell>
        <div className="max-w-2xl space-y-2">
          <Skeleton className="h-4 w-48 max-w-full" />
          <Skeleton className="h-3 w-80 max-w-full" />
        </div>
      </TableCell>
      <TableCell className="hidden md:table-cell">
        <div className="flex gap-1.5">
          <Skeleton className="h-5 w-14 rounded-full" />
          <Skeleton className="h-5 w-20 rounded-full" />
          <Skeleton className="h-5 w-12 rounded-full" />
        </div>
      </TableCell>
      <TableCell className="hidden sm:table-cell">
        <Skeleton className="ml-auto h-4 w-12" />
      </TableCell>
      <TableCell className="hidden sm:table-cell">
        <Skeleton className="ml-auto h-4 w-14" />
      </TableCell>
    </TableRow>
  ))
}

function ProblemTagList({ value }: { value?: string | string[] }) {
  const tags = parseQuestionTags(value)

  if (tags.length === 0) {
    return <span className="text-sm text-muted-foreground">无标签</span>
  }

  return (
    <div className="flex max-w-xs flex-wrap gap-1">
      {tags.slice(0, 3).map((tag) => (
        <Badge className="rounded-md" key={tag} variant="secondary">
          {tag}
        </Badge>
      ))}
      {tags.length > 3 ? (
        <Badge className="rounded-md" variant="outline">
          +{tags.length - 3}
        </Badge>
      ) : null}
    </div>
  )
}

export default ProblemListPage
