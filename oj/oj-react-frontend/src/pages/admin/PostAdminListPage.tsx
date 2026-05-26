import {
  FileText,
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
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Textarea } from '@/components/ui/textarea'
import {
  addPost,
  deletePost,
  getPostRequestErrorMessage,
  listPostVOs,
  updatePost,
  type PostAddRequest,
  type PostUpdateRequest,
  type PostVO,
} from '@/services/postService'
import {
  formatQuestionDateTime,
  parseQuestionTagInput,
  parseQuestionTags,
} from '@/lib/question-utils'

const PAGE_SIZE = 10

type PostFormState = {
  content: string
  questionId: string
  tags: string
  title: string
}

const EMPTY_POST_FORM: PostFormState = {
  content: '',
  questionId: '',
  tags: '',
  title: '',
}

function PostAdminListPage() {
  const [keyword, setKeyword] = useState('')
  const [submittedKeyword, setSubmittedKeyword] = useState('')
  const [current, setCurrent] = useState(1)
  const [records, setRecords] = useState<PostVO[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(false)
  const [deletingId, setDeletingId] = useState<number | null>(null)

  const [addDialogOpen, setAddDialogOpen] = useState(false)
  const [addForm, setAddForm] = useState<PostFormState>(EMPTY_POST_FORM)
  const [adding, setAdding] = useState(false)

  const [editDialogOpen, setEditDialogOpen] = useState(false)
  const [editingPost, setEditingPost] = useState<PostVO | null>(null)
  const [editForm, setEditForm] = useState<PostFormState>(EMPTY_POST_FORM)
  const [saving, setSaving] = useState(false)

  const pages = Math.max(1, Math.ceil(total / PAGE_SIZE))

  const fetchPosts = useCallback(async () => {
    setLoading(true)
    try {
      const page = await listPostVOs({
        current,
        pageSize: PAGE_SIZE,
        sortField: 'createTime',
        sortOrder: 'descend',
        title: submittedKeyword.trim() || undefined,
      })

      setRecords(page.records ?? [])
      setTotal(page.total ?? 0)
    } catch (error) {
      toast.error(getPostRequestErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [current, submittedKeyword])

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      void fetchPosts()
    }, 0)

    return () => window.clearTimeout(timeout)
  }, [fetchPosts])

  function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setCurrent(1)
    setSubmittedKeyword(keyword)
  }

  function openAddDialog() {
    setAddForm(EMPTY_POST_FORM)
    setAddDialogOpen(true)
  }

  function openEditDialog(post: PostVO) {
    setEditingPost(post)
    setEditForm({
      content: post.content || '',
      questionId: post.questionId ? String(post.questionId) : '',
      tags: parseQuestionTags(post.tags).join(', '),
      title: post.title || '',
    })
    setEditDialogOpen(true)
  }

  async function handleAddSubmit() {
    const payload = buildPostPayload(addForm)

    if (!payload) {
      return
    }

    setAdding(true)
    try {
      await addPost(payload)
      toast.success('帖子已创建')
      setAddDialogOpen(false)
      if (current === 1) {
        await fetchPosts()
      } else {
        setCurrent(1)
      }
    } catch (error) {
      toast.error(getPostRequestErrorMessage(error))
    } finally {
      setAdding(false)
    }
  }

  async function handleEditSubmit() {
    if (!editingPost?.id) {
      return
    }

    const payload = buildPostPayload(editForm)

    if (!payload) {
      return
    }

    setSaving(true)
    try {
      await updatePost({
        ...payload,
        id: editingPost.id,
      })
      toast.success('帖子已更新')
      setEditDialogOpen(false)
      await fetchPosts()
    } catch (error) {
      toast.error(getPostRequestErrorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(post: PostVO) {
    if (!post.id) {
      return
    }

    const confirmed = window.confirm(`确认删除帖子「${post.title || post.id}」？`)

    if (!confirmed) {
      return
    }

    setDeletingId(post.id)
    try {
      await deletePost({ id: post.id })
      toast.success('帖子已删除')
      if (records.length === 1 && current > 1) {
        setCurrent((page) => Math.max(1, page - 1))
      } else {
        await fetchPosts()
      }
    } catch (error) {
      toast.error(getPostRequestErrorMessage(error))
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <div className="p-4 sm:p-6">
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-4">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">帖子管理</h1>
            <p className="mt-1 text-sm text-muted-foreground">
              管理题解、讨论帖和关联题目的内容信息
            </p>
          </div>
          <Button onClick={openAddDialog}>
            <Plus />
            新增帖子
          </Button>
        </div>

        <section className="rounded-lg border bg-background">
          <div className="flex flex-col gap-3 border-b p-3 sm:flex-row sm:items-center sm:justify-between">
            <form className="flex w-full gap-2 sm:max-w-md" onSubmit={handleSearch}>
              <div className="relative flex-1">
                <Search className="pointer-events-none absolute left-2.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  className="pl-8"
                  onChange={(event) => setKeyword(event.target.value)}
                  placeholder="按帖子标题搜索"
                  value={keyword}
                />
              </div>
              <Button type="submit" variant="outline">
                搜索
              </Button>
            </form>
            <Button disabled={loading} onClick={fetchPosts} variant="ghost">
              {loading ? <Loader2 className="animate-spin" /> : <RefreshCw />}
              刷新
            </Button>
          </div>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-20">ID</TableHead>
                <TableHead>帖子</TableHead>
                <TableHead className="hidden md:table-cell">标签</TableHead>
                <TableHead className="hidden sm:table-cell">关联题目</TableHead>
                <TableHead className="hidden text-right sm:table-cell">点赞</TableHead>
                <TableHead className="hidden text-right lg:table-cell">收藏</TableHead>
                <TableHead className="hidden lg:table-cell">更新时间</TableHead>
                <TableHead className="w-32 text-right">操作</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {loading ? (
                <PostTableSkeleton />
              ) : records.length > 0 ? (
                records.map((post) => (
                  <TableRow key={post.id}>
                    <TableCell className="font-mono text-xs text-muted-foreground">
                      {post.id}
                    </TableCell>
                    <TableCell>
                      <div className="flex max-w-xl items-start gap-2">
                        <div className="mt-0.5 flex size-8 shrink-0 items-center justify-center rounded-md bg-muted text-muted-foreground">
                          <FileText className="size-4" />
                        </div>
                        <div className="min-w-0">
                          <div className="truncate font-medium">
                            {post.title || '未命名帖子'}
                          </div>
                          <div className="mt-1 line-clamp-1 text-xs text-muted-foreground">
                            {post.content || '暂无帖子内容'}
                          </div>
                        </div>
                      </div>
                    </TableCell>
                    <TableCell className="hidden md:table-cell">
                      <TagList value={post.tags} />
                    </TableCell>
                    <TableCell className="hidden sm:table-cell">
                      {post.questionId ? (
                        <Badge className="rounded-md" variant="outline">
                          #{post.questionId}
                        </Badge>
                      ) : (
                        <span className="text-sm text-muted-foreground">未关联</span>
                      )}
                    </TableCell>
                    <TableCell className="hidden text-right sm:table-cell">
                      {post.thumbNum ?? 0}
                    </TableCell>
                    <TableCell className="hidden text-right lg:table-cell">
                      {post.favourNum ?? 0}
                    </TableCell>
                    <TableCell className="hidden text-muted-foreground lg:table-cell">
                      {formatQuestionDateTime(post.updateTime)}
                    </TableCell>
                    <TableCell>
                      <div className="flex justify-end gap-1">
                        <Button
                          onClick={() => openEditDialog(post)}
                          size="icon-sm"
                          variant="ghost"
                        >
                          <Pencil />
                        </Button>
                        <Button
                          disabled={deletingId === post.id}
                          onClick={() => void handleDelete(post)}
                          size="icon-sm"
                          variant="destructive"
                        >
                          {deletingId === post.id ? (
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
                  <TableCell className="h-40 text-center text-sm text-muted-foreground" colSpan={8}>
                    暂无帖子
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
                    className={current <= 1 ? 'pointer-events-none opacity-50' : undefined}
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
                    className={current >= pages ? 'pointer-events-none opacity-50' : undefined}
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

      <PostDialog
        form={addForm}
        onFormChange={setAddForm}
        onOpenChange={setAddDialogOpen}
        onSubmit={handleAddSubmit}
        open={addDialogOpen}
        saving={adding}
        submitText="创建"
        title="新增帖子"
        description="创建一条新的题解或讨论帖子"
      />

      <PostDialog
        form={editForm}
        onFormChange={setEditForm}
        onOpenChange={setEditDialogOpen}
        onSubmit={handleEditSubmit}
        open={editDialogOpen}
        saving={saving}
        submitText="保存"
        title="编辑帖子"
        description={`修改帖子「${editingPost?.title || editingPost?.id || ''}」的内容信息`}
      />
    </div>
  )
}

function buildPostPayload(form: PostFormState): PostAddRequest | PostUpdateRequest | null {
  const title = form.title.trim()

  if (!title) {
    toast.error('帖子标题不能为空')
    return null
  }

  try {
    return {
      content: form.content.trim() || undefined,
      questionId: parseOptionalNumber(form.questionId, '关联题目 ID'),
      tags: parseQuestionTagInput(form.tags),
      title,
    }
  } catch (error) {
    toast.error(error instanceof Error ? error.message : '表单填写有误')
    return null
  }
}

function parseOptionalNumber(value: string, label: string) {
  const trimmed = value.trim()

  if (!trimmed) {
    return undefined
  }

  const parsed = Number(trimmed)

  if (!Number.isInteger(parsed) || parsed <= 0) {
    throw new Error(`${label} 必须是正整数`)
  }

  return parsed
}

function PostDialog({
  description,
  form,
  onFormChange,
  onOpenChange,
  onSubmit,
  open,
  saving,
  submitText,
  title,
}: {
  description: string
  form: PostFormState
  onFormChange: (form: PostFormState) => void
  onOpenChange: (open: boolean) => void
  onSubmit: () => void
  open: boolean
  saving: boolean
  submitText: string
  title: string
}) {
  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          <DialogDescription>{description}</DialogDescription>
        </DialogHeader>
        <div className="grid gap-4 py-2">
          <div className="grid gap-2">
            <Label htmlFor={`${title}-title`}>标题</Label>
            <Input
              id={`${title}-title`}
              onChange={(event) =>
                onFormChange({ ...form, title: event.target.value })
              }
              placeholder="请输入帖子标题"
              value={form.title}
            />
          </div>
          <div className="grid gap-2">
            <Label htmlFor={`${title}-content`}>内容</Label>
            <Textarea
              className="min-h-32"
              id={`${title}-content`}
              onChange={(event) =>
                onFormChange({ ...form, content: event.target.value })
              }
              placeholder="请输入帖子内容"
              value={form.content}
            />
          </div>
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="grid gap-2">
              <Label htmlFor={`${title}-question`}>关联题目 ID</Label>
              <Input
                id={`${title}-question`}
                inputMode="numeric"
                onChange={(event) =>
                  onFormChange({ ...form, questionId: event.target.value })
                }
                placeholder="可选"
                value={form.questionId}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor={`${title}-tags`}>标签</Label>
              <Input
                id={`${title}-tags`}
                onChange={(event) =>
                  onFormChange({ ...form, tags: event.target.value })
                }
                placeholder="用逗号或空格分隔"
                value={form.tags}
              />
            </div>
          </div>
        </div>
        <DialogFooter>
          <Button disabled={saving} onClick={onSubmit}>
            {saving && <Loader2 className="animate-spin" />}
            {submitText}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}

function PostTableSkeleton() {
  return Array.from({ length: PAGE_SIZE }).map((_, rowIndex) => (
    <TableRow key={rowIndex}>
      {Array.from({ length: 8 }).map((_, cellIndex) => (
        <TableCell key={cellIndex}>
          <div className="h-4 w-full animate-pulse rounded bg-muted" />
        </TableCell>
      ))}
    </TableRow>
  ))
}

function TagList({ value }: { value?: string | string[] }) {
  const tags = parseQuestionTags(value)

  if (tags.length === 0) {
    return <span className="text-sm text-muted-foreground">无标签</span>
  }

  return (
    <div className="flex max-w-xs flex-wrap gap-1">
      {tags.slice(0, 3).map((tag) => (
        <Badge className="rounded-md" key={tag} variant="secondary">
          {tag}
        </Badge>
      ))}
      {tags.length > 3 ? (
        <Badge className="rounded-md" variant="outline">
          +{tags.length - 3}
        </Badge>
      ) : null}
    </div>
  )
}

export default PostAdminListPage
