import type { LucideIcon } from 'lucide-react'
import { Link } from '@tanstack/react-router'
import { Button } from '@/components/ui/button'

type PlaceholderPageProps = {
  title: string
  description: string
  icon: LucideIcon
}

function PlaceholderPage({
  title,
  description,
  icon: Icon,
}: PlaceholderPageProps) {
  return (
    <main className="flex min-h-[calc(100svh-3.5rem)] items-center justify-center p-6">
      <section className="w-full max-w-xl rounded-lg border bg-background p-6 text-center">
        <div className="mx-auto flex size-10 items-center justify-center rounded-lg bg-muted">
          <Icon className="size-5 text-muted-foreground" />
        </div>
        <h1 className="mt-4 text-2xl font-semibold tracking-tight">{title}</h1>
        <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-muted-foreground">
          {description}
        </p>
        <Button className="mt-5" variant="outline" asChild>
          <Link to="/">返回首页</Link>
        </Button>
      </section>
    </main>
  )
}

export default PlaceholderPage
