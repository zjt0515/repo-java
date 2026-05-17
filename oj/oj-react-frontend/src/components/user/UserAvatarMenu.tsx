import {
  ClipboardList,
  Laptop,
  LogOut,
  Moon,
  Settings,
  Sun,
  UserCircle,
} from 'lucide-react'
import { Link } from '@tanstack/react-router'
import { useState } from 'react'
import { toast } from 'sonner'
import { type Theme, useTheme } from '@/components/user/theme'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuSeparator,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import type { LoginUserVO } from '@/services/userService'
import { useUserStore } from '@/stores/user'

type UserAvatarMenuProps = {
  user: LoginUserVO
}

function UserAvatarMenu({ user }: UserAvatarMenuProps) {
  const { setTheme, theme } = useTheme()
  const displayName = user.userName || user.userRole || '用户'

  // logout
  const logout = useUserStore((state) => state.logout)
  const [loggingOut, setLoggingOut] = useState(false)

  async function handleLogout() {
    setLoggingOut(true)
    try {
      await logout()
      toast.success('已退出登录')
    } catch {
      toast.error('退出登录失败，请稍后再试')
    } finally {
      setLoggingOut(false)
    }
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          aria-label="打开用户菜单"
          className="gap-2 px-2"
          disabled={loggingOut}
          size="sm"
          variant="ghost"
        >
          <span className="flex size-7 shrink-0 items-center justify-center overflow-hidden rounded-lg border bg-muted">
            {user.userAvatar ? (
              <img
                alt={displayName}
                className="size-full object-cover"
                src={user.userAvatar}
              />
            ) : (
              <UserCircle className="size-5 text-muted-foreground" />
            )}
          </span>
          <span className="hidden max-w-24 truncate text-sm sm:inline">
            {displayName}
          </span>
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent align="end" className="w-56">
        <DropdownMenuLabel className="space-y-1">
          <div className="truncate text-sm font-medium text-foreground">
            {displayName}
          </div>
          <div className="truncate text-xs font-normal text-muted-foreground">
            ID: {user.id ?? '未知'}
          </div>
        </DropdownMenuLabel>

        <DropdownMenuSeparator />

        <DropdownMenuItem asChild>
          <Link to="/user/profile">
            <Settings />
            账号设置
          </Link>
        </DropdownMenuItem>

        <DropdownMenuItem asChild>
          <Link to="/user/submissions">
            <ClipboardList />
            提交查询
          </Link>
        </DropdownMenuItem>

        <DropdownMenuSub>
          <DropdownMenuSubTrigger>
            {theme === 'dark' ? <Moon /> : <Sun />}
            切换主题
          </DropdownMenuSubTrigger>
          <DropdownMenuSubContent className="w-36">
            <DropdownMenuRadioGroup
              onValueChange={(value) => setTheme(value as Theme)}
              value={theme}
            >
              <DropdownMenuRadioItem value="light">
                <Sun />
                浅色
              </DropdownMenuRadioItem>
              <DropdownMenuRadioItem value="dark">
                <Moon />
                深色
              </DropdownMenuRadioItem>
              <DropdownMenuRadioItem value="system">
                <Laptop />
                跟随系统
              </DropdownMenuRadioItem>
            </DropdownMenuRadioGroup>
          </DropdownMenuSubContent>
        </DropdownMenuSub>

        <DropdownMenuSeparator />

        <DropdownMenuItem
          disabled={loggingOut}
          onSelect={() => void handleLogout()}
          variant="destructive"
        >
          <LogOut />
          {loggingOut ? '退出中' : '退出登录'}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

export default UserAvatarMenu
