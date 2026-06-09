import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ArrowDown, ArrowLeft, ArrowUp, CalendarClock, CheckCircle2, LoaderCircle, RefreshCw, Sparkles, XCircle } from 'lucide-react'
import api, { errorMessage } from '../api/client'
import Toast from '../components/Toast'

const meta = {
  SUBSCRIBED: { icon: CheckCircle2, color: 'bg-emerald-100 text-emerald-700' },
  UPGRADED: { icon: ArrowUp, color: 'bg-emerald-100 text-emerald-700' },
  DOWNGRADED: { icon: ArrowDown, color: 'bg-amber-100 text-amber-700' },
  CANCELLED: { icon: XCircle, color: 'bg-rose-100 text-rose-700' },
  EXPIRED: { icon: CalendarClock, color: 'bg-rose-100 text-rose-700' },
  TIER_CHANGED: { icon: Sparkles, color: 'bg-blue-100 text-blue-700' },
  RENEWED: { icon: RefreshCw, color: 'bg-blue-100 text-blue-700' },
}

export default function History() {
  const { userId } = useParams()
  const [membership, setMembership] = useState(null)
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [toast, setToast] = useState(null)

  useEffect(() => {
    api.get(`/subscriptions/user/${userId}`).then(async ({ data }) => {
      setMembership(data)
      const response = await api.get(`/subscriptions/${data.id}/history`)
      setEvents(response.data)
    }).catch((error) => setToast({ type: 'error', message: errorMessage(error) }))
      .finally(() => setLoading(false))
  }, [userId])

  if (loading) return <div className="grid min-h-[70vh] place-items-center"><LoaderCircle className="animate-spin text-club-600" /></div>

  return (
    <main className="shell max-w-4xl py-10 sm:py-14">
      <Link to={`/dashboard/${userId}`} className="mb-8 inline-flex items-center gap-2 text-sm font-semibold text-club-700"><ArrowLeft size={17} /> Back to dashboard</Link>
      <p className="text-sm font-bold uppercase tracking-widest text-club-600">Membership #{membership?.id}</p>
      <h1 className="mt-2 font-['Playfair_Display'] text-4xl font-bold">Event history</h1>
      <p className="mt-3 text-black/55">An append-only record of every membership state transition.</p>

      <section className="card mt-9 p-6 sm:p-8">
        {events.length === 0 && <p className="py-10 text-center text-black/50">No membership events yet.</p>}
        <div>
          {events.map((event, index) => {
            const item = meta[event.eventType] || meta.TIER_CHANGED
            const Icon = item.icon
            return (
              <article key={event.id} className="relative flex gap-5 pb-9 last:pb-0">
                {index < events.length - 1 && <div className="absolute left-5 top-11 h-[calc(100%-2rem)] w-px bg-black/10" />}
                <span className={`relative z-10 grid h-10 w-10 shrink-0 place-items-center rounded-full ${item.color}`}><Icon size={18} /></span>
                <div className="min-w-0 flex-1 pt-1">
                  <div className="flex flex-wrap items-start justify-between gap-2">
                    <h2 className="font-bold capitalize">{event.eventType.replaceAll('_', ' ').toLowerCase()}</h2>
                    <time className="text-xs text-black/40">{new Date(event.occurredAt).toLocaleString()}</time>
                  </div>
                  <p className="mt-1 text-sm text-black/55">{event.remarks}</p>
                  {(event.fromTierId || event.toTierId) && (
                    <p className="mt-2 text-xs font-semibold text-club-700">Tier {event.fromTierId || 'new'} → {event.toTierId || 'none'}</p>
                  )}
                  {(event.fromPlanId || event.toPlanId) && (
                    <p className="mt-1 text-xs font-semibold text-club-700">Plan {event.fromPlanId || 'new'} → {event.toPlanId || 'none'}</p>
                  )}
                </div>
              </article>
            )
          })}
        </div>
      </section>
      <Toast toast={toast} onClose={() => setToast(null)} />
    </main>
  )
}
