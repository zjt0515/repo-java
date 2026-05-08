import { Code2 } from 'lucide-react'
import { Link } from '@tanstack/react-router'

function Footer() {
  const currentYear = new Date().getFullYear()
  const footerLinks = [
    { label: '问题反馈', to: '/footer/feedback' },
    { label: '隐私政策', to: '/footer/privacy' },
    { label: '使用条款', to: '/footer/terms' },
  ] as const
  
  return (
    <footer className="border-t bg-background/95">
      <div className="flex flex-col gap-4 px-4 py-5 text-sm text-muted-foreground sm:px-5 md:flex-row md:items-center">
        <div className="flex items-center gap-2">
          <div className="flex size-8 items-center justify-center rounded-lg bg-muted text-foreground">
            <Code2 className="size-4" />
          </div>
          <div>
            <div className="font-medium text-foreground">OJ Console</div>
            <div className="text-xs">在线刷题与评测平台</div>
          </div>
        </div>

        <div className="text-xs md:ml-6">
          Copyright {currentYear} OJ Console
        </div>

        <nav className="flex flex-wrap items-center gap-x-4 gap-y-2 md:ml-auto md:justify-end">
          {footerLinks.map((item) => (
            <Link
              className="transition-colors hover:text-foreground"
              key={item.to}
              to={item.to}
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </div>
    </footer>
  )
}

export default Footer
