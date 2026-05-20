import { useState } from 'react'
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
  onRunCode?: () => void
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
  testInput?: string
  onTestInputChange?: (value: string) => void
  testResult?: import('./CodeWorkspacePanel').TestResult
  testing?: boolean
}

function OjWorkspaceLayout({
  code,
  error,
  language,
  loading,
  onCodeChange,
  onLanguageChange,
  onRunCode,
  onSubmitCode,
  question,
  questionId,
  submitDisabled,
  submitResult,
  submitting,
  testInput,
  onTestInputChange,
  testResult,
  testing,
}: OjWorkspaceLayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(false)

  return (
    <main
      className={`grid min-h-[calc(100svh-3.5rem)] ${
        sidebarOpen
          ? 'grid-cols-1 lg:grid-cols-[280px_minmax(360px,1fr)_minmax(460px,1.12fr)]'
          : 'grid-cols-1 lg:grid-cols-[minmax(360px,1fr)_minmax(460px,1.12fr)]'
      }`}
    >
      {sidebarOpen && (
        <TrainingSidebar
          currentQuestionId={questionId}
          onToggle={() => setSidebarOpen(false)}
        />
      )}
      <ProblemDetailPanel
        error={error}
        loading={loading}
        onToggleSidebar={() => setSidebarOpen(true)}
        question={question}
        questionId={questionId}
        showSidebarToggle={!sidebarOpen}
      />
      <CodeWorkspacePanel
        code={code}
        language={language}
        onCodeChange={onCodeChange}
        onLanguageChange={onLanguageChange}
        onRunCode={onRunCode}
        onSubmitCode={onSubmitCode}
        submitDisabled={submitDisabled}
        submitResult={submitResult}
        submitting={submitting}
        testInput={testInput}
        onTestInputChange={onTestInputChange}
        testResult={testResult}
        testing={testing}
      />
    </main>
  )
}

export default OjWorkspaceLayout
