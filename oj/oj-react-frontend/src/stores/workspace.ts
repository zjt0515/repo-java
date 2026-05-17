import { create } from 'zustand'
import { persist } from 'zustand/middleware'

type WorkspaceStore = {
  language: string
  setLanguage: (language: string) => void
}

export const useWorkspaceStore = create<WorkspaceStore>()(
  persist(
    (set) => ({
      language: 'java',

      setLanguage: (language) => {
        set({ language })
      },
    }),
    { name: 'workspace-storage' }
  )
)
