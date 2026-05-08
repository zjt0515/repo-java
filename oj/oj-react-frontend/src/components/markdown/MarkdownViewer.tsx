import MDEditor from '@uiw/react-md-editor/nohighlight'
import { cn } from '@/lib/utils'
import { useMarkdownColorMode } from './useMarkdownColorMode'
import './markdown.css'

type MarkdownViewerProps = {
  className?: string
  emptyText?: string
  value?: string
}

export function MarkdownViewer({
  className,
  emptyText = '暂无题目描述',
  value,
}: MarkdownViewerProps) {
  const colorMode = useMarkdownColorMode()
  const source = value?.trim()

  if (!source) {
    return (
      <p className={cn('text-sm leading-6 text-muted-foreground', className)}>
        {emptyText}
      </p>
    )
  }

  return (
    <div
      className={cn('oj-markdown-surface oj-markdown-viewer', className)}
      data-color-mode={colorMode}
    >
      <MDEditor.Markdown
        className="oj-markdown-body"
        source={source}
        wrapperElement={{ 'data-color-mode': colorMode }}
      />
    </div>
  )
}
