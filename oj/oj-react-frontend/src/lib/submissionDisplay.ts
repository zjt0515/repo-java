export const languageOptions = [
  { label: '全部语言', value: 'all' },
  { label: 'Java', value: 'java' },
  { label: 'C++', value: 'cpp' },
  { label: 'Python', value: 'python' },
  { label: 'JavaScript', value: 'javascript' },
]

export function getStatusMeta(status?: number) {
  switch (status) {
    case 0:
      return {
        className:
          'border-amber-500/30 bg-amber-500/10 text-amber-700 dark:text-amber-300',
        label: '待判题',
        variant: 'outline' as const,
      }
    case 1:
      return {
        className:
          'border-sky-500/30 bg-sky-500/10 text-sky-700 dark:text-sky-300',
        label: '判题中',
        variant: 'outline' as const,
      }
    case 2:
      return {
        className:
          'border-emerald-500/30 bg-emerald-500/10 text-emerald-700 dark:text-emerald-300',
        label: '成功',
        variant: 'outline' as const,
      }
    case 3:
      return {
        className: undefined,
        label: '失败',
        variant: 'destructive' as const,
      }
    default:
      return {
        className: undefined,
        label: '未知',
        variant: 'secondary' as const,
      }
  }
}

export function formatLanguage(value?: string) {
  if (!value) {
    return '-'
  }

  return languageOptions.find((item) => item.value === value)?.label ?? value
}

export function formatJudgeTime(value?: number, message?: string) {
  if (message !== 'Accepted') {
    return 'N/A'
  }
  return value === undefined || value === null ? 'N/A' : `${value} ms`
}

export function formatJudgeMemory(value?: number, message?: string) {
  if (message !== 'Accepted') {
    return 'N/A'
  }
  return value === undefined || value === null ? 'N/A' : `${value} MB`
}

export function getResultMeta(message?: string) {
  if (message === 'Accepted') {
    return {
      className:
        'border-emerald-500/30 bg-emerald-500/10 text-emerald-700 dark:text-emerald-300',
      label: message,
      variant: 'outline' as const,
    }
  }

  if (message) {
    return {
      className: undefined,
      label: message,
      variant: 'destructive' as const,
    }
  }

  return {
    className: undefined,
    label: '-',
    variant: 'secondary' as const,
  }
}
