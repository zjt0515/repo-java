import { createFileRoute } from '@tanstack/react-router'
import { Medal } from 'lucide-react'
import PlaceholderPage from '@/pages/PlaceholderPage'

export const Route = createFileRoute('/rankings')({
  component: RankingsPage,
})

function RankingsPage() {
  return (
    <PlaceholderPage
      title="排行榜"
      description="这里会展示用户解题数、通过率、竞赛积分和近期活跃度。"
      icon={Medal}
    />
  )
}
