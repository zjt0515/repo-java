import { ApiError } from '../../generated/core/ApiError'
import type { BaseResponse_LoginUserVO_ } from '../../generated/models/BaseResponse_LoginUserVO_'
import type { BaseResponse_boolean_ } from '../../generated/models/BaseResponse_boolean_'
import type { BaseResponse_long_ } from '../../generated/models/BaseResponse_long_'
import type { LoginUserVO } from '../../generated/models/LoginUserVO'
import type { UserLoginRequest } from '../../generated/models/UserLoginRequest'
import type { UserRegisterRequest } from '../../generated/models/UserRegisterRequest'
import type { UserUpdateMyRequest } from '../../generated/models/UserUpdateMyRequest'
import { UserControllerService } from '../../generated/services/UserControllerService'

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

export function getUserRequestErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    return getApiErrorMessage(error)
  }

  if (error instanceof Error) {
    return error.message
  }

  return '请求失败，请稍后再试'
}

export async function loginUser(payload: UserLoginRequest) {
  const response = (await UserControllerService.userLoginUsingPost(
    payload,
  )) as BaseResponse_LoginUserVO_

  return unwrapResponse<LoginUserVO>(response, '登录失败')
}

export async function registerUser(payload: UserRegisterRequest) {
  const response = (await UserControllerService.userRegisterUsingPost(
    payload,
  )) as BaseResponse_long_

  return unwrapResponse<number>(response, '注册失败')
}

export async function getLoginUser() {
  const response =
    (await UserControllerService.getLoginUserUsingGet()) as BaseResponse_LoginUserVO_

  return unwrapResponse<LoginUserVO>(response, '获取登录用户失败')
}

export async function logoutUser() {
  const response =
    (await UserControllerService.userLogoutUsingPost()) as BaseResponse_boolean_

  return unwrapResponse<boolean>(response, '退出登录失败')
}

export async function updateMyUser(payload: UserUpdateMyRequest) {
  const response = (await UserControllerService.updateMyUserUsingPost(
    payload,
  )) as BaseResponse_boolean_

  return unwrapResponse<boolean>(response, '更新个人信息失败')
}

export type {
  LoginUserVO,
  UserLoginRequest,
  UserRegisterRequest,
  UserUpdateMyRequest,
}
