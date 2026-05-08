import CodeWorkspacePanel from './CodeWorkspacePanel'
import ProblemDetailPanel from './ProblemDetailPanel'
import TrainingSidebar from './TrainingSidebar'
import type { Question } from '@/services/questionService'

type OjWorkspaceLayoutProps = {
  code: string
  error?: string
  language: string
  loading?: boolean
  onCodeChange: (code: string) => void
  onLanguageChange: (language: string) => void
  onSubmitCode: () => void
  question?: Question
  questionId?: string
  submitDisabled?: boolean
  submitResult?: {
    id: string
    language: string
    submittedAt: Date
  }
  submitting?: boolean
}

function OjWorkspaceLayout({
  code,
  error,
  language,
  loading,
  onCodeChange,
  onLanguageChange,
  onSubmitCode,
  question,
  questionId,
  submitDisabled,
  submitResult,
  submitting,
}: OjWorkspaceLayoutProps) {
  return (
    <main className="grid min-h-[calc(100svh-3.5rem)] grid-cols-1 lg:grid-cols-[280px_minmax(360px,1fr)_minmax(460px,1.12fr)]">
      <TrainingSidebar />
      <ProblemDetailPanel
        error={error}
        loading={loading}
        question={question}
        questionId={questionId}
      />
      <CodeWorkspacePanel
        code={code}
        language={language}
        onCodeChange={onCodeChange}
        onLanguageChange={onLanguageChange}
        onSubmitCode={onSubmitCode}
        submitDisabled={submitDisabled}
        submitResult={submitResult}
        submitting={submitting}
      />
    </main>
  )
}

export default OjWorkspaceLayout
