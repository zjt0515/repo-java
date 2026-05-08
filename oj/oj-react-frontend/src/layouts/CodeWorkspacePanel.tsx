import {
  Activity,
  CheckCircle2,
  Clock3,
  Gauge,
  Loader2,
  Play,
  Send,
  type LucideIcon,
} from 'lucide-react'
import CodeEditor from '@/components/code/CodeEditor'
import { Button } from '@/components/ui/button'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Separator } from '@/components/ui/separator'

const languageOptions = [
  { label: 'Java', value: 'java', editorLanguage: 'java' },
  { label: 'C++', value: 'cpp', editorLanguage: 'cpp' },
  { label: 'Python', value: 'python', editorLanguage: 'python' },
  { label: 'JavaScript', value: 'javascript', editorLanguage: 'javascript' },
]

type CodeWorkspacePanelProps = {
  code: string
  language: string
  onCodeChange: (code: string) => void
  onLanguageChange: (language: string) => void
  onSubmitCode: () => void
  submitDisabled?: boolean
  submitResult?: {
    id: string
    language: string
    submittedAt: Date
  }
  submitting?: boolean
}

function CodeWorkspacePanel({
  code,
  language,
  onCodeChange,
  onLanguageChange,
  onSubmitCode,
  submitDisabled = false,
  submitResult,
  submitting = false,
}: CodeWorkspacePanelProps) {
  const activeLanguage =
    languageOptions.find((item) => item.value === language) ?? languageOptions[0]
  const resultStats = submitResult
    ? [
        { label: '提交编号', value: `#${submitResult.id}`, icon: CheckCircle2 },
        {
          label: '提交语言',
          value: getLanguageLabel(submitResult.language),
          icon: Gauge,
        },
        {
          label: '提交时间',
          value: formatSubmitTime(submitResult.submittedAt),
          icon: Clock3,
        },
      ]
    : [
        { label: '提交编号', value: '-', icon: CheckCircle2 },
        { label: '提交语言', value: activeLanguage.label, icon: Gauge },
        { label: '提交时间', value: '-', icon: Clock3 },
      ]
  const judgeInfoText = submitResult
    ? `submit id: ${submitResult.id}
language: ${getLanguageLabel(submitResult.language)}
status: 已提交，等待判题服务处理`
    : '暂无提交结果'

  return (
    <section className="flex min-h-[720px] flex-col bg-muted/20 lg:min-h-0">
      <div className="flex h-12 items-center gap-2 border-b bg-background px-3">
        <Select value={language} onValueChange={onLanguageChange}>
          <SelectTrigger className="bg-muted/40">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            {languageOptions.map((item) => (
              <SelectItem key={item.value} value={item.value}>
                {item.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        <Separator orientation="vertical" className="mx-1 h-6" />
        <Button variant="ghost" size="sm">
          <Activity />
          最近提交
        </Button>
        <div className="ml-auto flex items-center gap-2">
          <Button variant="outline" size="sm">
            <Play />
            运行
          </Button>
          <Button
            disabled={submitting || submitDisabled}
            size="sm"
            onClick={onSubmitCode}
          >
            {submitting ? <Loader2 className="animate-spin" /> : <Send />}
            {submitting ? '提交中' : '提交'}
          </Button>
        </div>
      </div>

      <div className="grid flex-1 grid-rows-[minmax(360px,1fr)_260px]">
        <div className="min-h-0 border-b bg-background dark:bg-[#1e1e1e]">
          <CodeEditor
            value={code}
            onChange={onCodeChange}
            language={activeLanguage.editorLanguage}
            height="100%"
            options={{
              fontSize: 13,
              lineNumbersMinChars: 3,
              minimap: { enabled: true },
            }}
          />
        </div>

        <div className="bg-background">
          <div className="flex h-11 items-center justify-between border-b px-4">
            <div className="flex items-center gap-2 text-sm font-medium">
              {submitResult ? (
                <CheckCircle2 className="size-4 text-emerald-500" />
              ) : (
                <Clock3 className="size-4 text-muted-foreground" />
              )}
              提交结果
            </div>
            <span className="text-xs text-muted-foreground">
              {submitResult ? 'Judging' : 'Pending'}
            </span>
          </div>
          <div className="grid gap-3 p-4 sm:grid-cols-[1fr_1.2fr]">
            <div className="grid grid-cols-3 gap-2 sm:grid-cols-1">
              {resultStats.map((item) => (
                <ResultStat key={item.label} {...item} />
              ))}
            </div>
            <div className="rounded-lg border bg-muted/30 p-3">
              <div className="mb-2 text-sm font-medium">判题信息</div>
              <pre className="overflow-auto rounded-md bg-background p-3 font-mono text-xs leading-5 text-muted-foreground">{judgeInfoText}</pre>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

function getLanguageLabel(value: string) {
  return languageOptions.find((item) => item.value === value)?.label ?? value
}

function formatSubmitTime(value: Date) {
  return value.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

function ResultStat({
  label,
  value,
  icon: Icon,
}: {
  label: string
  value: string
  icon: LucideIcon
}) {
  return (
    <div className="rounded-lg border bg-background p-3">
      <div className="flex items-center gap-2 text-xs text-muted-foreground">
        <Icon className="size-3.5" />
        {label}
      </div>
      <div className="mt-1.5 text-sm font-semibold">{value}</div>
    </div>
  )
}

export default CodeWorkspacePanel
