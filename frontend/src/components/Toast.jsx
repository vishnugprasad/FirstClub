import { useEffect } from 'react'
import { CheckCircle2, XCircle, X } from 'lucide-react'

export default function Toast({ toast, onClose }) {
  useEffect(() => {
    if (!toast) return
    const timer = setTimeout(onClose, 4000)
    return () => clearTimeout(timer)
  }, [toast, onClose])

  if (!toast) return null
  const success = toast.type === 'success'
  const Icon = success ? CheckCircle2 : XCircle
  return (
    <div className="fixed right-4 top-24 z-50 flex max-w-sm items-center gap-3 rounded-2xl bg-white p-4 shadow-soft ring-1 ring-black/5">
      <Icon className={success ? 'text-emerald-600' : 'text-rose-600'} />
      <p className="flex-1 text-sm font-semibold">{toast.message}</p>
      <button onClick={onClose} aria-label="Close notification"><X size={17} /></button>
    </div>
  )
}
