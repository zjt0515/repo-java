import { Link, type LinkProps } from '@tanstack/react-router'
import type { LucideIcon } from 'lucide-react'
import { ArrowLeft, Home, ListChecks } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

type StatusAction = {
  icon?: LucideIcon
  label: string
  to: LinkProps['to']
  variant?: 'default' | 'outline' | 'secondary' | 'ghost'
}

type StatusPageProps = {
  actions?: StatusAction[]
  code: string
  description: string
  icon: LucideIcon
  tone?: 'danger' | 'muted' | 'warning'
  title: string
}

const toneStyles = {
  danger: 'border-destructive/30 bg-destructive/10 text-destructive',
  muted: 'border-muted-foreground/20 bg-muted text-muted-foreground',
  warning: 'border-amber-500/30 bg-amber-500/10 text-amber-700 dark:text-amber-300',
}

const defaultActions: StatusAction[] = [
  { icon: ListChecks, label: '返回题库', to: '/problems' },
  { icon: Home, label: '回到首页', to: '/', variant: 'outline' },
]

function StatusPage({
  actions = defaultActions,
  code,
  description,
  icon: Icon,
  tone = 'muted',
  title,
}: StatusPageProps) {
  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-muted/20 px-4 py-10 sm:px-6">
      <section className="mx-auto flex min-h-[520px] w-full max-w-5xl items-center">
        <div className="grid w-full gap-8 lg:grid-cols-[minmax(0,1fr)_18rem] lg:items-center">
          <div>
            <div
              className={cn(
                'inline-flex items-center gap-2 rounded-md border px-3 py-1 text-sm font-medium',
                toneStyles[tone],
              )}
            >
              <Icon className="size-4" />
              HTTP {code}
            </div>
            <h1 className="mt-5 max-w-2xl text-3xl font-semibold tracking-tight sm:text-4xl">
              {title}
            </h1>
            <p className="mt-3 max-w-2xl text-sm leading-6 text-muted-foreground sm:text-base">
              {description}
            </p>
            <div className="mt-7 flex flex-wrap items-center gap-3">
              {actions.map((action) => {
                const ActionIcon = action.icon ?? ArrowLeft

                return (
                  <Button
                    asChild
                    key={`${action.to}-${action.label}`}
                    variant={action.variant}
                  >
                    <Link to={action.to}>
                      <ActionIcon className="size-4" />
                      {action.label}
                    </Link>
                  </Button>
                )
              })}
            </div>
          </div>

          <div
            aria-hidden="true"
            className="hidden aspect-square items-center justify-center rounded-lg border bg-background shadow-sm lg:flex"
          >
            <div className="text-center">
              <div className="mx-auto flex size-16 items-center justify-center rounded-lg bg-muted">
                <Icon className="size-8 text-muted-foreground" />
              </div>
              <div className="mt-5 font-mono text-6xl font-semibold leading-none">
                {code}
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  )
}

export default StatusPage
