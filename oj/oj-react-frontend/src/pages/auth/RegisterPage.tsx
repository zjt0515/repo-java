import { Loader2, UserPlus } from 'lucide-react'
import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from '@tanstack/react-router'
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
import { getUserRequestErrorMessage, registerUser } from '@/services/userService'

function RegisterPage() {
  const navigate = useNavigate()
  const [userAccount, setUserAccount] = useState('')
  const [userPassword, setUserPassword] = useState('')
  const [checkPassword, setCheckPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    // 数据校验
    if (!userAccount.trim() || !userPassword || !checkPassword) {
      setError('请完整填写注册信息')
      return
    }
    if (userPassword !== checkPassword) {
      setError('两次输入的密码不一致')
      return
    }

    setSubmitting(true)
    try {
      await registerUser({
        checkPassword,
        userAccount: userAccount.trim(),
        userPassword,
      })
      toast.success('注册成功，请登录')
      await navigate({
        replace: true,
        search: { account: userAccount.trim() },
        to: '/user/login',
      })
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
      <section className="w-full max-w-md overflow-hidden rounded-lg border bg-background shadow-sm">
        <div className="p-6 sm:p-8">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">创建账号</h1>
            <p className="mt-2 text-sm text-muted-foreground">
              注册后即可提交代码、保存进度并查看个人训练数据
            </p>
          </div>

          <form className="mt-7" onSubmit={handleSubmit}>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="registerAccount">账号</FieldLabel>
                <Input
                  autoComplete="username"
                  id="registerAccount"
                  onChange={(event) => setUserAccount(event.target.value)}
                  placeholder="请输入账号"
                  value={userAccount}
                />
              </Field>

              <Field>
                <FieldLabel htmlFor="registerPassword">密码</FieldLabel>
                <Input
                  autoComplete="new-password"
                  id="registerPassword"
                  onChange={(event) => setUserPassword(event.target.value)}
                  placeholder="请输入密码"
                  type="password"
                  value={userPassword}
                />
              </Field>

              <Field>
                <FieldLabel htmlFor="checkPassword">确认密码</FieldLabel>
                <Input
                  autoComplete="new-password"
                  id="checkPassword"
                  onChange={(event) => setCheckPassword(event.target.value)}
                  placeholder="请再次输入密码"
                  type="password"
                  value={checkPassword}
                />
              </Field>

              <FieldError>{error}</FieldError>

              <Button className="w-full" disabled={submitting} type="submit">
                {submitting ? <Loader2 className="animate-spin" /> : <UserPlus />}
                注册
              </Button>

              <FieldDescription className="text-center">
                已经有账号？{' '}
                <Link className="font-medium text-foreground" to="/user/login">
                  去登录
                </Link>
              </FieldDescription>
            </FieldGroup>
          </form>
        </div>
      </section>
    </main>
  )
}

export default RegisterPage
