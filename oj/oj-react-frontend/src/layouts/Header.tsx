import { Bell, Code2, Menu, Search } from 'lucide-react'
import { Link } from '@tanstack/react-router'
import { ModeToggle } from '@/components/user/ModeToggle'
import UserAvatarMenu from '@/components/user/UserAvatarMenu'
import { Button, buttonVariants } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { platformConfig } from '@/config/platform'
import { ACCESS_ENUM, checkAccess, type AccessType } from '@/lib/auth/accessCheck'
import { cn } from '@/lib/utils'
import { useUserStore } from '@/stores/user'

type NavItem = {
  access?: AccessType
  label: string
  to: string
}

const navItems: NavItem[] = [
  { label: '首页', to: '/' },
  { label: '题库', to: '/problems' },
  // { label: '竞赛', to: '/contests' },
  // { label: '排名', to: '/rankings' },
  { access: ACCESS_ENUM.ADMIN, label: '管理', to: '/admin' },
]

function Header() {
  const currentUser = useUserStore((state) => state.currentUser)

  return (
    <header className="sticky top-0 z-20 border-b bg-background/95 backdrop-blur">
      <div className="flex h-14 items-center gap-3 px-4 sm:px-5">
        <Button variant="ghost" size="icon" className="hidden lg:hidden">
          <Menu />
        </Button>
        <Link className="flex min-w-0 items-center gap-2" to="/">
          <div className="flex size-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
            <Code2 className="size-4" />
          </div>
          <div className="leading-none">
            <div className="text-sm font-semibold">{platformConfig.name}</div>
            <div className="text-xs text-muted-foreground">
              {platformConfig.header.subtitle}
            </div>
          </div>
        </Link>
        <nav className="ml-5 hidden items-center gap-1 md:flex">
          {navItems
            .filter((item) => checkAccess(currentUser, item.access))
            .map((item) => (
              <Link
                key={item.to}
                activeProps={{ className: 'bg-muted text-foreground' }}
                className={cn(buttonVariants({ variant: 'ghost', size: 'sm' }))}
                to={item.to}
              >
                {item.label}
              </Link>
            ))}
        </nav>
        <div className="ml-auto flex items-center gap-2">
          <div className="relative hidden w-64">
            <Search className="pointer-events-none absolute left-2.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              className="bg-muted/40 pl-8"
              placeholder="搜索题目、标签、提交记录"
            />
          </div>
          <Button variant="ghost" size="icon" className="hidden">
            <Bell />
          </Button>
          {/* avatar */}
          {currentUser && <UserAvatarMenu user={currentUser} />}
          {/* login button and register button */}
          {!currentUser && (
            <div className="flex items-center gap-2">
              <ModeToggle />
              <Button asChild size="sm" variant="ghost">
                <Link to="/user/login">登录</Link>
              </Button>
              <Button asChild size="sm">
                <Link to="/user/register">注册</Link>
              </Button>
            </div>
          )}
        </div>
      </div>
    </header>
  )
}

export default Header
