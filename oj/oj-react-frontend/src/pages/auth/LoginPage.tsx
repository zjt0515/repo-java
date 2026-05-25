import { Loader2, LogIn } from 'lucide-react'
import { useState, type FormEvent } from 'react'
import { Link, useNavigate, useSearch } from '@tanstack/react-router'
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
import { platformConfig } from '@/config/platform'
import { getUserRequestErrorMessage } from '@/services/userService'
import { useUserStore } from '@/stores/user'

function LoginPage() {
  const navigate = useNavigate()
  const search = useSearch({ strict: false }) as { account?: string }
  const loginUser = useUserStore((state) => state.login)
  // state
  const [userAccount, setUserAccount] = useState(search.account ?? '')
  const [userPassword, setUserPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    if (!userAccount.trim() || !userPassword) {
      setError('请输入账号和密码')
      return
    }

    setSubmitting(true)
    try {
      const user = await loginUser({
        userAccount: userAccount.trim(),
        userPassword,
      })
      toast.success(`欢迎回来，${user.userName}`)
      await navigate({ replace: true, to: '/problems' })
    } catch (requestError) {
      const message = getUserRequestErrorMessage(requestError)
      setError(message)
      toast.error(message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="flex min-h-[calc(100svh-3.5rem)] items-center justify-center bg-muted/20 p-4 sm:p-6">
      <section className="grid w-full max-w-5xl overflow-hidden rounded-lg border bg-background shadow-sm md:grid-cols-[0.95fr_1.05fr]">
        <div className="hidden border-r bg-muted/30 p-8 md:flex md:flex-col md:justify-between">
          <div>
            <div className="flex size-10 items-center justify-center rounded-lg bg-primary text-primary-foreground">
              <LogIn className="size-5" />
            </div>
            <h1 className="mt-6 text-3xl font-semibold tracking-tight">
              继续做题
            </h1>
            <p className="mt-3 text-sm leading-6 text-muted-foreground">
              登录后可以同步提交记录、并提交代码。
            </p>
          </div>
        </div>

        <div className="p-6 sm:p-8">
          <div>
            <h2 className="text-2xl font-semibold tracking-tight">用户登录</h2>
            <p className="mt-2 text-sm text-muted-foreground">
              {platformConfig.auth.loginDescription}
            </p>
          </div>

          <form className="mt-7" onSubmit={handleSubmit}>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="userAccount">账号</FieldLabel>
                <Input
                  autoComplete="username"
                  id="userAccount"
                  onChange={(event) => setUserAccount(event.target.value)}
                  placeholder="请输入账号"
                  value={userAccount}
                />
              </Field>

              <Field>
                <FieldLabel htmlFor="userPassword">密码</FieldLabel>
                <Input
                  autoComplete="current-password"
                  id="userPassword"
                  onChange={(event) => setUserPassword(event.target.value)}
                  placeholder="请输入密码"
                  type="password"
                  value={userPassword}
                />
              </Field>

              <FieldError>{error}</FieldError>

              <Button className="w-full" disabled={submitting} type="submit">
                {submitting ? <Loader2 className="animate-spin" /> : <LogIn />}
                登录
              </Button>

              <FieldDescription className="text-center">
                还没有账号？{' '}
                <Link className="font-medium text-foreground" to="/user/register">
                  立即注册
                </Link>
              </FieldDescription>
            </FieldGroup>
          </form>
        </div>
      </section>
    </main>
  )
}

export default LoginPage
