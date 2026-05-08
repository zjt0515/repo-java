import { Outlet, useRouterState } from '@tanstack/react-router'
import { ThemeProvider } from '@/components/user/ThemeProvider'
import { Toaster } from '@/components/ui/sonner'
import Footer from '@/layouts/Footer'
import Header from '@/layouts/Header'
import './App.css'

function App() {
  const pathname = useRouterState({
    select: (state) => state.location.pathname,
  })
  const isProblemWorkspace = /^\/problems\/[^/]+/.test(pathname)

  return (
    <ThemeProvider defaultTheme="system" storageKey="oj-ui-theme">
      <div className="flex min-h-svh flex-col bg-background text-foreground">
        <Header />
        <div className="flex-1">
          <Outlet />
        </div>
        {!isProblemWorkspace && <Footer />}
        <Toaster richColors />
      </div>
    </ThemeProvider>
  )
}

export default App
