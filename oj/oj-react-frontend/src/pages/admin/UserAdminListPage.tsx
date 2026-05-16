import {
  Loader2,
  Pencil,
  Plus,
  RefreshCw,
  Search,
  Trash2,
} from 'lucide-react'
import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { toast } from 'sonner'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  addUser,
  deleteUser,
  getUserRequestErrorMessage,
  listUsers,
  updateUser,
  type User,
  type UserAddRequest,
  type UserUpdateRequest,
} from '@/services/userService'

const PAGE_SIZE = 10

const ROLE_MAP: Record<string, string> = {
  admin: '管理员',
  user: '普通用户',
  ban: '已封禁',
}

const ROLE_OPTIONS = [
  { label: '管理员', value: 'admin' },
  { label: '普通用户', value: 'user' },
  { label: '已封禁', value: 'ban' },
]

function formatDateTime(value?: string) {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN', {
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

function UserAdminListPage() {
  const [keyword, setKeyword] = useState('')
  const [submittedKeyword, setSubmittedKeyword] = useState('')
  const [current, setCurrent] = useState(1)
  const [records, setRecords] = useState<User[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)
  const [deletingId, setDeletingId] = useState<number | null>(null)

  const [editDialogOpen, setEditDialogOpen] = useState(false)
  const [editingUser, setEditingUser] = useState<User | null>(null)
  const [editForm, setEditForm] = useState<Partial<UserUpdateRequest>>({})
  const [saving, setSaving] = useState(false)

  const [addDialogOpen, setAddDialogOpen] = useState(false)
  const [addForm, setAddForm] = useState<Partial<UserAddRequest>>({
    userRole: 'user',
  })
  const [adding, setAdding] = useState(false)

  const pages = Math.max(1, Math.ceil(total / PAGE_SIZE))

  const fetchUsers = useCallback(async () => {
    setLoading(true)
    try {
      const page = await listUsers({
        current,
        pageSize: PAGE_SIZE,
        sortField: 'createTime',
        sortOrder: 'descend',
        userName: submittedKeyword.trim() || undefined,
      })

      setRecords(page.records ?? [])
      setTotal(page.total ?? 0)
    } catch (error) {
      toast.error(getUserRequestErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [current, submittedKeyword])

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      void fetchUsers()
    }, 0)

    return () => window.clearTimeout(timeout)
  }, [fetchUsers])

  function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setCurrent(1)
    setSubmittedKeyword(keyword)
  }

  async function handleDelete(user: User) {
    if (!user.id) return

    const confirmed = window.confirm(
      `确认删除用户「${user.userName || user.userAccount || user.id}」？`,
    )
    if (!confirmed) return

    setDeletingId(user.id)
    try {
      await deleteUser({ id: user.id })
      toast.success('用户已删除')
      await fetchUsers()
    } catch (error) {
      toast.error(getUserRequestErrorMessage(error))
    } finally {
      setDeletingId(null)
    }
  }

  function openEditDialog(user: User) {
    setEditingUser(user)
    setEditForm({
      id: user.id,
      userName: user.userName,
      userAvatar: user.userAvatar,
      userProfile: user.userProfile,
      userRole: user.userRole,
    })
    setEditDialogOpen(true)
  }

  async function handleEditSubmit() {
    if (!editForm.id) return
    if (!editForm.userName?.trim()) {
      toast.error('用户名不能为空')
      return
    }

    setSaving(true)
    try {
      await updateUser(editForm as UserUpdateRequest)
      toast.success('用户已更新')
      setEditDialogOpen(false)
      await fetchUsers()
    } catch (error) {
      toast.error(getUserRequestErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  function openAddDialog() {
    setAddForm({ userRole: 'user' })
    setAddDialogOpen(true)
  }

  async function handleAddSubmit() {
    if (!addForm.userAccount?.trim()) {
      toast.error('用户账号不能为空')
      return
    }
    if (!addForm.userName?.trim()) {
      toast.error('用户名不能为空')
      return
    }

    setAdding(true)
    try {
      await addUser(addForm as UserAddRequest)
      toast.success('用户已创建')
      setAddDialogOpen(false)
      setCurrent(1)
      await fetchUsers()
    } catch (error) {
      toast.error(getUserRequestErrorMessage(error))
    } finally {
      setAdding(false)
    }
  }

  return (
    <div className="p-4 sm:p-6">
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-4">
        {/* Header */}
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">用户管理</h1>
            <p className="mt-1 text-sm text-muted-foreground">
              管理系统用户账号、角色和基本信息
            </p>
          </div>
          <Button onClick={openAddDialog}>
            <Plus />
            新增用户
          </Button>
        </div>

        {/* Search + Table */}
        <section className="rounded-lg border bg-background">
          <div className="flex flex-col gap-3 border-b p-3 sm:flex-row sm:items-center sm:justify-between">
            <form className="flex w-full gap-2 sm:max-w-md" onSubmit={handleSearch}>
              <div className="relative flex-1">
                <Search className="pointer-events-none absolute left-2.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  className="pl-8"
                  onChange={(event) => setKeyword(event.target.value)}
                  placeholder="按用户名搜索"
                  value={keyword}
                />
              </div>
              <Button type="submit" variant="outline">
                搜索
              </Button>
            </form>
            <Button disabled={loading} onClick={fetchUsers} variant="ghost">
              {loading ? <Loader2 className="animate-spin" /> : <RefreshCw />}
              刷新
            </Button>
          </div>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-20">ID</TableHead>
                <TableHead>用户名</TableHead>
                <TableHead className="hidden sm:table-cell">账号</TableHead>
                <TableHead>角色</TableHead>
                <TableHead className="hidden lg:table-cell">创建时间</TableHead>
                <TableHead className="w-32 text-right">操作</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {loading ? (
                Array.from({ length: PAGE_SIZE }).map((_, i) => (
                  <TableRow key={i}>
                    {Array.from({ length: 6 }).map((_, j) => (
                      <TableCell key={j}>
                        <div className="h-4 w-full animate-pulse rounded bg-muted" />
                      </TableCell>
                    ))}
                  </TableRow>
                ))
              ) : records.length > 0 ? (
                records.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell className="font-mono text-xs text-muted-foreground">
                      {user.id}
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center gap-2">
                        {user.userAvatar ? (
                          <img
                            alt={user.userName}
                            className="size-8 rounded-full object-cover"
                            src={user.userAvatar}
                          />
                        ) : (
                          <div className="flex size-8 items-center justify-center rounded-full bg-muted text-xs font-medium">
                            {(user.userName || '?').charAt(0).toUpperCase()}
                          </div>
                        )}
                        <div className="max-w-[200px] truncate font-medium">
                          {user.userName || '未命名用户'}
                        </div>
                      </div>
                    </TableCell>
                    <TableCell className="hidden text-muted-foreground sm:table-cell">
                      {user.userAccount || '-'}
                    </TableCell>
                    <TableCell>
                      <RoleBadge role={user.userRole} />
                    </TableCell>
                    <TableCell className="hidden text-muted-foreground lg:table-cell">
                      {formatDateTime(user.createTime)}
                    </TableCell>
                    <TableCell>
                      <div className="flex justify-end gap-1">
                        <Button
                          onClick={() => openEditDialog(user)}
                          size="icon-sm"
                          variant="ghost"
                        >
                          <Pencil />
                        </Button>
                        <Button
                          disabled={deletingId === user.id}
                          onClick={() => void handleDelete(user)}
                          size="icon-sm"
                          variant="destructive"
                        >
                          {deletingId === user.id ? (
                            <Loader2 className="animate-spin" />
                          ) : (
                            <Trash2 />
                          )}
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell
                    className="h-40 text-center text-sm text-muted-foreground"
                    colSpan={6}
                  >
                    暂无用户
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>

          <div className="flex flex-col gap-3 border-t p-3 sm:flex-row sm:items-center sm:justify-between">
            <div className="text-sm text-muted-foreground">
              共 {total} 条，第 {current} / {pages} 页
            </div>
            <Pagination className="sm:mx-0 sm:w-auto">
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious
                    aria-disabled={current <= 1}
                    className={
                      current <= 1 ? 'pointer-events-none opacity-50' : undefined
                    }
                    href="#"
                    onClick={(event) => {
                      event.preventDefault()
                      setCurrent((page) => Math.max(1, page - 1))
                    }}
                    text="上一页"
                  />
                </PaginationItem>
                <PaginationItem>
                  <PaginationNext
                    aria-disabled={current >= pages}
                    className={
                      current >= pages ? 'pointer-events-none opacity-50' : undefined
                    }
                    href="#"
                    onClick={(event) => {
                      event.preventDefault()
                      setCurrent((page) => Math.min(pages, page + 1))
                    }}
                    text="下一页"
                  />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          </div>
        </section>
      </div>

      {/* Edit Dialog */}
      <Dialog onOpenChange={setEditDialogOpen} open={editDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>编辑用户</DialogTitle>
            <DialogDescription>
              修改用户「{editingUser?.userName || editingUser?.id}」的基本信息
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-2">
            <div className="grid gap-2">
              <Label htmlFor="edit-name">用户名</Label>
              <Input
                id="edit-name"
                onChange={(e) =>
                  setEditForm((f) => ({ ...f, userName: e.target.value }))
                }
                placeholder="请输入用户名"
                value={editForm.userName || ''}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="edit-avatar">头像 URL</Label>
              <Input
                id="edit-avatar"
                onChange={(e) =>
                  setEditForm((f) => ({ ...f, userAvatar: e.target.value }))
                }
                placeholder="请输入头像链接"
                value={editForm.userAvatar || ''}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="edit-profile">个人简介</Label>
              <Input
                id="edit-profile"
                onChange={(e) =>
                  setEditForm((f) => ({ ...f, userProfile: e.target.value }))
                }
                placeholder="请输入个人简介"
                value={editForm.userProfile || ''}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="edit-role">角色</Label>
              <Select
                onValueChange={(value) =>
                  setEditForm((f) => ({ ...f, userRole: value }))
                }
                value={editForm.userRole || 'user'}
              >
                <SelectTrigger id="edit-role">
                  <SelectValue placeholder="选择角色" />
                </SelectTrigger>
                <SelectContent>
                  {ROLE_OPTIONS.map((opt) => (
                    <SelectItem key={opt.value} value={opt.value}>
                      {opt.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button disabled={saving} onClick={handleEditSubmit}>
              {saving && <Loader2 className="animate-spin" />}
              保存
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Add Dialog */}
      <Dialog onOpenChange={setAddDialogOpen} open={addDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>新增用户</DialogTitle>
            <DialogDescription>创建一个新的系统用户账号</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-2">
            <div className="grid gap-2">
              <Label htmlFor="add-account">用户账号</Label>
              <Input
                id="add-account"
                onChange={(e) =>
                  setAddForm((f) => ({ ...f, userAccount: e.target.value }))
                }
                placeholder="请输入登录账号"
                value={addForm.userAccount || ''}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="add-name">用户名</Label>
              <Input
                id="add-name"
                onChange={(e) =>
                  setAddForm((f) => ({ ...f, userName: e.target.value }))
                }
                placeholder="请输入显示名称"
                value={addForm.userName || ''}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="add-avatar">头像 URL</Label>
              <Input
                id="add-avatar"
                onChange={(e) =>
                  setAddForm((f) => ({ ...f, userAvatar: e.target.value }))
                }
                placeholder="请输入头像链接（可选）"
                value={addForm.userAvatar || ''}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="add-role">角色</Label>
              <Select
                onValueChange={(value) =>
                  setAddForm((f) => ({ ...f, userRole: value }))
                }
                value={addForm.userRole || 'user'}
              >
                <SelectTrigger id="add-role">
                  <SelectValue placeholder="选择角色" />
                </SelectTrigger>
                <SelectContent>
                  {ROLE_OPTIONS.map((opt) => (
                    <SelectItem key={opt.value} value={opt.value}>
                      {opt.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button disabled={adding} onClick={handleAddSubmit}>
              {adding && <Loader2 className="animate-spin" />}
              创建
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

function RoleBadge({ role }: { role?: string }) {
  const label = ROLE_MAP[role || ''] || role || '未知'

  if (role === 'admin') {
    return <Badge variant="default">{label}</Badge>
  }
  if (role === 'ban') {
    return <Badge variant="destructive">{label}</Badge>
  }
  return <Badge variant="secondary">{label}</Badge>
}

export default UserAdminListPage
