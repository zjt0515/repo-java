import { Link } from '@tanstack/react-router'
import {
  ArrowLeft,
  ArrowRight,
  BookOpen,
  Heart,
  Loader2,
  PenLine,
  RefreshCw,
  Search,
  ThumbsUp,
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
import { Skeleton } from '@/components/ui/skeleton'
import { formatQuestionDateTime } from '@/lib/question-utils'
import {
  getQuestionRequestErrorMessage,
  getQuestionVO,
  type QuestionVO,
} from '@/services/questionService'
import {
  getPostRequestErrorMessage,
  listPostVOs,
  type PostVO,
} from '@/services/postService'

const PAGE_SIZE = 8

type SolutionListPageProps = {
  questionId?: string
}

function SolutionListPage({ questionId }: SolutionListPageProps) {
  const invalidQuestionId = !questionId || !/^[1-9]\d*$/.test(questionId)
  const [question, setQuestion] = useState<QuestionVO>()
  const [keyword, setKeyword] = useState('')
  const [submittedKeyword, setSubmittedKeyword] = useState('')
  const [current, setCurrent] = useState(1)
  const [records, setRecords] = useState<PostVO[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(!invalidQuestionId)
  const [error, setError] = useState(invalidQuestionId ? '题目编号无效' : '')

  const pages = Math.max(1, Math.ceil(total / PAGE_SIZE))

  const fetchSolutions = useCallback(async () => {
    if (!questionId || invalidQuestionId) {
      return
    }

    setLoading(true)
    setError('')
    try {
      const [nextQuestion, page] = await Promise.all([
        getQuestionVO(questionId),
        listPostVOs({
          current,
          pageSize: PAGE_SIZE,
          questionId,
          sortField: 'createTime',
          sortOrder: 'descend',
          title: submittedKeyword.trim() || undefined,
        }),
      ])

      setQuestion(nextQuestion)
      setRecords(page.records ?? [])
      setTotal(page.total ?? 0)
    } catch (requestError) {
      const message =
        requestError instanceof Error
          ? getPostRequestErrorMessage(requestError)
          : getQuestionRequestErrorMessage(requestError)

      setError(message)
      toast.error(message)
    } finally {
      setLoading(false)
    }
  }, [current, invalidQuestionId, questionId, submittedKeyword])

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      void fetchSolutions()
    }, 0)

    return () => window.clearTimeout(timeout)
  }, [fetchSolutions])

  function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setCurrent(1)
    setSubmittedKeyword(keyword)
  }

  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-muted/20 p-4 sm:p-6">
      <div className="mx-auto flex w-full max-w-5xl flex-col gap-4">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <div className="min-w-0">
            <Button asChild className="mb-3 w-fit" size="sm" variant="ghost">
              <Link
                params={{ questionId: questionId || '' }}
                to="/problems/$questionId"
              >
                <ArrowLeft />
                返回题目
              </Link>
            </Button>
            <div className="flex items-center gap-2 text-sm font-medium text-primary">
              <BookOpen className="size-4" />
              题解
            </div>
            <h1 className="mt-2 truncate text-2xl font-semibold tracking-tight">
              {question?.title ? `${question.title} 的题解` : '题解列表'}
            </h1>
            <p className="mt-1 text-sm text-muted-foreground">
              浏览这道题的题解、思路和讨论内容
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            <Button asChild disabled={invalidQuestionId}>
              <Link
                params={{ questionId: questionId || '' }}
                to="/problems/$questionId/solutions/new"
              >
                <PenLine />
                提交题解
              </Link>
            </Button>
            <Button disabled={loading || invalidQuestionId} onClick={fetchSolutions} variant="outline">
              {loading ? <Loader2 className="animate-spin" /> : <RefreshCw />}
              刷新
            </Button>
          </div>
        </div>

        <section className="rounded-lg border bg-background">
          <form
            className="flex flex-col gap-2 border-b p-3 sm:flex-row"
            onSubmit={handleSearch}
          >
            <div className="relative min-w-0 flex-1">
              <Search className="pointer-events-none absolute left-2.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                className="pl-8"
                disabled={invalidQuestionId}
                onChange={(event) => setKeyword(event.target.value)}
                placeholder="搜索题解标题"
                value={keyword}
              />
            </div>
            <Button disabled={invalidQuestionId} type="submit" variant="outline">
              搜索
            </Button>
          </form>

          <div className="grid gap-3 p-3">
            {loading ? (
              <SolutionListSkeleton />
            ) : error ? (
              <div className="rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
                {error}
              </div>
            ) : records.length > 0 ? (
              records.map((post) => (
                <SolutionListItem
                  key={post.id}
                  post={post}
                  questionId={questionId || ''}
                />
              ))
            ) : (
              <div className="flex h-40 items-center justify-center rounded-lg border border-dashed text-sm text-muted-foreground">
                暂无题解
              </div>
            )}
          </div>

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

