import { redirect } from '@tanstack/react-router'
import type { LoginUserVO } from '@/services/userService'
import { useUserStore } from '@/stores/user'

export const ACCESS_ENUM = {
  NOT_LOGIN: 'not_login',
  USER: 'user',
  ADMIN: 'admin',
} as const

export type AccessType = (typeof ACCESS_ENUM)[keyof typeof ACCESS_ENUM]

function normalizeRole(role?: string) {
  return role?.trim().toLowerCase()
}

export function isAdminUser(user?: LoginUserVO | null) {
  return normalizeRole(user?.userRole) === ACCESS_ENUM.ADMIN
}

/**
 * 判断是否有权限
 * @param user 
 * @param needAccess 
 * @returns {boolean}
 */
export function checkAccess(
  user?: LoginUserVO | null,
  needAccess: AccessType = ACCESS_ENUM.NOT_LOGIN,
) {
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true
  }

  if (!user) {
    return false
  }

  if (needAccess === ACCESS_ENUM.ADMIN) {
    return isAdminUser(user)
  }

  return true
}

export async function checkAdminAndRedirect() {
  const currentUser = await useUserStore.getState().refreshCurrentUser()

  if (!currentUser) {
    throw redirect({ to: '/user/login' })
  }

  if (!isAdminUser(currentUser)) {
    throw redirect({ to: '/403' })
  }
}

export async function checkLoginAndRedirect() {
  const currentUser = await useUserStore.getState().refreshCurrentUser()

  if (!currentUser) {
    throw redirect({ to: '/user/login' })
  }
}

export async function restoreLoginStatus() {
  await useUserStore.getState().refreshCurrentUser()
}
