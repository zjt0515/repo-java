import { useEffect, useState } from 'react'
import { useTheme } from '@/components/user/theme'

type MarkdownColorMode = 'dark' | 'light'

function getSystemColorMode(): MarkdownColorMode {
  if (typeof window === 'undefined') {
    return 'light'
  }

  return window.matchMedia('(prefers-color-scheme: dark)').matches
    ? 'dark'
    : 'light'
}

export function useMarkdownColorMode(): MarkdownColorMode {
  const { theme } = useTheme()
  const [systemColorMode, setSystemColorMode] =
    useState<MarkdownColorMode>(getSystemColorMode)

  useEffect(() => {
    if (theme !== 'system') {
      return
    }

    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    const handleChange = () => setSystemColorMode(getSystemColorMode())

    mediaQuery.addEventListener('change', handleChange)
    return () => {
      mediaQuery.removeEventListener('change', handleChange)
    }
  }, [theme])

  return theme === 'system' ? systemColorMode : theme
}
