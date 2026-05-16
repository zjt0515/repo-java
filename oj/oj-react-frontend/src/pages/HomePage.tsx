import {
  ArrowRight,
  BarChart3,
  BookOpen,
  CheckCircle2,
  Code2,
  Flame,
  ListChecks,
  Play,
  Trophy,
} from 'lucide-react'
import { Link } from '@tanstack/react-router'
import { Button } from '@/components/ui/button'

const stats = [
  { label: '题库训练', value: 'Practice', icon: BookOpen },
  { label: '实时提交', value: 'Judge', icon: CheckCircle2 },
  { label: '排名追踪', value: 'Ranking', icon: BarChart3 },
]

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
  {
    description: '关注竞赛安排，进入限时挑战节奏。',
    icon: Trophy,
    label: '竞赛中心',
    to: '/contests',
  },
]

const tracks = ['数组与字符串', '搜索与图论', '动态规划', '数据结构']

function HomePage() {
  return (
    <main className="min-h-[calc(100svh-3.5rem)] bg-background">
      <section className="border-b bg-[linear-gradient(180deg,var(--background)_0%,var(--muted)_100%)]">
        <div className="mx-auto grid w-full max-w-7xl gap-8 px-4 py-10 sm:px-6 lg:grid-cols-[minmax(0,1.05fr)_minmax(22rem,0.95fr)] lg:items-center lg:py-14">
          <div className="flex flex-col gap-7">
            <div className="inline-flex w-fit items-center gap-2 rounded-md border bg-background px-3 py-1 text-sm font-medium text-muted-foreground">
              <Flame className="size-4 text-orange-500" />
              OJ Console
            </div>
            <div className="max-w-3xl">
              <h1 className="text-4xl font-semibold tracking-tight text-foreground sm:text-5xl">
                把每一次练习变成可追踪的进步
              </h1>
              <p className="mt-5 max-w-2xl text-base leading-7 text-muted-foreground sm:text-lg">
                从题库筛选、在线编码、提交判题到排名反馈，围绕日常训练整理一条清晰的刷题路径。
              </p>
            </div>
            <div className="flex flex-col gap-3 sm:flex-row">
              <Button asChild size="lg">
                <Link to="/problems">
                  <Play />
                  开始刷题
                </Link>
              </Button>
              <Button asChild size="lg" variant="outline">
                <Link to="/rankings">
                  查看排名
                  <ArrowRight />
                </Link>
              </Button>
            </div>
          </div>

          <div className="rounded-md border bg-background shadow-sm">
            <div className="flex items-center justify-between border-b px-4 py-3">
              <div className="flex items-center gap-2 text-sm font-medium">
                <Code2 className="size-4 text-primary" />
                今日训练
              </div>
              <div className="rounded-md bg-emerald-500/10 px-2 py-1 text-xs font-medium text-emerald-700 dark:text-emerald-300">
                Ready
              </div>
            </div>
            <div className="space-y-4 p-4">
              <div className="grid grid-cols-3 gap-2">
                {stats.map((item) => (
                  <div className="rounded-md border bg-muted/30 p-3" key={item.label}>
                    <item.icon className="mb-3 size-4 text-muted-foreground" />
                    <div className="truncate text-sm font-semibold">{item.value}</div>
                    <div className="mt-1 truncate text-xs text-muted-foreground">
                      {item.label}
                    </div>
                  </div>
                ))}
              </div>
              <div className="rounded-md border bg-muted/20 p-4">
                <div className="mb-3 flex items-center justify-between gap-3">
                  <div>
                    <div className="text-sm font-semibold">推荐训练路径</div>
                    <div className="mt-1 text-xs text-muted-foreground">
                      按主题推进，适合连续练习
                    </div>
                  </div>
                  <Button asChild size="sm" variant="secondary">
                    <Link to="/problems">进入</Link>
                  </Button>
                </div>
                <div className="grid gap-2 sm:grid-cols-2">
                  {tracks.map((track, index) => (
                    <div
                      className="flex items-center gap-2 rounded-md border bg-background px-3 py-2 text-sm"
                      key={track}
                    >
                      <span className="flex size-6 shrink-0 items-center justify-center rounded-md bg-primary text-xs font-semibold text-primary-foreground">
                        {index + 1}
                      </span>
                      <span className="truncate">{track}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

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
        <div className="grid gap-3 md:grid-cols-3">
          {quickLinks.map((item) => (
            <Link
              className="group rounded-md border bg-background p-4 transition-colors hover:border-primary/40 hover:bg-muted/30"
              key={item.to}
              to={item.to}
            >
              <div className="mb-5 flex items-center justify-between gap-3">
                <span className="flex size-10 items-center justify-center rounded-md bg-muted text-foreground">
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
    </main>
  )
}

export default HomePage
