import { useNavigate } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { toast } from 'sonner'
import OjWorkspaceLayout from '@/layouts/OjWorkspaceLayout'
import { useWorkspaceStore } from '@/stores/workspace'
import {
  getQuestionRequestErrorMessage,
  getQuestionVO,
  submitQuestion,
  type Question,
  type QuestionVO,
} from '@/services/questionService'

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

  return (
    <OjWorkspaceLayout
      code={code}
      error={displayError}
      language={language}
      loading={loading}
      onCodeChange={setCode}
      onLanguageChange={setLanguage}
      onSubmitCode={handleSubmitCode}
      question={question}
      questionId={questionId}
      submitDisabled={loading || Boolean(displayError)}
      submitResult={submitResult}
      submitting={submitting}
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
