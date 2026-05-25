import { Link } from '@tanstack/react-router'
import {
  Activity,
  CalendarDays,
  Loader2,
  LogIn,
  RefreshCw,
  TrendingUp,
} from 'lucide-react'
import { useCallback, useEffect, useMemo, useState } from 'react'
import CalendarHeatmap from 'react-calendar-heatmap'
import 'react-calendar-heatmap/dist/styles.css'
import { Button } from '@/components/ui/button'
import {
  getQuestionRequestErrorMessage,
  listQuestionSubmissions,
  type QuestionSubmitVO,
} from '@/services/questionService'
import { useUserStore } from '@/stores/user'

const PAGE_SIZE = 200
const MAX_PAGES = 10
const MONTH_LABELS = [
  '1月',
  '2月',
  '3月',
  '4月',
  '5月',
  '6月',
  '7月',
  '8月',
  '9月',
  '10月',
  '11月',
  '12月',
]
const WEEKDAY_LABELS = ['日', '一', '二', '三', '四', '五', '六']

type LoadStatus = 'loading' | 'ready' | 'anonymous' | 'error'

type HeatMapValue = {
  accepted: number
  count: number
  date: string
}

type DailyCount = {
  accepted: number
  count: number
}

type HeatMapState = {
  error?: string
  status: LoadStatus
  values: HeatMapValue[]
}

function HeatMap() {
  const currentUser = useUserStore((state) => state.currentUser)
  const ensureCurrentUser = useUserStore((state) => state.ensureCurrentUser)
  const range = useMemo(() => getThreeMonthRange(), [])
  const [state, setState] = useState<HeatMapState>({
    status: 'loading',
    values: createEmptyValues(range.start, range.end),
  })

  const summary = useMemo(() => summarizeValues(state.values), [state.values])

  const loadData = useCallback(async () => {
    setState((previous) => ({
      ...previous,
      error: undefined,
      status: 'loading',
    }))

    try {
      const user = currentUser ?? (await ensureCurrentUser())

      if (!user) {
        setState({
          status: 'anonymous',
          values: createEmptyValues(range.start, range.end),
        })
        return
      }

      const submissions = await loadRecentSubmissions(user.id, range.start)
      const values = buildHeatMapValues(range.start, range.end, submissions)

      setState({ status: 'ready', values })
    } catch (error) {
      setState({
        error: getQuestionRequestErrorMessage(error),
        status: 'error',
        values: createEmptyValues(range.start, range.end),
      })
    }
  }, [currentUser, ensureCurrentUser, range.end, range.start])

  useEffect(() => {
    void loadData()
  }, [loadData])

  const loading = state.status === 'loading'
  const message = getFeedbackMessage(state.status, summary.total, state.error)

  return (
    <section className="home-submit-heatmap rounded-xl border bg-background shadow-sm">
      <div className="flex items-center justify-between gap-3 border-b px-4 py-3">
        <div className="flex min-w-0 items-center gap-2">
          <CalendarDays className="size-4 shrink-0 text-primary" />
          <div className="min-w-0">
            <h2 className="truncate text-sm font-semibold">提交热力图</h2>
            <p className="truncate text-xs text-muted-foreground">近 3 个月</p>
          </div>
        </div>

        <Button
          aria-label="刷新提交热力图"
          disabled={loading}
          onClick={loadData}
          size="icon"
          title="刷新"
          type="button"
          variant="ghost"
        >
          {loading ? (
            <Loader2 className="size-4 animate-spin" />
          ) : (
            <RefreshCw className="size-4" />
          )}
        </Button>
      </div>

      <div className="space-y-4 p-4">
        <div className="flex items-start justify-between gap-3">
          <div className="min-w-0">
            <div className="flex items-center gap-2 text-sm font-medium">
              <Activity className="size-4 text-emerald-600 dark:text-emerald-400" />
              训练反馈
            </div>
            <p className="mt-1 text-xs leading-5 text-muted-foreground">
              {message}
            </p>
          </div>

          {state.status === 'anonymous' && (
            <Button asChild size="sm" variant="secondary">
              <Link to="/user/login">
                <LogIn />
                登录
              </Link>
            </Button>
          )}
        </div>

        <div className="grid grid-cols-4 divide-x rounded-lg border bg-muted/20">
          <SummaryItem label="提交" value={summary.total} />
          <SummaryItem label="AC" value={summary.accepted} />
          <SummaryItem label="活跃" value={`${summary.activeDays} 天`} />
          <SummaryItem label="连续" value={`${summary.currentStreak} 天`} />
        </div>

        <div className="overflow-hidden rounded-lg border bg-muted/10 px-3 py-3">
          <CalendarHeatmap<HeatMapValue>
            classForValue={getClassForValue}
            endDate={range.end}
            gutterSize={3}
            monthLabels={MONTH_LABELS}
            showMonthLabels
            showOutOfRangeDays={false}
            showWeekdayLabels
            startDate={range.start}
            titleForValue={getTitleForValue}
            values={state.values}
            weekdayLabels={WEEKDAY_LABELS}
          />
        </div>

        <div className="flex flex-wrap items-center justify-between gap-3 text-xs text-muted-foreground">
          <span>
            {formatShortDate(range.start)} - {formatShortDate(range.end)}
          </span>
          <div className="flex items-center gap-1.5">
            <TrendingUp className="size-3.5" />
            <span>少</span>
            {[0, 1, 2, 3, 4].map((level) => (
              <span
                aria-hidden="true"
                className={`size-3 rounded-[3px] border heatmap-preview-${level}`}
                key={level}
              />
            ))}
            <span>多</span>
          </div>
        </div>
      </div>
    </section>
  )
}

