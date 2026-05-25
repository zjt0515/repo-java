declare module 'react-calendar-heatmap' {
  import type { MouseEvent, ReactElement, SVGProps } from 'react'

  type DateLike = Date | number | string

  export type CalendarHeatmapValue = {
    date: DateLike
  }

  export type CalendarHeatmapProps<T extends CalendarHeatmapValue> = {
    classForValue?: (value?: T) => string
    endDate?: DateLike
    gutterSize?: number
    horizontal?: boolean
    monthLabels?: string[]
    onClick?: (value?: T) => void
    onMouseLeave?: (
      event: MouseEvent<SVGRectElement>,
      value?: T,
    ) => void
    onMouseOver?: (
      event: MouseEvent<SVGRectElement>,
      value?: T,
    ) => void
    showMonthLabels?: boolean
    showOutOfRangeDays?: boolean
    showWeekdayLabels?: boolean
    startDate?: DateLike
    titleForValue?: (value?: T) => string
    tooltipDataAttrs?:
      | Record<string, string>
      | ((value?: T) => Record<string, string>)
    transformDayElement?: (
      element: ReactElement<SVGProps<SVGRectElement>>,
      value: T | undefined,
      index: number,
    ) => ReactElement
    values: T[]
    weekdayLabels?: string[]
  }

  export default function CalendarHeatmap<T extends CalendarHeatmapValue>(
    props: CalendarHeatmapProps<T>,
  ): ReactElement
}
