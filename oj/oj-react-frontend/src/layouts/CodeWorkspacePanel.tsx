import {
  Activity,
  CheckCircle2,
  Loader2,
  Play,
  Send,
} from 'lucide-react'
import CodeEditor from '@/components/code/CodeEditor'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Separator } from '@/components/ui/separator'
import { Textarea } from '@/components/ui/textarea'

const languageOptions = [
  { label: 'Java', value: 'java', editorLanguage: 'java' },
  { label: 'C++', value: 'cpp', editorLanguage: 'cpp' },
  { label: 'Python', value: 'python', editorLanguage: 'python' },
  { label: 'JavaScript', value: 'javascript', editorLanguage: 'javascript' },
]

export type TestResult = {
  status?: number
  outputList?: string[]
  judgeInfo: {
    memory?: number
    time?: number
    message?: string
  }
}

type CodeWorkspacePanelProps = {
  code: string
  language: string
  onCodeChange: (code: string) => void
  onLanguageChange: (language: string) => void
  onSubmitCode: () => void
  onRunCode?: () => void
  testInput?: string
  onTestInputChange?: (value: string) => void
  testResult?: TestResult
  testing?: boolean
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
  onRunCode,
  testInput = '',
  onTestInputChange,
  testResult,
  testing = false,
  submitDisabled = false,
  submitting = false,
}: CodeWorkspacePanelProps) {
  const activeLanguage =
    languageOptions.find((item) => item.value === language) ?? languageOptions[0]

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

      {/* 代码编辑器 */}
      <div className="flex flex-1 flex-col overflow-hidden">
        <div className="min-h-[360px] flex-1 bg-background dark:bg-[#1e1e1e]">
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

        {/* 样例输入输出 */}

        <div className="flex h-56 flex-col border-t bg-background">
          <div className="flex h-8 items-center border-b px-3 text-xs font-medium text-muted-foreground">
            <div className="flex flex-1 items-center gap-2">
              <span>输入</span>
              <Button
                variant="ghost"
                size="sm"
                className="h-5 gap-1 px-1.5 text-[10px]"
                disabled={testing}
                onClick={onRunCode}
              >
                {testing ? (
                  <Loader2 className="animate-spin size-3" />
                ) : (
                  <Play className="size-3" />
                )}
                {testing ? '运行中' : '运行'}
              </Button>
            </div>
            {/* 输出 */}
            <div className="flex flex-1 items-center gap-2">
              <span>输出</span>
              {testResult?.status === 1 && (
                <>
                  <Badge
                    variant="default"
                    className="h-4 gap-0.5 rounded bg-green-600 px-1.5 text-[10px] font-medium text-white hover:bg-green-600"
                  >
                    运行成功
                  </Badge>
                  {typeof testResult.judgeInfo?.time === 'number' && (
                    <span className="text-[10px] text-muted-foreground">
                      {testResult.judgeInfo.time}ms
                    </span>
                  )}
                  {typeof testResult.judgeInfo.memory === 'number' && (
                    <span className="text-[10px] text-muted-foreground">
                      {testResult.judgeInfo.memory}MB
                    </span>
                  )}
                </>
              )}
            </div>
          </div>
          <div className="flex flex-1 overflow-hidden">
            <div className="flex-1 overflow-auto border-r p-2">
              <Textarea
                value={testInput}
                onChange={(e) => onTestInputChange?.(e.target.value)}
                placeholder="请输入自定义测试输入..."
                className="h-full min-h-0 resize-none border-0 bg-transparent font-mono text-sm shadow-none focus-visible:ring-0"
              />
            </div>
            {/* 结果 */}
            <div className="flex-1 overflow-auto p-2">
              {testResult ? (
                <pre className="h-full whitespace-pre-wrap break-all font-mono text-sm">
                  {testResult.status === 1
                    ? testResult.outputList?.join('\n') ?? ''
                    : testResult.judgeInfo.message ?? '运行错误'}
                </pre>
              ) : (
                <div className="flex h-full items-center justify-center text-sm text-muted-foreground">
                  点击「运行」按钮查看输出结果
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}


export default CodeWorkspacePanel
