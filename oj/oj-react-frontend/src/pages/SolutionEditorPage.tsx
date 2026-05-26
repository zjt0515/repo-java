import { Link, useNavigate } from '@tanstack/react-router'
import { ArrowLeft, BookOpen, Loader2, Save } from 'lucide-react'
import { useEffect, useState, type FormEvent } from 'react'
import { toast } from 'sonner'
import { MarkdownEditor } from '@/components/markdown'
import { Button } from '@/components/ui/button'
import {
  Field,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { parseQuestionTagInput } from '@/lib/question-utils'
import {
  getQuestionRequestErrorMessage,
  getQuestionVO,
  type QuestionVO,
} from '@/services/questionService'
import {
  addPost,
  getPostRequestErrorMessage,
  type LongResponseData,
} from '@/services/postService'

type SolutionEditorPageProps = {
  questionId?: string
}

type SolutionFormState = {
  content: string
  tags: string
  title: string
}

const emptyForm: SolutionFormState = {
  content: '',
  tags: '',
  title: '',
}

function SolutionEditorPage({ questionId }: SolutionEditorPageProps) {
  const navigate = useNavigate()
  const invalidQuestionId = !questionId || !/^[1-9]\d*$/.test(questionId)
  const [question, setQuestion] = useState<QuestionVO>()
  const [form, setForm] = useState<SolutionFormState>(emptyForm)
  const [loading, setLoading] = useState(!invalidQuestionId)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(invalidQuestionId ? '题目编号无效' : '')

  useEffect(() => {
    if (!questionId || invalidQuestionId) {
      return
    }

    let ignore = false

    async function fetchQuestion() {
      setLoading(true)
      setError('')
      try {
        const nextQuestion = await getQuestionVO(questionId)

        if (!ignore) {
          setQuestion(nextQuestion)
        }
      } catch (requestError) {
        const message = getQuestionRequestErrorMessage(requestError)

        if (!ignore) {
          setError(message)
          toast.error(message)
        }
      } finally {
        if (!ignore) {
          setLoading(false)
        }
      }
    }

    const timeout = window.setTimeout(() => {
      void fetchQuestion()
    }, 0)

    return () => {
      ignore = true
      window.clearTimeout(timeout)
    }
  }, [invalidQuestionId, questionId])

  function updateField<K extends keyof SolutionFormState>(
    key: K,
    value: SolutionFormState[K],
  ) {
    setForm((current) => ({ ...current, [key]: value }))
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!questionId || invalidQuestionId) {
      setError('题目编号无效')
      return
    }

    const title = form.title.trim()
    const content = form.content.trim()

    if (!title) {
      setError('请填写题解标题')
      return
    }

    if (!content) {
      setError('请填写题解内容')
      return
    }

    setSubmitting(true)
    setError('')
    try {
      const createdId = await addPost({
        content,
        questionId,
        tags: parseQuestionTagInput(form.tags),
        title,
      })
      const postId = getRoutePostId(createdId)

      toast.success('题解已提交')

      if (postId) {
        await navigate({
          params: { postId, questionId },
          to: '/problems/$questionId/solutions/$postId',
        })
      } else {
        await navigate({
          params: { questionId },
          to: '/problems/$questionId/solutions',
        })
      }
    } catch (requestError) {
      const message = getPostRequestErrorMessage(requestError)
      setError(message)
      toast.error(message)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <main className="flex min-h-[calc(100svh-3.5rem)] items-center justify-center bg-muted/20">
        <span className="inline-flex items-center gap-2 text-sm text-muted-foreground">
          <Loader2 className="size-4 animate-spin" />
          正在加载题目信息
        </span>
      </main>
    )
  }

  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-muted/20 p-4 sm:p-6">
      <div className="mx-auto w-full max-w-5xl">
        <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
          <div className="min-w-0">
            <Button asChild className="-ml-2 mb-2" variant="ghost">
              <Link
                params={{ questionId: questionId || '' }}
                to="/problems/$questionId/solutions"
              >
                <ArrowLeft />
                返回题解列表
              </Link>
            </Button>
            <div className="flex items-center gap-2 text-sm font-medium text-primary">
              <BookOpen className="size-4" />
              提交题解
            </div>
            <h1 className="mt-2 truncate text-2xl font-semibold tracking-tight">
              {question?.title ? `为「${question.title}」提交题解` : '提交题解'}
            </h1>
            <p className="mt-1 text-sm text-muted-foreground">
              分享解题思路、复杂度分析和关键代码说明
            </p>
          </div>
          <Button asChild variant="outline">
            <Link
              params={{ questionId: questionId || '' }}
              to="/problems/$questionId"
            >
              查看题目
            </Link>
          </Button>
        </div>

        <form className="rounded-lg border bg-background" onSubmit={handleSubmit}>
          <div className="border-b p-4 sm:p-5">
            <FieldGroup>
              <div className="grid gap-4 md:grid-cols-[1fr_18rem]">
                <Field>
                  <FieldLabel htmlFor="solutionTitle">题解标题</FieldLabel>
                  <Input
                    disabled={invalidQuestionId || submitting}
                    id="solutionTitle"
                    onChange={(event) => updateField('title', event.target.value)}
                    placeholder="例如：双指针排序后一遍扫描"
                    value={form.title}
                  />
                </Field>
                <Field>
                  <FieldLabel htmlFor="solutionTags">标签</FieldLabel>
                  <Input
                    disabled={invalidQuestionId || submitting}
                    id="solutionTags"
                    onChange={(event) => updateField('tags', event.target.value)}
                    placeholder="双指针, 排序"
                    value={form.tags}
                  />
                </Field>
              </div>

              <Field>
                <FieldLabel htmlFor="solutionContent">题解内容</FieldLabel>
                <MarkdownEditor
                  height={520}
                  onChange={(value) => updateField('content', value)}
                  placeholder="请输入题解内容，可以包含思路、复杂度分析和代码片段"
                  textareaProps={{ disabled: invalidQuestionId || submitting, id: 'solutionContent' }}
                  value={form.content}
                />
                <FieldDescription>
                  标签可用逗号、空格或中文逗号分隔
                </FieldDescription>
              </Field>

              <FieldError>{error}</FieldError>
            </FieldGroup>
          </div>

          <div className="flex flex-col-reverse gap-2 p-4 sm:flex-row sm:items-center sm:justify-end sm:p-5">
            <Button asChild disabled={submitting} variant="outline">
              <Link
                params={{ questionId: questionId || '' }}
                to="/problems/$questionId/solutions"
              >
                取消
              </Link>
            </Button>
            <Button disabled={invalidQuestionId || submitting} type="submit">
              {submitting ? <Loader2 className="animate-spin" /> : <Save />}
              提交题解
            </Button>
          </div>
        </form>
      </div>
    </main>
  )
}

function getRoutePostId(value: LongResponseData) {
  if (typeof value === 'string') {
    return value.trim() || undefined
  }

  if (Number.isSafeInteger(value)) {
    return String(value)
  }

  return undefined
}

export default SolutionEditorPage
