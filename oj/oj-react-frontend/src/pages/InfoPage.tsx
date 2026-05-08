import { Link } from '@tanstack/react-router'
import type { LucideIcon } from 'lucide-react'
import { Button } from '@/components/ui/button'

type InfoSection = {
  content: string
  title: string
}

type InfoPageProps = {
  description: string
  icon: LucideIcon
  sections: InfoSection[]
  title: string
}

function InfoPage({ description, icon: Icon, sections, title }: InfoPageProps) {
  return (
    <main className="bg-muted/20 px-4 py-8 sm:px-6">
      <section className="mx-auto max-w-4xl rounded-lg border bg-background p-6 shadow-sm sm:p-8">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <div className="flex size-10 items-center justify-center rounded-lg bg-muted text-muted-foreground">
              <Icon className="size-5" />
            </div>
            <h1 className="mt-4 text-2xl font-semibold tracking-tight">
              {title}
            </h1>
            <p className="mt-2 max-w-2xl text-sm leading-6 text-muted-foreground">
              {description}
            </p>
          </div>

          <Button asChild variant="outline">
            <Link to="/problems">返回题库</Link>
          </Button>
        </div>

        <div className="mt-8 space-y-5">
          {sections.map((section) => (
            <article className="rounded-lg border bg-muted/20 p-4" key={section.title}>
              <h2 className="text-base font-medium">{section.title}</h2>
              <p className="mt-2 text-sm leading-6 text-muted-foreground">
                {section.content}
              </p>
            </article>
          ))}
        </div>
      </section>
    </main>
  )
}

export default InfoPage
