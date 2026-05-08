import { MessageSquareText } from 'lucide-react'
import { createFileRoute } from '@tanstack/react-router'
import InfoPage from '@/pages/InfoPage'

export const Route = createFileRoute('/footer/feedback')({
  component: FeedbackPage,
})

function FeedbackPage() {
  return (
    <InfoPage
      description="如果你在刷题、提交、登录或页面使用过程中遇到问题，可以通过反馈帮助我们定位和改进体验。"
      icon={MessageSquareText}
      sections={[
        {
          content:
            '请尽量提供问题发生的页面、操作步骤、浏览器信息，以及可复现的题目或提交编号。',
          title: '反馈内容',
        },
        {
          content:
            '建议优先描述实际现象和期望结果。如果涉及评测异常，请附上语言、代码片段和运行结果。',
          title: '定位建议',
        },
        {
          content:
            '当前反馈页面为静态说明页，后续可接入表单、工单或邮件通知等能力。',
          title: '后续处理',
        },
      ]}
      title="问题反馈"
    />
  )
}
