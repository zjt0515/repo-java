import { Link } from '@tanstack/react-router'
import {
  ArrowLeft,
  BookOpen,
  Calendar,
  Heart,
  Hash,
  Loader2,
  ThumbsUp,
  UserRound,
} from 'lucide-react'
import { useEffect, useState, type ReactNode } from 'react'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { MarkdownViewer } from '@/components/markdown'
import { Skeleton } from '@/components/ui/skeleton'
import { formatQuestionDateTime } from '@/lib/question-utils'
import {
  getPostRequestErrorMessage,
  getPostVO,
  type PostVO,
} from '@/services/postService'

type SolutionDetailPageProps = {
  postId?: string
  questionId?: string
}

function SolutionDetailPage({ postId, questionId }: SolutionDetailPageProps) {
  const invalidQuestionId = !questionId || !/^[1-9]\d*$/.test(questionId)
  const invalidPostId = !postId || !/^[1-9]\d*$/.test(postId)
  const [post, setPost] = useState<PostVO>()
  const [loading, setLoading] = useState(!invalidQuestionId && !invalidPostId)
  const [error, setError] = useState(
    invalidQuestionId || invalidPostId ? '题解或题目编号无效' : '',
  )

  useEffect(() => {
    if (!postId || invalidQuestionId || invalidPostId) {
      return
    }

    let ignore = false

    async function fetchPost() {
      setLoading(true)
      setError('')
      try {
        const nextPost = await getPostVO(postId)
        const postQuestionId = nextPost.questionId
          ? String(nextPost.questionId)
          : undefined

        if (postQuestionId && postQuestionId !== questionId) {
          throw new Error('该题解不属于当前题目')
        }

        if (!ignore) {
          setPost(nextPost)
        }
      } catch (requestError) {
        if (!ignore) {
          setPost(undefined)
          setError(getPostRequestErrorMessage(requestError))
        }
      } finally {
        if (!ignore) {
          setLoading(false)
        }
      }
    }

    const timeout = window.setTimeout(() => {
      void fetchPost()
    }, 0)

    return () => {
      ignore = true
      window.clearTimeout(timeout)
    }
  }, [invalidPostId, invalidQuestionId, postId, questionId])

  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-muted/20 p-4 sm:p-6">
      <div className="mx-auto flex w-full max-w-5xl flex-col gap-4">
        <div className="flex flex-wrap items-center gap-2">
          <Button asChild size="sm" variant="ghost">
            <Link
              params={{ questionId: questionId || '' }}
              to="/problems/$questionId/solutions"
            >
              <ArrowLeft />
              返回题解列表
            </Link>
          </Button>
          <Button asChild size="sm" variant="outline">
            <Link
              params={{ questionId: questionId || '' }}
              to="/problems/$questionId"
            >
              <BookOpen />
              查看题目
            </Link>
          </Button>
        </div>

        {loading ? (
          <SolutionDetailSkeleton />
        ) : error ? (
          <div className="rounded-lg border border-destructive/30 bg-background p-5 text-sm text-destructive">
            {error}
          </div>
        ) : post ? (
          <div className="grid gap-4 lg:grid-cols-[minmax(0,1fr)_18rem]">
            <article className="rounded-lg border bg-background p-4 sm:p-6">
              <div className="space-y-4">
                <div>
                  <SolutionTagList value={post.tags} />
                  <h1 className="mt-3 text-2xl font-semibold tracking-tight">
                    {post.title || '未命名题解'}
                  </h1>
                  <div className="mt-3 flex flex-wrap items-center gap-x-4 gap-y-2 text-sm text-muted-foreground">
                    <span className="inline-flex items-center gap-1.5">
                      <UserRound className="size-4" />
                      {post.userVO?.userName || '匿名用户'}
                    </span>
                    <span className="inline-flex items-center gap-1.5">
                      <Calendar className="size-4" />
                      {formatQuestionDateTime(post.updateTime || post.createTime)}
                    </span>
                  </div>
                </div>

                <MarkdownViewer
                  emptyText="题解内容"
                  value={post.content}
                />
              </div>
            </article>

            <aside className="space-y-4">
              <section className="rounded-lg border bg-background p-4">
                <h2 className="text-sm font-medium">互动数据</h2>
                <div className="mt-3 grid grid-cols-2 gap-2">
                  <MetricCard
                    icon={<ThumbsUp className="size-4" />}
                    label="点赞"
                    value={post.thumbNum ?? 0}
                  />
                  <MetricCard
                    icon={<Heart className="size-4" />}
                    label="收藏"
                    value={post.favourNum ?? 0}
                  />
                </div>
              </section>

              <section className="rounded-lg border bg-background p-4">
                <h2 className="text-sm font-medium">关联题目</h2>
                <div className="mt-3 space-y-2">
                  {/* <div className="flex items-center gap-2 text-xs text-muted-foreground">
                    <Hash className="size-3.5" />
                    {post.questionId || questionId}
                  </div> */}
                  <div className="text-sm font-medium">
                    {post.questionVO?.title || '当前题目'}
                  </div>
                  <Button asChild className="mt-2 w-full" size="sm" variant="outline">
                    <Link
                      params={{ questionId: questionId || String(post.questionId || '') }}
                      to="/problems/$questionId"
                    >
                      查看题目
                    </Link>
                  </Button>
                </div>
              </section>
            </aside>
          </div>
        ) : null}
      </div>
    </main>
  )
}

function SolutionDetailSkeleton() {
  return (
    <div className="grid gap-4 lg:grid-cols-[minmax(0,1fr)_18rem]">
      <div className="rounded-lg border bg-background p-4 sm:p-6">
        <div className="space-y-4">
          <div className="flex gap-2">
            <Skeleton className="h-5 w-14 rounded-full" />
            <Skeleton className="h-5 w-20 rounded-full" />
          </div>
          <Skeleton className="h-7 w-80 max-w-full" />
          <Skeleton className="h-4 w-56 max-w-full" />
          <Skeleton className="h-80 w-full" />
        </div>
      </div>
      <div className="space-y-4">
        <Skeleton className="h-32 rounded-lg" />
        <Skeleton className="h-40 rounded-lg" />
      </div>
    </div>
  )
}

function SolutionTagList({ value }: { value?: string[] }) {
  const tags = value ?? []

  if (tags.length === 0) {
    return (
      <Badge className="rounded-md" variant="outline">
        无标签
      </Badge>
    )
  }

  return (
    <div className="flex flex-wrap gap-1">
      {tags.map((tag) => (
        <Badge className="rounded-md" key={tag} variant="secondary">
          {tag}
        </Badge>
      ))}
    </div>
  )
}

function MetricCard({
  icon,
  label,
  value,
}: {
  icon: ReactNode
  label: string
  value: number
}) {
  return (
    <div className="rounded-lg border bg-muted/20 p-3">
      <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
        {icon}
        {label}
      </div>
      <div className="mt-2 text-lg font-semibold">{value}</div>
    </div>
  )
}

export default SolutionDetailPage
