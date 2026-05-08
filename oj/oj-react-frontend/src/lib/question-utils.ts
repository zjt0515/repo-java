export function parseQuestionTags(value?: string | string[]) {
  if (!value) {
    return []
  }

  if (Array.isArray(value)) {
    return value.map(String).filter(Boolean)
  }

  try {
    const parsed: unknown = JSON.parse(value)

    if (Array.isArray(parsed)) {
      return parsed.map(String).filter(Boolean)
    }
  } catch {
    return value
      .split(',')
      .map((tag) => tag.trim())
      .filter(Boolean)
  }

  return []
}

export function parseQuestionTagInput(value: string) {
  return value
    .split(/[,，\s]+/)
    .map((tag) => tag.trim())
    .filter(Boolean)
}

export function formatQuestionDateTime(value?: string) {
  if (!value) {
    return '-'
  }

  return new Date(value).toLocaleString('zh-CN', {
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

export function formatAcceptRate(acceptedNum?: number, submitNum?: number) {
  if (!submitNum) {
    return '-'
  }

  return `${Math.round(((acceptedNum ?? 0) / submitNum) * 100)}%`
}
