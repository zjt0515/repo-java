import {
  CalendarClock,
  ImageIcon,
  Loader2,
  Save,
  Shield,
  UserCircle,
  type LucideIcon,
} from 'lucide-react'
import { useMemo, useState, type FormEvent } from 'react'
import { Link } from '@tanstack/react-router'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import {
  Field,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Separator } from '@/components/ui/separator'
import { Textarea } from '@/components/ui/textarea'
import {
  getUserRequestErrorMessage,
  updateMyUser,
  type LoginUserVO,
  type UserUpdateMyRequest,
} from '@/services/userService'
import { useUserStore } from '@/stores/user'

function formatDateTime(value?: string) {
  if (!value) {
    return '暂无记录'
  }

  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  return date.toLocaleString('zh-CN', {
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

function ProfilePage() {
  const currentUser = useUserStore((state) => state.currentUser)

  if (!currentUser) {
    return (
      <main className="flex min-h-[calc(100svh-3.5rem)] items-center justify-center p-6">
        <section className="w-full max-w-md rounded-lg border bg-background p-6 text-center">
          <div className="mx-auto flex size-10 items-center justify-center rounded-lg bg-muted">
            <UserCircle className="size-5 text-muted-foreground" />
          </div>
          <h1 className="mt-4 text-2xl font-semibold tracking-tight">
            请先登录
          </h1>
          <p className="mt-2 text-sm text-muted-foreground">
            登录后可以查看和修改个人资料。
          </p>
          <Button asChild className="mt-5">
            <Link to="/user/login">去登录</Link>
          </Button>
        </section>
      </main>
    )
  }

  return (
    <ProfileEditor currentUser={currentUser} />
  )
}

function ProfileEditor({
  currentUser,
}: {
  currentUser: LoginUserVO
}) {
  const setCurrentUser = useUserStore((state) => state.setCurrentUser)
  const [userName, setUserName] = useState(currentUser.userName ?? '')
  const [userAvatar, setUserAvatar] = useState(currentUser.userAvatar ?? '')
  const [userProfile, setUserProfile] = useState(currentUser.userProfile ?? '')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const profilePayload = useMemo<UserUpdateMyRequest>(
    () => ({
      userAvatar: userAvatar.trim(),
      userName: userName.trim(),
      userProfile: userProfile.trim(),
    }),
    [userAvatar, userName, userProfile],
  )

  const isDirty =
    (currentUser.userName ?? '') !== userName ||
    (currentUser.userAvatar ?? '') !== userAvatar ||
    (currentUser.userProfile ?? '') !== userProfile

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    if (userName.trim().length > 50) {
      setError('昵称不能超过 50 个字符')
      return
    }

    if (userProfile.trim().length > 300) {
      setError('个人简介不能超过 300 个字符')
      return
    }

    setSaving(true)
    try {
      await updateMyUser(profilePayload)
      setCurrentUser({ ...currentUser, ...profilePayload })
      toast.success('个人信息已更新')
    } catch (requestError) {
      const message = getUserRequestErrorMessage(requestError)
      setError(message)
      toast.error(message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <main className="bg-muted/20 px-4 py-6 sm:px-6 lg:py-8">
      <div className="mx-auto grid max-w-6xl gap-6 lg:grid-cols-[18rem_1fr]">
        <aside className="rounded-lg border bg-background p-5 shadow-sm">
          <div className="flex flex-col items-center text-center">
            <div className="flex size-24 items-center justify-center overflow-hidden rounded-lg border bg-muted">
              {currentUser.userAvatar ? (
                <img
                  alt={currentUser.userName || '用户头像'}
                  className="size-full object-cover"
                  src={currentUser.userAvatar}
                />
              ) : (
                <UserCircle className="size-12 text-muted-foreground" />
              )}
            </div>
            <h1 className="mt-4 max-w-full break-words text-xl font-semibold tracking-tight">
              {currentUser.userName || '未设置昵称'}
            </h1>
            <p className="mt-1 text-sm text-muted-foreground">
              ID: {currentUser.id ?? '未知'}
            </p>
          </div>

          <Separator className="my-5" />

          <div className="space-y-4 text-sm">
            <InfoRow
              icon={Shield}
              label="用户角色"
              value={currentUser.userRole || '普通用户'}
            />
            <InfoRow
              icon={CalendarClock}
              label="注册时间"
              value={formatDateTime(currentUser.createTime)}
            />
            <InfoRow
              icon={CalendarClock}
              label="更新时间"
              value={formatDateTime(currentUser.updateTime)}
            />
          </div>
        </aside>

        <section className="rounded-lg border bg-background p-5 shadow-sm sm:p-6">
          <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
            <div>
              <h2 className="text-2xl font-semibold tracking-tight">
                个人资料
              </h2>
              <p className="mt-1 text-sm text-muted-foreground">
                维护你的公开昵称、头像和个人简介。
              </p>
            </div>
          </div>

          <form className="mt-6" onSubmit={handleSubmit}>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="profileName">昵称</FieldLabel>
                <Input
                  id="profileName"
                  maxLength={50}
                  onChange={(event) => setUserName(event.target.value)}
                  placeholder="请输入昵称"
                  value={userName}
                />
                <FieldDescription>
                  其他用户会在题解、提交记录和排名中看到这个名字。
                </FieldDescription>
              </Field>

              <Field>
                <FieldLabel htmlFor="profileAvatar">头像地址</FieldLabel>
                <div className="flex gap-2">
                  <Input
                    id="profileAvatar"
                    onChange={(event) => setUserAvatar(event.target.value)}
                    placeholder="https://example.com/avatar.png"
                    value={userAvatar}
                  />
                  <Button disabled type="button" variant="outline">
                    <ImageIcon />
                  </Button>
                </div>
                <FieldDescription>
                  当前版本使用图片链接作为头像来源。
                </FieldDescription>
              </Field>

              <Field>
                <FieldLabel htmlFor="profileBio">个人简介</FieldLabel>
                <Textarea
                  className="min-h-28 resize-none"
                  id="profileBio"
                  maxLength={300}
                  onChange={(event) => setUserProfile(event.target.value)}
                  placeholder="写一点你的训练方向、擅长语言或刷题目标"
                  value={userProfile}
                />
                <FieldDescription>
                  {userProfile.trim().length}/300
                </FieldDescription>
              </Field>

              <FieldError>{error}</FieldError>

              <div className="flex flex-col gap-2 sm:flex-row sm:justify-end">
                <Button asChild type="button" variant="outline">
                  <Link to="/problems">返回题库</Link>
                </Button>
                <Button disabled={saving || !isDirty} type="submit">
                  {saving ? <Loader2 className="animate-spin" /> : <Save />}
                  保存修改
                </Button>
              </div>
            </FieldGroup>
          </form>
        </section>
      </div>
    </main>
  )
}

function InfoRow({
  icon: Icon,
  label,
  value,
}: {
  icon: LucideIcon
  label: string
  value: string
}) {
  return (
    <div className="flex items-start gap-3">
      <div className="mt-0.5 flex size-8 shrink-0 items-center justify-center rounded-lg bg-muted text-muted-foreground">
        <Icon className="size-4" />
      </div>
      <div className="min-w-0">
        <div className="text-xs text-muted-foreground">{label}</div>
        <div className="mt-0.5 break-words font-medium">{value}</div>
      </div>
    </div>
  )
}

export default ProfilePage
