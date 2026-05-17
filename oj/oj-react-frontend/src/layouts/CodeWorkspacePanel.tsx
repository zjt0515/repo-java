import {
  Activity,
  Loader2,
  Play,
  Send,
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

      <div className="flex flex-1 flex-col">
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
      </div>
    </section>
  )
}


export default CodeWorkspacePanel