function SolutionListItem({
  post,
  questionId,
}: {
  post: PostVO
  questionId: string
}) {
  const authorName = post.userVO?.userName || '匿名用户'
  const avatar = post.userVO?.userAvatar

  return (
    <article className="rounded-lg border bg-background p-4 transition-colors hover:border-primary/40">
      <div className="flex gap-3">
        {avatar ? (
          <img
            alt={authorName}
            className="size-10 rounded-full object-cover"
            src={avatar}
          />
        ) : (
          <div className="flex size-10 shrink-0 items-center justify-center rounded-full bg-muted text-sm font-medium">
            {authorName.charAt(0).toUpperCase()}
          </div>
        )}
        <div className="min-w-0 flex-1">
          <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
            <div className="min-w-0">
              {post.id ? (
                <Link
                  className="group block"
                  params={{ postId: String(post.id), questionId }}
                  to="/problems/$questionId/solutions/$postId"
                >
                  <h2 className="truncate font-medium transition-colors group-hover:text-primary">
                    {post.title || '未命名题解'}
                  </h2>
                </Link>
              ) : (
                <h2 className="truncate font-medium">
                  {post.title || '未命名题解'}
                </h2>
              )}
              <div className="mt-1 flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-muted-foreground">
                <span>{authorName}</span>
                <span>{formatQuestionDateTime(post.createTime)}</span>
              </div>
            </div>
            <div className="flex shrink-0 items-center gap-3 text-xs text-muted-foreground">
              <span className="inline-flex items-center gap-1">
                <ThumbsUp className="size-3.5" />
                {post.thumbNum ?? 0}
              </span>
              <span className="inline-flex items-center gap-1">
                <Heart className="size-3.5" />
                {post.favourNum ?? 0}
              </span>
            </div>
          </div>

          <p className="mt-2 line-clamp-2 text-sm leading-6 text-muted-foreground">
            {getPostExcerpt(post.content)}
          </p>

          <div className="mt-3 flex flex-wrap items-center justify-between gap-2">
            <SolutionTagList value={post.tags} />
            {post.id ? (
              <Button asChild size="sm" variant="ghost">
                <Link
                  params={{ postId: String(post.id), questionId }}
                  to="/problems/$questionId/solutions/$postId"
                >
                  查看详情
                  <ArrowRight />
                </Link>
              </Button>
            ) : null}
          </div>
        </div>
      </div>
    </article>
  )
}

function SolutionListSkeleton() {
  return Array.from({ length: 4 }).map((_, index) => (
    <div className="rounded-lg border bg-background p-4" key={index}>
      <div className="flex gap-3">
        <Skeleton className="size-10 rounded-full" />
        <div className="flex-1 space-y-3">
          <Skeleton className="h-4 w-56 max-w-full" />
          <Skeleton className="h-3 w-72 max-w-full" />
          <Skeleton className="h-12 w-full" />
          <div className="flex gap-2">
            <Skeleton className="h-5 w-14 rounded-full" />
            <Skeleton className="h-5 w-20 rounded-full" />
          </div>
        </div>
      </div>
    </div>
  ))
}

function SolutionTagList({ value }: { value?: string[] }) {
  const tags = value ?? []

  if (tags.length === 0) {
    return <span className="text-xs text-muted-foreground">无标签</span>
  }

  return (
    <div className="flex flex-wrap gap-1">
      {tags.slice(0, 4).map((tag) => (
        <Badge className="rounded-md" key={tag} variant="secondary">
          {tag}
        </Badge>
      ))}
      {tags.length > 4 ? (
        <Badge className="rounded-md" variant="outline">
          +{tags.length - 4}
        </Badge>
      ) : null}
    </div>
  )
}

function getPostExcerpt(value?: string) {
  const excerpt = value
    ?.replace(/```[\s\S]*?```/g, ' ')
    .replace(/[#>*_`~[\]()!-]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()

  return excerpt || '题解内容'
}

export default SolutionListPage
