import MDEditor, { type MDEditorProps } from '@uiw/react-md-editor/nohighlight'
import * as markdownCommands from '@uiw/react-md-editor/commands-cn'
import { useMemo } from 'react'
import { cn } from '@/lib/utils'
import { useMarkdownColorMode } from './useMarkdownColorMode'
import './markdown.css'

type MarkdownEditorProps = Omit<
  MDEditorProps,
  'commands' | 'extraCommands' | 'onChange' | 'value'
> & {
  onChange: (value: string) => void
  placeholder?: string
  value: string
}

const editorCommands = markdownCommands.getCommands()
const editorExtraCommands = markdownCommands.getExtraCommands()

export function MarkdownEditor({
  className,
  height = 360,
  onChange,
  placeholder = '请输入 Markdown 内容',
  preview = 'live',
  previewOptions,
  textareaProps,
  value,
  visibleDragbar = false,
  ...props
}: MarkdownEditorProps) {
  const colorMode = useMarkdownColorMode()

  const mergedPreviewOptions = useMemo(
    () => ({
      ...previewOptions,
      className: cn('oj-markdown-body', previewOptions?.className),
      wrapperElement: {
        ...previewOptions?.wrapperElement,
        'data-color-mode': colorMode,
      },
    }),
    [colorMode, previewOptions],
  )

  return (
    <div
      className={cn('oj-markdown-surface oj-markdown-editor', className)}
      data-color-mode={colorMode}
    >
      <MDEditor
        commands={editorCommands}
        extraCommands={editorExtraCommands}
        height={height}
        onChange={(nextValue) => onChange(nextValue ?? '')}
        preview={preview}
        previewOptions={mergedPreviewOptions}
        textareaProps={{
          placeholder,
          ...textareaProps,
        }}
        value={value}
        visibleDragbar={visibleDragbar}
        {...props}
      />
    </div>
  )
}
