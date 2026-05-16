import MonacoEditor, {
  type BeforeMount,
  type EditorProps as MonacoEditorProps,
  type Monaco,
  type OnMount,
  type OnValidate,
} from '@monaco-editor/react'
import {debounce} from 'es-toolkit/function'
import {
  forwardRef,
  useImperativeHandle,
  useMemo,
  useRef,
  type ReactNode,
} from 'react'
import { useTheme } from '@/components/user/theme'

export type CodeEditorTheme = 'vs-dark' | 'light'
type MonacoCodeEditor = Parameters<OnMount>[0]
type MonacoEditorOptions = NonNullable<MonacoEditorProps['options']>

export type CodeEditorHandle = {
  focus: () => void
  getValue: () => string
  setValue: (value: string) => void
  getEditor: () => MonacoCodeEditor | null
}

export type CodeEditorProps = {
  value?: string
  defaultValue?: string
  language?: string
  path?: string
  theme?: CodeEditorTheme | string
  height?: number | string
  width?: number | string
  readOnly?: boolean
  className?: string
  loading?: ReactNode
  options?: MonacoEditorOptions
  beforeMount?: BeforeMount
  onMount?: OnMount
  onChange?: (value: string) => void
  onValidate?: OnValidate
}

const defaultOptions: MonacoEditorOptions = {
  automaticLayout: true,
  fontFamily:
    'JetBrains Mono, Fira Code, Menlo, Monaco, Consolas, "Liberation Mono", monospace',
  fontSize: 14,
  lineHeight: 22,
  minimap: {
    enabled: false,
  },
  padding: {
    top: 12,
    bottom: 12,
  },
  scrollBeyondLastLine: false,
  tabSize: 2,
  wordWrap: 'on',
}

const defaultLoading = (
  <div className="flex h-full min-h-64 items-center justify-center text-sm text-muted-foreground">
    Loading editor...
  </div>
)

const CodeEditor = forwardRef<CodeEditorHandle, CodeEditorProps>(
  (
    {
      value,
      defaultValue = '',
      language = 'javascript',
      path,
      theme,
      height = 420,
      width = '100%',
      readOnly = false,
      className,
      loading = defaultLoading,
      options,
      beforeMount,
      onMount,
      onChange,
      onValidate,
    },
    ref,
  ) => {
    const editorRef = useRef<MonacoCodeEditor | null>(null)
    const { resolvedTheme } = useTheme()
    const editorTheme = theme ?? (resolvedTheme === 'dark' ? 'vs-dark' : 'light')

    const mergedOptions = useMemo<MonacoEditorOptions>(
      () => ({
        ...defaultOptions,
        ...options,
        readOnly,
      }),
      [options, readOnly],
    )

    useImperativeHandle(
      ref,
      () => ({
        focus: () => {
          editorRef.current?.focus()
        },
        getValue: () => editorRef.current?.getValue() ?? '',
        setValue: (nextValue) => {
          editorRef.current?.setValue(nextValue)
        },
        getEditor: () => editorRef.current,
      }),
      [],
    )

    const handleMount: OnMount = (editorInstance, monaco) => {
      editorRef.current = editorInstance
      onMount?.(editorInstance, monaco)
    }

    const debouncedHandleChange: MonacoEditorProps['onChange'] = debounce(
      (nextValue) => {
        onChange?.(nextValue)
      }, 500
    )

    return (
      <MonacoEditor
        value={value}
        defaultValue={defaultValue}
        language={language}
        path={path}
        theme={editorTheme}
        height={height}
        width={width}
        className={className}
        loading={loading}
        options={mergedOptions}
        beforeMount={beforeMount}
        onMount={handleMount}
        onChange={debouncedHandleChange}
        onValidate={onValidate}
      />
    )
  },
)

CodeEditor.displayName = 'CodeEditor'

export type { Monaco }
export default CodeEditor
