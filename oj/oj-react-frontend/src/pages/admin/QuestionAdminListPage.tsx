import { Link } from '@tanstack/react-router'
import {
  Loader2,
  Pencil,
  Plus,
  RefreshCw,
  Search,
  Trash2,
} from 'lucide-react'
import { useCallback, useEffect, useState, type FormEvent } from 'react'
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
import {
  deleteQuestion,
  getQuestionRequestErrorMessage,
  listQuestions,
  type Question,
} from '@/services/questionService'
import { formatQuestionDateTime, parseQuestionTags } from '@/lib/question-utils'
import QuestionAdminListSkeleton from './QuestionAdminListSkeleton'

const PAGE_SIZE = 10

function QuestionAdminListPage() {
  const [keyword, setKeyword] = useState('')
  const [submittedKeyword, setSubmittedKeyword] = useState('')
  const [current, setCurrent] = useState(1)
  const [records, setRecords] = useState<Question[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)
  const [deletingId, setDeletingId] = useState<string | null>(null)

  const pages = Math.max(1, Math.ceil(total / PAGE_SIZE))

  const fetchQuestions = useCallback(async () => {
    setLoading(true)
    try {
      const page = await listQuestions({
        current,
        pageSize: PAGE_SIZE,
        sortField: 'createTime',
        sortOrder: 'descend',
        title: submittedKeyword.trim() || undefined,
      })

      setRecords(page.records ?? [])
      setTotal(page.total ?? 0)
    } catch (error) {
      toast.error(getQuestionRequestErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [current, submittedKeyword])

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
  }

  async function handleDelete(question: Question) {
    if (!question.id) {
      return
    }

    const confirmed = window.confirm(`确认删除题目「${question.title || question.id}」？`)

    if (!confirmed) {
      return
    }

    setDeletingId(question.id)
    try {
      await deleteQuestion({ id: question.id })
      toast.success('题目已删除')
      await fetchQuestions()
    } catch (error) {
      toast.error(getQuestionRequestErrorMessage(error))
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-muted/20 p-4 sm:p-6">
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-4">

        {/* Buttons */}
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">题目管理</h1>
            <p className="mt-1 text-sm text-muted-foreground">
              管理题库中的题目内容、标签、判题配置和样例数据
            </p>
          </div>
          <Button asChild>
            <Link to="/admin/questions/new">
              <Plus />
              新增题目
            </Link>
          </Button>
        </div>

        {/* Search */}
        <section className="rounded-lg border bg-background">
          <div className="flex flex-col gap-3 border-b p-3 sm:flex-row sm:items-center sm:justify-between">
            <form className="flex w-full gap-2 sm:max-w-md" onSubmit={handleSearch}>
              <div className="relative flex-1">
                <Search className="pointer-events-none absolute left-2.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  className="pl-8"
                  onChange={(event) => setKeyword(event.target.value)}
                  placeholder="按题目标题搜索"
                  value={keyword}
                />
              </div>
              <Button type="submit" variant="outline">
                搜索
              </Button>
            </form>
            <Button disabled={loading} onClick={fetchQuestions} variant="ghost">
              {loading ? <Loader2 className="animate-spin" /> : <RefreshCw />}
              刷新
            </Button>
          </div>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-20">ID</TableHead>
                <TableHead>题目</TableHead>
                <TableHead className="hidden md:table-cell">标签</TableHead>
                <TableHead className="hidden text-right sm:table-cell">提交</TableHead>
                <TableHead className="hidden text-right sm:table-cell">通过</TableHead>
                <TableHead className="hidden lg:table-cell">更新时间</TableHead>
                <TableHead className="w-32 text-right">操作</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {loading ? (
                <QuestionAdminListSkeleton />
              ) : records.length > 0 ? (
                records.map((question) => (
                  <TableRow key={question.id}>
                    <TableCell className="font-mono text-xs text-muted-foreground">
                      {question.id}
                    </TableCell>
                    <TableCell>
                      <div className="max-w-xl">
                        <div className="truncate font-medium">
                          {question.title || '未命名题目'}
                        </div>
                        <div className="mt-1 truncate text-xs text-muted-foreground">
                          {question.content || '暂无题目描述'}
                        </div>
                      </div>
                    </TableCell>
                    <TableCell className="hidden md:table-cell">
                      <TagList value={question.tags} />
                    </TableCell>
                    <TableCell className="hidden text-right sm:table-cell">
                      {question.submitNum ?? 0}
                    </TableCell>
                    <TableCell className="hidden text-right sm:table-cell">
                      {question.acceptedNum ?? 0}
                    </TableCell>
                    <TableCell className="hidden text-muted-foreground lg:table-cell">
                      {formatQuestionDateTime(question.updateTime)}
                    </TableCell>
                    <TableCell>
                      <div className="flex justify-end gap-1">
                        {question.id ? (
                          <Button asChild size="icon-sm" variant="ghost">
                            <Link
                              params={{ questionId: String(question.id) }}
                              to="/admin/questions/$questionId/edit"
                            >
                              <Pencil />
                            </Link>
                          </Button>
                        ) : null}
                        <Button
                          disabled={deletingId === question.id}
                          onClick={() => void handleDelete(question)}
                          size="icon-sm"
                          variant="destructive"
                        >
                          {deletingId === question.id ? (
                            <Loader2 className="animate-spin" />
                          ) : (
                            <Trash2 />
                          )}
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell className="h-40 text-center text-sm text-muted-foreground" colSpan={7}>
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



function TagList({ value }: { value?: string }) {
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

export default QuestionAdminListPage
