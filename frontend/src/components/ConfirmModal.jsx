import { X } from 'lucide-react'

export default function ConfirmModal({ open, title, children, confirmLabel = 'Confirm', danger = false, onConfirm, onClose, busy }) {
  if (!open) return null
  return (
    <div className="fixed inset-0 z-40 grid place-items-center bg-ink/50 p-4 backdrop-blur-sm" onMouseDown={onClose}>
      <div className="w-full max-w-md rounded-3xl bg-white p-6 shadow-2xl" onMouseDown={(event) => event.stopPropagation()}>
        <div className="mb-5 flex items-center justify-between">
          <h2 className="font-['Playfair_Display'] text-2xl font-bold">{title}</h2>
          <button onClick={onClose} className="rounded-full p-2 hover:bg-black/5" aria-label="Close"><X size={20} /></button>
        </div>
        <div>{children}</div>
        <div className="mt-6 flex justify-end gap-3">
          <button className="btn-secondary" onClick={onClose}>Not now</button>
          <button disabled={busy} className={danger ? 'rounded-full bg-rose-600 px-6 py-3 font-semibold text-white disabled:opacity-50' : 'btn-primary'} onClick={onConfirm}>
            {busy ? 'Working...' : confirmLabel}
          </button>
        </div>
      </div>
    </div>
  )
}
