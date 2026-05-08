import { Link, useNavigate } from '@tanstack/react-router'
import {
  ArrowLeft,
  Loader2,
  Plus,
  Save,
  Trash2,
} from 'lucide-react'
import { useEffect, useMemo, useState, type FormEvent } from 'react'
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
import { Textarea } from '@/components/ui/textarea'
import {
  addQuestion,
  getQuestion,
  getQuestionRequestErrorMessage,
  updateQuestion,
  type Question,
} from '@/services/questionService'

type QuestionAdminEditorPageProps = {
  questionId?: string
}

type JudgeCaseFormItem = {
  input: string
  output: string
}

type QuestionFormState = {
  answer: string
  content: string
  judgeCases: JudgeCaseFormItem[]
  memoryLimit: string
  stackLimit: string
  tags: string
  timeLimit: string
  title: string
}

const emptyForm: QuestionFormState = {
  answer: '',
  content: '',
  judgeCases: [{ input: '', output: '' }],
  memoryLimit: '256',
  stackLimit: '128',
  tags: '',
  timeLimit: '1000',
  title: '',
}

function QuestionAdminEditorPage({ questionId }: QuestionAdminEditorPageProps) {
  const navigate = useNavigate()
  // 区分编辑还是新建题目
  const isEdit = Boolean(questionId)
  
  const [form, setForm] = useState<QuestionFormState>(emptyForm)
  const [loading, setLoading] = useState(isEdit)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')

  const pageTitle = useMemo(() => (isEdit ? '修改题目' : '新增题目'), [isEdit])

  useEffect(() => {
    if (!questionId) {
      return
    }

    const id = questionId
    let ignore = false

    async function fetchQuestion() {
      setLoading(true)
      try {
        const question = await getQuestion(id)

        if (!ignore) {
          setForm(questionToForm(question))
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
  }, [questionId])

  function updateField<K extends keyof QuestionFormState>(
    key: K,
    value: QuestionFormState[K],
  ) {
    setForm((current) => ({ ...current, [key]: value }))
  }

  function updateJudgeCase(
    index: number,
    key: keyof JudgeCaseFormItem,
    value: string,
  ) {
    setForm((current) => ({
      ...current,
      judgeCases: current.judgeCases.map((item, itemIndex) =>
        itemIndex === index ? { ...item, [key]: value } : item,
      ),
    }))
  }

  function addJudgeCase() {
    setForm((current) => ({
      ...current,
      judgeCases: [...current.judgeCases, { input: '', output: '' }],
    }))
  }

  function removeJudgeCase(index: number) {
    setForm((current) => ({
      ...current,
      judgeCases:
        current.judgeCases.length > 1
          ? current.judgeCases.filter((_, itemIndex) => itemIndex !== index)
          : current.judgeCases,
    }))
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    if (!form.title.trim()) {
      setError('请填写题目标题')
      return
    }

    if (!form.content.trim()) {
      setError('请填写题目描述')
      return
    }

    const payload = formToPayload(form)

    setSubmitting(true)
    try {
      if (questionId) {
        await updateQuestion({ ...payload, id: questionId })
        toast.success('题目已更新')
      } else {
        await addQuestion(payload)
        toast.success('题目已创建')
      }

      await navigate({ to: '/admin/questions' })
    } catch (requestError) {
      const message = getQuestionRequestErrorMessage(requestError)
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
          正在加载题目
        </span>
      </main>
    )
  }

  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-muted/20 p-4 sm:p-6">
      <div className="mx-auto w-full max-w-5xl">
        <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <Button asChild className="-ml-2 mb-2" variant="ghost">
              <Link to="/admin/questions">
                <ArrowLeft />
                返回题目管理
              </Link>
            </Button>
            <h1 className="text-2xl font-semibold tracking-tight">{pageTitle}</h1>
            <p className="mt-1 text-sm text-muted-foreground">
              配置题目正文、参考答案、测试用例和判题资源限制
            </p>
          </div>
        </div>

        <form className="rounded-lg border bg-background" onSubmit={handleSubmit}>
          <div className="border-b p-4 sm:p-5">
            <FieldGroup>
              <div className="grid gap-4 md:grid-cols-[1fr_16rem]">
                <Field>
                  <FieldLabel htmlFor="questionTitle">题目标题</FieldLabel>
                  <Input
                    id="questionTitle"
                    onChange={(event) => updateField('title', event.target.value)}
                    placeholder="例如：两数之和"
                    value={form.title}
                  />
                </Field>
                <Field>
                  <FieldLabel htmlFor="questionTags">标签</FieldLabel>
                  <Input
                    id="questionTags"
                    onChange={(event) => updateField('tags', event.target.value)}
                    placeholder="数组, 哈希表"
                    value={form.tags}
                  />
                </Field>
              </div>

              <Field>
                <FieldLabel htmlFor="questionContent">题目描述</FieldLabel>
                <MarkdownEditor
                  height={420}
                  onChange={(value) => updateField('content', value)}
                  placeholder="请输入题目描述、输入输出说明和示例"
                  textareaProps={{ id: 'questionContent' }}
                  value={form.content}
                />
              </Field>

              <Field>
                <FieldLabel htmlFor="questionAnswer">参考答案</FieldLabel>
                <Textarea
                  className="min-h-40 font-mono text-sm"
                  id="questionAnswer"
                  onChange={(event) => updateField('answer', event.target.value)}
                  placeholder="请输入参考答案或题解"
                  value={form.answer}
                />
              </Field>
            </FieldGroup>
          </div>

          <div className="grid gap-0 lg:grid-cols-[1fr_20rem]">
            <section className="border-b p-4 sm:p-5 lg:border-b-0 lg:border-r">
              <div className="mb-4 flex items-center justify-between gap-3">
                <div>
                  <h2 className="font-medium">测试用例</h2>
                  <p className="mt-1 text-sm text-muted-foreground">
                    每组用例会提交给后端作为 judgeCase 数组
                  </p>
                </div>
                <Button onClick={addJudgeCase} type="button" variant="outline">
                  <Plus />
                  添加
                </Button>
              </div>

              <div className="space-y-3">
                {form.judgeCases.map((judgeCase, index) => (
                  <div className="rounded-lg border bg-muted/20 p-3" key={index}>
                    <div className="mb-3 flex items-center justify-between">
                      <span className="text-sm font-medium">用例 {index + 1}</span>
                      <Button
                        disabled={form.judgeCases.length <= 1}
                        onClick={() => removeJudgeCase(index)}
                        size="icon-sm"
                        type="button"
                        variant="ghost"
                      >
                        <Trash2 />
                      </Button>
                    </div>
                    <div className="grid gap-3 md:grid-cols-2">
                      <Field>
                        <FieldLabel>输入</FieldLabel>
                        <Textarea
                          className="min-h-24 font-mono text-sm"
                          onChange={(event) =>
                            updateJudgeCase(index, 'input', event.target.value)
                          }
                          placeholder="1 2"
                          value={judgeCase.input}
                        />
                      </Field>
                      <Field>
                        <FieldLabel>输出</FieldLabel>
                        <Textarea
                          className="min-h-24 font-mono text-sm"
                          onChange={(event) =>
                            updateJudgeCase(index, 'output', event.target.value)
                          }
                          placeholder="3"
                          value={judgeCase.output}
                        />
                      </Field>
                    </div>
                  </div>
                ))}
              </div>
            </section>

            <aside className="p-4 sm:p-5">
              <FieldGroup>
                <div>
                  <h2 className="font-medium">判题配置</h2>
                  <p className="mt-1 text-sm text-muted-foreground">
                    留空时不会向后端提交对应限制
                  </p>
                </div>

                <Field>
                  <FieldLabel htmlFor="timeLimit">时间限制 ms</FieldLabel>
                  <Input
                    id="timeLimit"
                    min={0}
                    onChange={(event) => updateField('timeLimit', event.target.value)}
                    type="number"
                    value={form.timeLimit}
                  />
                </Field>

                <Field>
                  <FieldLabel htmlFor="memoryLimit">内存限制 MB</FieldLabel>
                  <Input
                    id="memoryLimit"
                    min={0}
                    onChange={(event) =>
                      updateField('memoryLimit', event.target.value)
                    }
                    type="number"
                    value={form.memoryLimit}
                  />
                </Field>

                <Field>
                  <FieldLabel htmlFor="stackLimit">栈限制 MB</FieldLabel>
                  <Input
                    id="stackLimit"
                    min={0}
                    onChange={(event) => updateField('stackLimit', event.target.value)}
                    type="number"
                    value={form.stackLimit}
                  />
                </Field>

                <FieldError>{error}</FieldError>

                <Button disabled={submitting} type="submit">
                  {submitting ? <Loader2 className="animate-spin" /> : <Save />}
                  保存题目
                </Button>

                <FieldDescription>
                  标签请用英文逗号分隔，例如：数组,动态规划,图论
                </FieldDescription>
              </FieldGroup>
            </aside>
          </div>
        </form>
      </div>
    </main>
  )
}

function questionToForm(question: Question): QuestionFormState {
  const judgeConfig = parseJsonObject<{
    memoryLimit?: number
    stackLimit?: number
    timeLimit?: number
  }>(question.judgeConfig as unknown)

  return {
    answer: question.answer ?? '',
    content: question.content ?? '',
    judgeCases: parseJudgeCases(question.judgeCase as unknown),
    memoryLimit: numberToString(judgeConfig?.memoryLimit),
    stackLimit: numberToString(judgeConfig?.stackLimit),
    tags: parseTags(question.tags as unknown).join(', '),
    timeLimit: numberToString(judgeConfig?.timeLimit),
    title: question.title ?? '',
  }
}

function formToPayload(form: QuestionFormState) {
  return {
    answer: form.answer.trim() || undefined,
    content: form.content.trim(),
    judgeCase: form.judgeCases
      .map((judgeCase) => ({
        input: judgeCase.input,
        output: judgeCase.output,
      }))
      .filter((judgeCase) => judgeCase.input.trim() || judgeCase.output.trim()),
    judgeConfig: {
      memoryLimit: toOptionalNumber(form.memoryLimit),
      stackLimit: toOptionalNumber(form.stackLimit),
      timeLimit: toOptionalNumber(form.timeLimit),
    },
    tags: parseTags(form.tags),
    title: form.title.trim(),
  }
}

function parseJudgeCases(value?: unknown): JudgeCaseFormItem[] {
  const parsed = parseJsonObject<Array<JudgeCaseFormItem>>(value)

  if (!Array.isArray(parsed) || parsed.length === 0) {
    return [{ input: '', output: '' }]
  }

  return parsed.map((item) => ({
    input: item.input ?? '',
    output: item.output ?? '',
  }))
}

function parseTags(value?: unknown) {
  if (!value) {
    return []
  }

  if (Array.isArray(value)) {
    return value.map(String).map((tag) => tag.trim()).filter(Boolean)
  }

  const parsed = parseJsonObject<unknown>(value)

  if (Array.isArray(parsed)) {
    return parsed.map(String).map((tag) => tag.trim()).filter(Boolean)
  }

  if (typeof value !== 'string') {
    return []
  }

  return value
    .split(',')
    .map((tag) => tag.trim())
    .filter(Boolean)
}

function parseJsonObject<T>(value?: unknown): T | undefined {
  if (!value) {
    return undefined
  }

  if (typeof value !== 'string') {
    return value as T
  }

  try {
    return JSON.parse(value) as T
  } catch {
    return undefined
  }
}

function numberToString(value?: number) {
  return typeof value === 'number' ? String(value) : ''
}

function toOptionalNumber(value: string) {
  if (!value.trim()) {
    return undefined
  }

  const numberValue = Number(value)

  return Number.isFinite(numberValue) ? numberValue : undefined
}

export default QuestionAdminEditorPage
