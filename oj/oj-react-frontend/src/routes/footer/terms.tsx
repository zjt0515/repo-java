import { ScrollText } from 'lucide-react'
import { createFileRoute } from '@tanstack/react-router'
import { platformConfig } from '@/config/platform'
import InfoPage from '@/pages/InfoPage'

export const Route = createFileRoute('/footer/terms')({
  component: TermsPage,
})

function TermsPage() {
  return (
    <InfoPage
      description={platformConfig.legal.termsDescription}
      icon={ScrollText}
      sections={[
        {
          content:
            '请勿提交恶意代码、攻击性请求、刷量脚本或其他影响平台稳定性和他人正常使用的内容。',
          title: '使用规范',
        },
        {
          content:
            '用户提交的代码和题解应遵守题目要求与社区规范。涉及抄袭、作弊或违规行为时，平台可限制相关功能。',
          title: '内容责任',
        },
        {
          content:
            '平台会持续维护评测服务，但不保证所有情况下都完全无错误。遇到异常时，可通过问题反馈页面说明情况。',
          title: '服务说明',
        },
      ]}
      title="使用条款"
    />
  )
}
