import { ShieldCheck } from 'lucide-react'
import { createFileRoute } from '@tanstack/react-router'
import InfoPage from '@/pages/InfoPage'

export const Route = createFileRoute('/footer/privacy')({
  component: PrivacyPage,
})

function PrivacyPage() {
  return (
    <InfoPage
      description="我们会尽量只收集维持在线评测、账号登录和学习记录所需的信息，并以必要、透明的方式使用。"
      icon={ShieldCheck}
      sections={[
        {
          content:
            '平台可能保存账号信息、提交记录、题目互动记录和基础访问日志，用于身份识别、评测展示和问题排查。',
          title: '信息收集',
        },
        {
          content:
            '相关信息主要用于提供刷题、提交评测、排行榜、训练进度和安全风控等功能。',
          title: '信息使用',
        },
        {
          content:
            '我们不会主动公开你的个人资料。涉及公开展示的内容通常限于昵称、排名、提交状态等平台功能所需信息。',
          title: '信息展示',
        },
      ]}
      title="隐私政策"
    />
  )
}
