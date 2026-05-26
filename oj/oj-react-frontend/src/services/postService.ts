import { ApiError } from '../../generated/core/ApiError'
import { OpenAPI } from '../../generated/core/OpenAPI'
import { request as __request } from '../../generated/core/request'
import type { BaseResponse_Page_Post_ } from '../../generated/models/BaseResponse_Page_Post_'
import type { BaseResponse_Page_PostVO_ } from '../../generated/models/BaseResponse_Page_PostVO_'
import type { BaseResponse_PostVO_ } from '../../generated/models/BaseResponse_PostVO_'
import type { BaseResponse_boolean_ } from '../../generated/models/BaseResponse_boolean_'
import type { BaseResponse_long_ } from '../../generated/models/BaseResponse_long_'
import type { DeleteRequest } from '../../generated/models/DeleteRequest'
import type { Page_Post_ } from '../../generated/models/Page_Post_'
import type { Page_PostVO_ } from '../../generated/models/Page_PostVO_'
import type { Post } from '../../generated/models/Post'
import type { PostAddRequest as GeneratedPostAddRequest } from '../../generated/models/PostAddRequest'
import type { PostQueryRequest } from '../../generated/models/PostQueryRequest'
import type { PostUpdateRequest as GeneratedPostUpdateRequest } from '../../generated/models/PostUpdateRequest'
import type { PostVO } from '../../generated/models/PostVO'

export type PostAddRequest = Omit<GeneratedPostAddRequest, 'questionId'> & {
  questionId?: number | string
}

export type PostQueryPayload = Omit<
  PostQueryRequest,
  'id' | 'questionId' | 'userId'
> & {
  id?: number | string
  questionId?: number | string
  userId?: number | string
}

export type PostUpdateRequest = Omit<
  GeneratedPostUpdateRequest,
  'id' | 'questionId'
> & {
  id?: number | string
  questionId?: number | string
}

export type LongResponseData = number | string

type ApiResponse<T> = {
  code?: number
  data?: T
  message?: string
}

const SUCCESS_CODE = 0
const POST_REQUEST_ERRORS = {
  401: 'Unauthorized',
  403: 'Forbidden',
  404: 'Not Found',
}

function unwrapResponse<T>(response: ApiResponse<T>, fallbackMessage: string): T {
  if (response.code !== SUCCESS_CODE) {
    throw new Error(response.message || fallbackMessage)
  }

  return response.data as T
}

function getApiErrorMessage(error: ApiError) {
  const body = error.body as { message?: string } | undefined

  return body?.message || error.message || '请求失败，请稍后再试'
}

export function getPostRequestErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    return getApiErrorMessage(error)
  }

  if (error instanceof Error) {
    return error.message
  }

  return '请求失败，请稍后再试'
}

async function postRequest<T>(url: string, body: unknown) {
  return (await __request(OpenAPI, {
    method: 'POST',
    url,
    body,
    errors: POST_REQUEST_ERRORS,
  })) as T
}

async function getRequest<T>(url: string, query: Record<string, unknown>) {
  return (await __request(OpenAPI, {
    method: 'GET',
    url,
    query,
    errors: POST_REQUEST_ERRORS,
  })) as T
}

export async function listPosts(payload: PostQueryPayload) {
  const response = await postRequest<BaseResponse_Page_Post_>(
    '/api/post/list/page',
    payload,
  )

  return unwrapResponse<Page_Post_>(response, '获取帖子列表失败')
}

export async function listPostVOs(payload: PostQueryPayload) {
  const response = await postRequest<BaseResponse_Page_PostVO_>(
    '/api/post/list/page/vo',
    payload,
  )

  return unwrapResponse<Page_PostVO_>(response, '获取题解列表失败')
}

export async function getPostVO(id: string | number) {
  const response = await getRequest<BaseResponse_PostVO_>('/api/post/get/vo', {
    id,
  })

  return unwrapResponse<PostVO>(response, '获取题解详情失败')
}

export async function addPost(payload: PostAddRequest) {
  const response = await postRequest<BaseResponse_long_>('/api/post/add', payload)

  return unwrapResponse<LongResponseData>(
    response as unknown as ApiResponse<LongResponseData>,
    '新增帖子失败',
  )
}

export async function updatePost(payload: PostUpdateRequest) {
  const response = await postRequest<BaseResponse_boolean_>(
    '/api/post/update',
    payload,
  )

  return unwrapResponse<boolean>(response, '更新帖子失败')
}

export async function deletePost(payload: DeleteRequest) {
  const response = await postRequest<BaseResponse_boolean_>(
    '/api/post/delete',
    payload,
  )

  return unwrapResponse<boolean>(response, '删除帖子失败')
}

export type {
  DeleteRequest,
  Page_Post_,
  Page_PostVO_,
  Post,
  PostQueryRequest,
  PostVO,
}
