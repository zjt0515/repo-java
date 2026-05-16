import { Link, Outlet, useLocation } from '@tanstack/react-router'
import { FileQuestion, LayoutDashboard, Users } from 'lucide-react'
import { cn } from '@/lib/utils'

const sidebarItems = [
  { label: '管理概览', to: '/admin', icon: LayoutDashboard },
  { label: '题目管理', to: '/admin/questions', icon: FileQuestion },
  { label: '用户管理', to: '/admin/users', icon: Users },
]

function AdminLayout() {
  const { pathname } = useLocation()

  return (
    <div className="flex min-h-[calc(100svh-3.5rem)] flex-col md:flex-row">
      {/* Sidebar */}
      <aside className="border-b bg-background md:w-56 md:flex-shrink-0 md:border-b-0 md:border-r">
        <div className="p-3 md:p-4">
          <h2 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            管理后台
          </h2>
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
                  isActive
                    ? 'bg-muted text-foreground'
                    : 'text-muted-foreground hover:bg-muted hover:text-foreground'
                )}
              >
                <item.icon className="size-4" />
                {item.label}
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
