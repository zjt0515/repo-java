import { Link, Outlet, useLocation } from '@tanstack/react-router'
import { FileQuestion, LayoutDashboard, Users, ChevronsLeft, ChevronsRight } from 'lucide-react'
import { useState } from 'react'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'

const sidebarItems = [
  { label: '管理概览', to: '/admin', icon: LayoutDashboard },
  { label: '题目管理', to: '/admin/questions', icon: FileQuestion },
  { label: '用户管理', to: '/admin/users', icon: Users },
]

function AdminLayout() {
  const { pathname } = useLocation()
  const [collapsed, setCollapsed] = useState(false)

  return (
    <div className="flex min-h-[calc(100svh-3.5rem)] flex-col md:flex-row">
      {/* Sidebar */}
      <aside
        className={cn(
          'border-b bg-background md:flex-shrink-0 md:border-b-0 md:border-r transition-all duration-300',
          collapsed ? 'md:w-16' : 'md:w-56'
        )}
      >
        <div className="flex items-center justify-between p-3 md:p-4">
          <h2
            className={cn(
              'text-xs font-semibold uppercase tracking-wider text-muted-foreground transition-opacity duration-300',
              collapsed && 'md:hidden'
            )}
          >
            管理后台
          </h2>
          <Button
            variant="ghost"
            size="icon"
            className="hidden md:flex size-7"
            onClick={() => setCollapsed((v) => !v)}
            aria-label={collapsed ? '展开侧边栏' : '折叠侧边栏'}
          >
            {collapsed ? <ChevronsRight className="size-4" /> : <ChevronsLeft className="size-4" />}
          </Button>
        </div>
        <nav className="flex flex-row gap-1 px-2 pb-2 md:flex-col md:space-y-1 md:pb-0">
          {sidebarItems.map((item) => {
            const isActive =
              pathname === item.to ||
              (item.to !== '/admin' && pathname.startsWith(item.to))

            return (
              <Link
                key={item.to}
                to={item.to}
                className={cn(
                  'flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium transition-colors whitespace-nowrap',
                  collapsed && 'md:justify-center md:px-2',
                  isActive
                    ? 'bg-muted text-foreground'
                    : 'text-muted-foreground hover:bg-muted hover:text-foreground'
                )}
                title={item.label}
              >
                <item.icon className="size-4 flex-shrink-0" />
                <span className={cn('transition-opacity duration-300', collapsed && 'md:hidden')}>
                  {item.label}
                </span>
              </Link>
            )
          })}
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex flex-1 flex-col overflow-auto bg-muted/20">
        <div className="flex flex-1 flex-col">
          <Outlet />
        </div>
      </main>
    </div>
  )
}

export default AdminLayout
