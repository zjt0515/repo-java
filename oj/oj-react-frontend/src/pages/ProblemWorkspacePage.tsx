import { useNavigate } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { toast } from 'sonner'
import OjWorkspaceLayout from '@/layouts/OjWorkspaceLayout'
import { useWorkspaceStore } from '@/stores/workspace'
import {
  getQuestionRequestErrorMessage,
  getQuestionVO,
  submitQuestion,
  submitQuestionTest,
  type Question,
  type QuestionVO,
} from '@/services/questionService'
import type { TestResult } from '@/layouts/CodeWorkspacePanel'

const starterCode = ``

type ProblemWorkspacePageProps = {
  questionId?: string
}

export type QuestionSubmitResult = {
  id: string
  language: string
  submittedAt: Date
}

function ProblemWorkspacePage({ questionId }: ProblemWorkspacePageProps) {
  const navigate = useNavigate()
  const invalidQuestionId = questionId !== undefined && !/^[1-9]\d*$/.test(questionId)
  const [code, setCode] = useState(starterCode)
  const language = useWorkspaceStore((state) => state.language)
  const setLanguage = useWorkspaceStore((state) => state.setLanguage)
  const [question, setQuestion] = useState<Question>()
  const [loading, setLoading] = useState(Boolean(questionId) && !invalidQuestionId)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [submitResult, setSubmitResult] = useState<QuestionSubmitResult>()
  const [testing, setTesting] = useState(false)
  const [testInput, setTestInput] = useState('')
  const [testResult, setTestResult] = useState<TestResult>()
  const displayError = invalidQuestionId ? '题目编号无效' : error

  useEffect(() => {
    if (!questionId || invalidQuestionId) {
      return
    }

    const id = questionId
    let ignore = false

    async function fetchQuestion() {
      setLoading(true)
      setError('')
      try {
        const nextQuestion = await getQuestionVO(id)

        if (!ignore) {
          setQuestion(mapQuestionVOToQuestion(nextQuestion))
        }
      } catch (requestError) {
        if (!ignore) {
          setQuestion(undefined)
          setError(getQuestionRequestErrorMessage(requestError))
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

  async function handleSubmitCode() {
    if (!questionId || invalidQuestionId) {
      toast.error('题目编号无效，无法提交')
      return
    }

    const trimmedCode = code.trim()

    if (!trimmedCode) {
      toast.error('请先编写代码再提交')
      return
    }

    setSubmitting(true)
    try {
      const submitId = String(await submitQuestion({
        code,
        language,
        questionId,
      }))

      setSubmitResult({
        id: submitId,
        language,
        submittedAt: new Date(),
      })
      toast.success(`提交成功，提交编号 #${submitId}`)
      void navigate({ to: '/submissions/$submissionId', params: { submissionId: submitId } })
    } catch (requestError) {
      toast.error(getQuestionRequestErrorMessage(requestError))
    } finally {
      setSubmitting(false)
    }
  }

  function toNum(value: unknown): number | undefined {
    if (typeof value === 'number') return value
    if (typeof value === 'string') {
      const n = Number(value)
      if (!Number.isNaN(n)) return n
    }
    return undefined
  }

  async function handleRunCode() {
    const trimmedCode = code.trim()
    if (!trimmedCode) {
      toast.error('请先编写代码再运行')
      return
    }

    setTesting(true)
    setTestResult(undefined)
    try {
      const result = await submitQuestionTest({
        code,
        language,
        judgeInputCase: [testInput],
      })

      if (typeof result === 'string') {
        setTestResult({ status: 1, outputList: [result], judgeInfo: {} })
        return
      }

      if (!result || typeof result !== 'object') {
        setTestResult({ status: 1, outputList: [String(result)], judgeInfo: {} })
        return
      }

      const r = result as Record<string, unknown>
      const ji = (r.judgeInfo as Record<string, unknown> | undefined) ?? {}

      setTestResult({
        status: toNum(r.status),
        outputList: Array.isArray(r.outputList) ? (r.outputList as string[]) : undefined,
        judgeInfo: {
          memory: toNum(ji.memory),
          time: toNum(ji.time),
          message: typeof ji.message === 'string' ? ji.message : undefined,
        },
      })
    } catch (requestError) {
      const message = getQuestionRequestErrorMessage(requestError)
      setTestResult({ judgeInfo: { message: `运行失败：${message}` } })
      toast.error(message)
    } finally {
      setTesting(false)
    }
  }

  return (
    <OjWorkspaceLayout
      code={code}
      error={displayError}
      language={language}
      loading={loading}
      onCodeChange={setCode}
      onLanguageChange={setLanguage}
      onRunCode={handleRunCode}
      onSubmitCode={handleSubmitCode}
      question={question}
      questionId={questionId}
      submitDisabled={loading || Boolean(displayError)}
      submitResult={submitResult}
      submitting={submitting}
      testInput={testInput}
      onTestInputChange={setTestInput}
      testResult={testResult}
      testing={testing}
    />
  )
}

function mapQuestionVOToQuestion(question: QuestionVO): Question {
  return {
    ...question,
    judgeCase: undefined,
    judgeConfig: question.judgeConfig ? JSON.stringify(question.judgeConfig) : undefined,
    tags: question.tags ? JSON.stringify(question.tags) : undefined,
  }
}

export default ProblemWorkspacePage
