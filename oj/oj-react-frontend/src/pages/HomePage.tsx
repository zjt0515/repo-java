import {
  ArrowRight,
  BarChart3,
  BookOpen,
  CheckCircle2,
  Flame,
  ListChecks,
  Play,
} from 'lucide-react'
import { Link } from '@tanstack/react-router'
import HeatMap from '@/components/home/HeatMap'
import { Button } from '@/components/ui/button'
import { platformConfig } from '@/config/platform'

const quickLinks = [
  {
    description: '按标题和标签筛选题目，进入在线编码工作区。',
    icon: BookOpen,
    label: '刷题训练',
    to: '/problems',
  },
  {
    description: '查看最近提交记录，复盘运行结果和状态。',
    icon: ListChecks,
    label: '提交记录',
    to: '/user/submissions',
  },
]

function HomePage() {
  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-background">
      {/* Hero */}
      <section className="border-b bg-[linear-gradient(180deg,var(--background)_0%,var(--muted)_100%)]">
        <div className="mx-auto grid w-full max-w-7xl gap-8 px-4 py-10 sm:px-6 lg:grid-cols-[minmax(0,1.05fr)_minmax(22rem,0.95fr)] lg:items-center lg:py-14">
          <div className="flex flex-col gap-7">
            <div className="inline-flex w-fit items-center gap-2 rounded-full border bg-background px-3 py-1 text-sm font-medium text-muted-foreground">
              <Flame className="size-4 text-orange-500" />
              {platformConfig.home.heroBadge}
            </div>
            <div className="max-w-3xl">
              <h1 className="text-4xl font-semibold tracking-tight text-foreground sm:text-5xl">
                {platformConfig.home.heroTitle[0]}
                <br className="hidden sm:block" />
                {platformConfig.home.heroTitle[1]}
              </h1>
              <p className="mt-5 max-w-2xl text-base leading-7 text-muted-foreground sm:text-lg">
                {platformConfig.home.heroDescription}
              </p>
            </div>
            <div className="flex flex-col gap-3 sm:flex-row">
              <Button asChild size="lg">
                <Link to="/problems">
                  <Play />
                  开始刷题
                </Link>
              </Button>
              <Button asChild size="lg" variant="ghost">
                <Link to="/problems">
                  浏览题库
                  <ArrowRight />
                </Link>
              </Button>
            </div>
          </div>

          <HeatMap />
        </div>
      </section>

      {/* Quick entry */}
      <section className="mx-auto w-full max-w-7xl px-4 py-8 sm:px-6 lg:py-10">
        <div className="mb-5 flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <h2 className="text-2xl font-semibold tracking-tight">快速入口</h2>
            <p className="mt-1 text-sm text-muted-foreground">
              常用训练流程集中在这里，打开就能继续推进。
            </p>
          </div>
          <Button asChild variant="ghost">
            <Link to="/problems">
              全部题目
              <ArrowRight />
            </Link>
          </Button>
        </div>
        <div className="grid gap-3 md:grid-cols-2">
          {quickLinks.map((item) => (
            <Link
              className="group rounded-xl border bg-background p-5 shadow-sm transition-all hover:border-primary/30 hover:shadow-md"
              key={item.to}
              to={item.to}
            >
              <div className="mb-5 flex items-center justify-between gap-3">
                <span className="flex size-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
                  <item.icon className="size-5" />
                </span>
                <ArrowRight className="size-4 text-muted-foreground transition-transform group-hover:translate-x-1 group-hover:text-foreground" />
              </div>
              <h3 className="font-semibold">{item.label}</h3>
              <p className="mt-2 text-sm leading-6 text-muted-foreground">
                {item.description}
              </p>
            </Link>
          ))}
        </div>
      </section>

      {/* Core features */}
      <section className="border-t bg-muted/30">
        <div className="mx-auto w-full max-w-7xl px-4 py-10 sm:px-6 lg:py-14">
          <div className="mb-8 text-center">
            <h2 className="text-2xl font-semibold tracking-tight">围绕训练闭环设计</h2>
            <p className="mt-2 text-sm text-muted-foreground">
              每个环节都为了让你更高效地练习与提升
            </p>
          </div>
          <div className="grid gap-4 sm:grid-cols-3">
            <div className="rounded-xl border bg-background p-6 text-center shadow-sm">
              <div className="mx-auto flex size-12 items-center justify-center rounded-full bg-primary/10">
                <BookOpen className="size-6 text-primary" />
              </div>
              <h3 className="mt-4 font-semibold">题库筛选</h3>
              <p className="mt-2 text-sm leading-6 text-muted-foreground">
                按难度、标签、题目名称快速定位目标题目，专注训练。
              </p>
            </div>
            <div className="rounded-xl border bg-background p-6 text-center shadow-sm">
              <div className="mx-auto flex size-12 items-center justify-center rounded-full bg-primary/10">
                <CheckCircle2 className="size-6 text-primary" />
              </div>
              <h3 className="mt-4 font-semibold">即时判题</h3>
              <p className="mt-2 text-sm leading-6 text-muted-foreground">
                提交代码后自动评测，快速获取结果与运行状态反馈。
              </p>
            </div>
            <div className="rounded-xl border bg-background p-6 text-center shadow-sm">
              <div className="mx-auto flex size-12 items-center justify-center rounded-full bg-primary/10">
                <BarChart3 className="size-6 text-primary" />
              </div>
              <h3 className="mt-4 font-semibold">提交记录</h3>
              <p className="mt-2 text-sm leading-6 text-muted-foreground">
                完整保存历次提交，随时复盘代码与运行表现。
              </p>
            </div>
          </div>
        </div>
      </section>
    </main>
  )
}

export default HomePage
