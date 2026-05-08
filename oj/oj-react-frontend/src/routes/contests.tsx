import { createFileRoute } from '@tanstack/react-router'
import { Trophy } from 'lucide-react'
import PlaceholderPage from '@/pages/PlaceholderPage'

export const Route = createFileRoute('/contests')({
  component: ContestsPage,
})

function ContestsPage() {
  return (
    <PlaceholderPage
      title="竞赛中心"
      description="这里会展示正在进行和即将开始的比赛，后续可以接入报名、倒计时、排行榜和赛时提交。"
      icon={Trophy}
    />
  )
}
