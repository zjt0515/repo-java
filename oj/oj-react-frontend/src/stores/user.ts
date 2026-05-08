import { create } from 'zustand'
import {
  getLoginUser,
  loginUser as requestLoginUser,
  logoutUser as requestLogoutUser,
  type LoginUserVO,
  type UserLoginRequest,
} from '@/services/userService'
import { persist , devtools} from 'zustand/middleware'

export type CurrentUserState = LoginUserVO | null | undefined

type AuthStatus = 'unchecked' | 'authenticated' | 'anonymous'
 
type UserStore = {
  currentUser: CurrentUserState
  status: AuthStatus
  clearCurrentUser: () => void
  ensureCurrentUser: () => Promise<LoginUserVO | null>
  login: (payload: UserLoginRequest) => Promise<LoginUserVO>
  logout: () => Promise<void>
  refreshCurrentUser: () => Promise<LoginUserVO | null>
  setCurrentUser: (user: LoginUserVO | null) => void
}

let pendingEnsureCurrentUser: Promise<LoginUserVO | null> | null = null

export const useUserStore = create<UserStore>()(
  devtools(
    persist(
      (set, get) => ({
        currentUser: undefined,
        status: 'unchecked',

        clearCurrentUser: () => {
          set({ currentUser: null, status: 'anonymous' })
        },

        ensureCurrentUser: async () => {
          const currentUser = get().currentUser

          if (currentUser !== undefined) {
            return currentUser
          }

          pendingEnsureCurrentUser ??= get()
            .refreshCurrentUser()
            .finally(() => {
              pendingEnsureCurrentUser = null
            })

          return pendingEnsureCurrentUser
        },

        login: async (payload) => {
          const user = await requestLoginUser(payload)
          set({ currentUser: user, status: 'authenticated' })
          return user
        },

        logout: async () => {
          await requestLogoutUser()
          set({ currentUser: null, status: 'anonymous' })
        },

        refreshCurrentUser: async () => {
          try {
            const user = await getLoginUser()
            set({ currentUser: user, status: 'authenticated' })
            return user
          } catch {
            set({ currentUser: null, status: 'anonymous' })
            return null
          }
        },

        setCurrentUser: (user) => {
          set({
            currentUser: user,
            status: user ? 'authenticated' : 'anonymous',
          })
        },
      }),
    { name: 'user-storage' }
  ))
)