function SummaryItem({ label, value }: { label: string; value: number | string }) {
  return (
    <div className="min-w-0 px-2 py-2.5 text-center">
      <div className="truncate text-sm font-semibold tabular-nums sm:text-base">
        {value}
      </div>
      <div className="mt-0.5 truncate text-[11px] text-muted-foreground">
        {label}
      </div>
    </div>
  )
}

async function loadRecentSubmissions(userId: number | undefined, startDate: Date) {
  const submissions: QuestionSubmitVO[] = []

  for (let current = 1; current <= MAX_PAGES; current += 1) {
    const page = await listQuestionSubmissions({
      current,
      pageSize: PAGE_SIZE,
      sortField: 'id',
      sortOrder: 'descend',
      userId,
    })
    const records = page.records ?? []

    submissions.push(...records)

    if (records.length < PAGE_SIZE || pageIsBeforeRange(records, startDate)) {
      break
    }
  }

  return submissions.filter((submission) => {
    const submittedAt = getSubmissionDate(submission)

    return submittedAt !== undefined && submittedAt >= startDate
  })
}

function buildHeatMapValues(
  startDate: Date,
  endDate: Date,
  submissions: QuestionSubmitVO[],
) {
  const counts = new Map<string, DailyCount>()

  for (const submission of submissions) {
    const submittedAt = getSubmissionDate(submission)

    if (!submittedAt || submittedAt < startDate || submittedAt > endDate) {
      continue
    }

    const key = formatDateKey(submittedAt)
    const current = counts.get(key) ?? { accepted: 0, count: 0 }

    current.count += 1
    if (submission.judgeInfo?.message === 'Accepted') {
      current.accepted += 1
    }

    counts.set(key, current)
  }

  return createDateKeys(startDate, endDate).map((date) => {
    const count = counts.get(date) ?? { accepted: 0, count: 0 }

    return {
      accepted: count.accepted,
      count: count.count,
      date,
    }
  })
}

function createEmptyValues(startDate: Date, endDate: Date) {
  return createDateKeys(startDate, endDate).map((date) => ({
    accepted: 0,
    count: 0,
    date,
  }))
}

function summarizeValues(values: HeatMapValue[]) {
  const total = values.reduce((sum, value) => sum + value.count, 0)
  const accepted = values.reduce((sum, value) => sum + value.accepted, 0)
  const activeDays = values.filter((value) => value.count > 0).length
  let currentStreak = 0

  for (let index = values.length - 1; index >= 0; index -= 1) {
    if (values[index].count === 0) {
      break
    }
    currentStreak += 1
  }

  return { accepted, activeDays, currentStreak, total }
}

function pageIsBeforeRange(records: QuestionSubmitVO[], startDate: Date) {
  const oldestTime = records.reduce<number | undefined>((oldest, submission) => {
    const submittedAt = getSubmissionDate(submission)

    if (!submittedAt) {
      return oldest
    }

    return oldest === undefined
      ? submittedAt.getTime()
      : Math.min(oldest, submittedAt.getTime())
  }, undefined)

  return oldestTime !== undefined && oldestTime < startDate.getTime()
}

function getSubmissionDate(submission: QuestionSubmitVO) {
  const value = submission.createTime ?? submission.updateTime

  if (!value) {
    return undefined
  }

  const date = new Date(value.includes('T') ? value : value.replace(' ', 'T'))

  return Number.isNaN(date.getTime()) ? undefined : startOfDay(date)
}

function getThreeMonthRange() {
  const end = startOfDay(new Date())
  const start = new Date(end)
  start.setMonth(start.getMonth() - 3)

  return { end, start }
}

function createDateKeys(startDate: Date, endDate: Date) {
  const keys: string[] = []

  for (
    let date = new Date(startDate);
    date <= endDate;
    date = addDays(date, 1)
  ) {
    keys.push(formatDateKey(date))
  }

  return keys
}

function getClassForValue(value?: HeatMapValue) {
  return `home-heatmap-scale-${getLevel(value?.count ?? 0)}`
}

function getTitleForValue(value?: HeatMapValue) {
  if (!value) {
    return ''
  }

  const date = parseDateKey(value.date)
  const acceptedText = value.accepted > 0 ? `，AC ${value.accepted} 次` : ''

  return `${formatLongDate(date)}：提交 ${value.count} 次${acceptedText}`
}

function getFeedbackMessage(status: LoadStatus, total: number, error?: string) {
  if (status === 'loading') {
    return '正在同步提交记录'
  }

  if (status === 'anonymous') {
    return '登录后即可查看你的训练节奏'
  }

  if (status === 'error') {
    return error || '提交记录暂时不可用'
  }

  return total > 0 ? '颜色越深代表当天提交越密集' : '近 3 个月暂无提交记录'
}

function getLevel(count: number) {
  if (count <= 0) return 0
  if (count === 1) return 1
  if (count <= 3) return 2
  if (count <= 6) return 3

  return 4
}

function startOfDay(date: Date) {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate())
}

function addDays(date: Date, amount: number) {
  const next = new Date(date)
  next.setDate(next.getDate() + amount)

  return next
}

function parseDateKey(value: string) {
  const [year, month, day] = value.split('-').map(Number)

  return new Date(year, month - 1, day)
}

function formatDateKey(date: Date) {
  return [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, '0'),
    String(date.getDate()).padStart(2, '0'),
  ].join('-')
}

function formatShortDate(date: Date) {
  return `${date.getMonth() + 1}/${date.getDate()}`
}

function formatLongDate(date: Date) {
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

export default HeatMap
