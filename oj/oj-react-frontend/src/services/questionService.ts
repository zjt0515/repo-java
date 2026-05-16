import { ApiError } from '../../generated/core/ApiError'
import { OpenAPI } from '../../generated/core/OpenAPI'
import { request as __request } from '../../generated/core/request'
import type { BaseResponse_Page_Question_ } from '../../generated/models/BaseResponse_Page_Question_'
import type { BaseResponse_Page_QuestionSubmitVO_ } from '../../generated/models/BaseResponse_Page_QuestionSubmitVO_'
import type { BaseResponse_Page_QuestionVO_ } from '../../generated/models/BaseResponse_Page_QuestionVO_'
import type { BaseResponse_Question_ } from '../../generated/models/BaseResponse_Question_'
import type { BaseResponse_QuestionVO_ } from '../../generated/models/BaseResponse_QuestionVO_'
import type { BaseResponse_boolean_ } from '../../generated/models/BaseResponse_boolean_'
import type { DeleteRequest } from '../../generated/models/DeleteRequest'
import type { Question as GeneratedQuestion } from '../../generated/models/Question'
import type { QuestionAddRequest } from '../../generated/models/QuestionAddRequest'
import type { QuestionQueryRequest } from '../../generated/models/QuestionQueryRequest'
import type { QuestionSubmitAddRequest as GeneratedQuestionSubmitAddRequest } from '../../generated/models/QuestionSubmitAddRequest'
import type { QuestionSubmitQueryRequest as GeneratedQuestionSubmitQueryRequest } from '../../generated/models/QuestionSubmitQueryRequest'
import type { QuestionSubmitVO as GeneratedQuestionSubmitVO } from '../../generated/models/QuestionSubmitVO'
import type { QuestionUpdateRequest } from '../../generated/models/QuestionUpdateRequest'
import type { QuestionVO as GeneratedQuestionVO } from '../../generated/models/QuestionVO'
import type { Page_Question_ as GeneratedPageQuestion } from '../../generated/models/Page_Question_'
import type { Page_QuestionVO_ as GeneratedPageQuestionVO } from '../../generated/models/Page_QuestionVO_'
import type { Page_QuestionSubmitVO_ as GeneratedPageQuestionSubmitVO } from '../../generated/models/Page_QuestionSubmitVO_'
import { QuestionControllerService } from '../../generated/services/QuestionControllerService'

export type QuestionId = string
export type LongId = string
export type LongResponseData = string | number
export type Question = Omit<GeneratedQuestion, 'id' | 'userId'> & {
  id?: LongId
  userId?: LongId
}
export type QuestionVO = Omit<GeneratedQuestionVO, 'id' | 'userId'> & {
  id?: LongId
  userId?: LongId
}
export type QuestionSubmitVO = Omit<
  GeneratedQuestionSubmitVO,
  'id' | 'questionVO'
> & {
  id?: LongId
  questionId?: LongId
  questionVO?: QuestionVO
}
type PageQuestion = Omit<GeneratedPageQuestion, 'records'> & {
  records?: Question[]
}
type PageQuestionVO = Omit<GeneratedPageQuestionVO, 'records'> & {
  records?: QuestionVO[]
}
type PageQuestionSubmitVO = Omit<GeneratedPageQuestionSubmitVO, 'records'> & {
  records?: QuestionSubmitVO[]
}
export type QuestionUpdatePayload = Omit<QuestionUpdateRequest, 'id'> & {
  id?: QuestionId
}
export type DeleteQuestionRequest = Omit<DeleteRequest, 'id'> & {
  id?: QuestionId
}
export type QuestionSubmitAddRequest = Omit<
  GeneratedQuestionSubmitAddRequest,
  'questionId'
> & {
  questionId?: QuestionId
}
export type QuestionSubmitQueryRequest = Omit<
  GeneratedQuestionSubmitQueryRequest,
  'questionId'
> & {
  questionId?: string
}

type ApiResponse<T> = {
  code?: number
  data?: T
  message?: string
}

const SUCCESS_CODE = 0

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

export function getQuestionRequestErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    return getApiErrorMessage(error)
  }

  if (error instanceof Error) {
    return error.message
  }

  return '请求失败，请稍后再试'
}

export async function listQuestions(payload: QuestionQueryRequest) {
  const response = (await QuestionControllerService.listQuestionByPageUsingPost(
    payload,
  )) as BaseResponse_Page_Question_

  return unwrapResponse<PageQuestion>(
    response as unknown as ApiResponse<PageQuestion>,
    '获取题目列表失败',
  )
}

export async function listQuestionVOs(payload: QuestionQueryRequest) {
  const response = (await QuestionControllerService.listQuestionVoByPageUsingPost(
    payload,
  )) as BaseResponse_Page_QuestionVO_

  return unwrapResponse<PageQuestionVO>(
    response as unknown as ApiResponse<PageQuestionVO>,
    '获取题目列表失败',
  )
}

export async function getQuestion(id: QuestionId) {
  const getQuestionById = QuestionControllerService.getQuestionByIdUsingGet as (
    id?: QuestionId,
  ) => Promise<BaseResponse_Question_>
  const response = await getQuestionById(id)

  return unwrapResponse<Question>(
    response as unknown as ApiResponse<Question>,
    '获取题目详情失败',
  )
}

export async function getQuestionVO(id: QuestionId) {
  const response =
    (await QuestionControllerService.getQuestionVoByIdUsingGet(
      id,
    )) as BaseResponse_QuestionVO_

  return unwrapResponse<QuestionVO>(
    response as unknown as ApiResponse<QuestionVO>,
    '获取题目VO详情失败',
  )
}

export async function addQuestion(payload: QuestionAddRequest) {
  const response = (await QuestionControllerService.addQuestionUsingPost(
    payload,
  )) as ApiResponse<LongResponseData>

  return unwrapResponse<LongResponseData>(response, '新增题目失败')
}

export async function updateQuestion(payload: QuestionUpdatePayload) {
  const response = (await QuestionControllerService.updateQuestionUsingPost(
    payload as QuestionUpdateRequest,
  )) as BaseResponse_boolean_

  return unwrapResponse<boolean>(response, '更新题目失败')
}

export async function deleteQuestion(payload: DeleteQuestionRequest) {
  const response = (await QuestionControllerService.deleteQuestionUsingPost(
    payload as DeleteRequest,
  )) as BaseResponse_boolean_

  return unwrapResponse<boolean>(response, '删除题目失败')
}

export async function submitQuestion(payload: QuestionSubmitAddRequest) {
  const response = (await __request(OpenAPI, {
    method: 'POST',
    url: '/api/question/question_submit',
    body: payload as unknown as GeneratedQuestionSubmitAddRequest,
    errors: {
      401: 'Unauthorized',
      403: 'Forbidden',
      404: 'Not Found',
    },
  })) as ApiResponse<LongResponseData>

  return unwrapResponse<LongResponseData>(response, '提交代码失败')
}

export async function listQuestionSubmissions(
  payload: QuestionSubmitQueryRequest,
) {
  const response =
    (await QuestionControllerService.listQuestionSubmitByPageUsingPost(
      payload as unknown as GeneratedQuestionSubmitQueryRequest,
    )) as BaseResponse_Page_QuestionSubmitVO_

  return unwrapResponse<PageQuestionSubmitVO>(
    response as unknown as ApiResponse<PageQuestionSubmitVO>,
    '获取提交记录失败',
  )
}

export type {
  QuestionAddRequest,
  QuestionQueryRequest,
  QuestionUpdateRequest,
}
