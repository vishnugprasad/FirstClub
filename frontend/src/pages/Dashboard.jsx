import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ArrowRight, CalendarDays, History as HistoryIcon, LoaderCircle, PackageCheck, ShoppingBag, TrendingUp } from 'lucide-react'
import api, { errorMessage } from '../api/client'
import { BenefitBadge, StatusBadge, TierBadge } from '../components/Badges'
import ConfirmModal from '../components/ConfirmModal'
import Toast from '../components/Toast'

export default function Dashboard() {
  const { userId } = useParams()
  const [membership, setMembership] = useState(null)
  const [tiers, setTiers] = useState([])
  const [orderValue, setOrderValue] = useState('')
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [cancelOpen, setCancelOpen] = useState(false)
  const [toast, setToast] = useState(null)
  const navigate = useNavigate()

  const load = useCallback(async (quiet = false) => {
    try {
      const [{ data: current }, { data: tierData }] = await Promise.all([
        api.get(`/subscriptions/user/${userId}`),
        api.get('/tiers'),
      ])
      setMembership(current)
      setTiers(tierData)
    } catch (error) {
      if (!quiet) setToast({ type: 'error', message: errorMessage(error) })
    } finally {
      if (!quiet) setLoading(false)
    }
  }, [userId])

  useEffect(() => {
    load()
    const timer = setInterval(() => load(true), 10000)
    return () => clearInterval(timer)
  }, [load])

  const dayStats = useMemo(() => {
    if (!membership) return { remaining: 0, percent: 0 }
    const start = new Date(`${membership.startDate}T00:00:00`)
    const end = new Date(`${membership.expiryDate}T00:00:00`)
    const now = new Date()
    const total = Math.max(1, end - start)
    const remaining = Math.max(0, Math.ceil((end - now) / 86400000))
    return { remaining, percent: Math.max(0, Math.min(100, ((now - start) / total) * 100)) }
  }, [membership])

  const recordOrder = async () => {
    if (Number(orderValue) <= 0) {
      setToast({ type: 'error', message: 'Enter an order value greater than zero' })
      return
    }
    setBusy(true)
    try {
      await api.post(`/subscriptions/user/${userId}/record-order`, { orderValue: Number(orderValue) })
      setOrderValue('')
      setToast({ type: 'success', message: 'Order recorded! Your tier may be upgraded.' })
      setTimeout(() => load(true), 800)
    } catch (error) {
      setToast({ type: 'error', message: errorMessage(error) })
    } finally {
      setBusy(false)
    }
  }

  const changeTier = async (tier) => {
    if (tier.id === membership.tier.id) return
    const action = tier.rank > membership.tier.rank ? 'upgrade' : 'downgrade'
    setBusy(true)
    try {
      const { data } = await api.patch(`/subscriptions/${membership.id}/${action}`, { newTierId: tier.id })
      setMembership(data)
      setToast({ type: 'success', message: `Membership ${action}d to ${tier.name}` })
    } catch (error) {
      setToast({ type: 'error', message: errorMessage(error) })
    } finally {
      setBusy(false)
    }
  }

  const cancel = async () => {
    setBusy(true)
    try {
      await api.delete(`/subscriptions/${membership.id}`)
      setCancelOpen(false)
      navigate('/')
    } catch (error) {
      setToast({ type: 'error', message: errorMessage(error) })
      setBusy(false)
    }
  }

  if (loading) return <div className="grid min-h-[70vh] place-items-center"><LoaderCircle className="animate-spin text-club-600" /></div>
  if (!membership) return <main className="shell py-20 text-center"><h1 className="text-3xl font-bold">Membership not found</h1><Link to="/" className="btn-primary mt-6">Choose a plan</Link></main>

  return (
    <main className="shell py-10 sm:py-14">
      <div className="mb-8 flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="text-sm font-bold uppercase tracking-widest text-club-600">Member #{userId}</p>
          <h1 className="mt-2 font-['Playfair_Display'] text-4xl font-bold">Your membership</h1>
        </div>
        <Link to={`/dashboard/${userId}/history`} className="btn-secondary gap-2"><HistoryIcon size={17} /> Event history</Link>
      </div>

      <section className="card overflow-hidden">
        <div className="bg-club-900 p-7 text-white sm:p-9">
          <div className="flex flex-wrap items-start justify-between gap-5">
            <div>
              <div className="mb-5 flex items-center gap-3"><TierBadge name={membership.tier.name} large /><StatusBadge status={membership.status} /></div>
              <h2 className="font-['Playfair_Display'] text-4xl font-bold">{membership.plan.name} membership</h2>
              <p className="mt-2 text-white/60">Member since {new Date(`${membership.startDate}T00:00:00`).toLocaleDateString()}</p>
            </div>
            <div className="rounded-2xl bg-white/10 p-4 text-right">
              <p className="text-xs font-bold uppercase tracking-wider text-white/50">Renews / expires</p>
              <p className="mt-1 text-lg font-bold">{new Date(`${membership.expiryDate}T00:00:00`).toLocaleDateString()}</p>
            </div>
          </div>
          <div className="mt-8">
            <div className="mb-2 flex justify-between text-sm"><span>{dayStats.remaining} days remaining</span><span>{Math.round(dayStats.percent)}% elapsed</span></div>
            <div className="h-2 overflow-hidden rounded-full bg-white/15"><div className="h-full rounded-full bg-amber-300" style={{ width: `${dayStats.percent}%` }} /></div>
          </div>
        </div>
        <div className="grid gap-px bg-black/5 sm:grid-cols-3">
          <Stat icon={ShoppingBag} label="Orders recorded" value={membership.totalOrderCount} />
          <Stat icon={TrendingUp} label="Lifetime order value" value={`₹${membership.totalOrderValue}`} />
          <Stat icon={CalendarDays} label="Plan duration" value={`${membership.plan.durationMonths} mo`} />
        </div>
      </section>

      <div className="mt-8 grid gap-8 lg:grid-cols-[1.15fr_.85fr]">
        <section className="card p-7">
          <p className="text-sm font-bold uppercase tracking-widest text-club-600">Included today</p>
          <h2 className="mt-2 font-['Playfair_Display'] text-3xl font-bold">{membership.tier.name.toLowerCase()} benefits</h2>
          <div className="mt-7 grid gap-5 sm:grid-cols-2">
            {membership.tier.benefits.map((benefit) => <BenefitBadge key={benefit.id} benefit={benefit} />)}
          </div>
        </section>

        <section className="card bg-club-50 p-7">
          <span className="grid h-12 w-12 place-items-center rounded-2xl bg-club-900 text-white"><PackageCheck /></span>
          <h2 className="mt-5 font-['Playfair_Display'] text-3xl font-bold">Simulate an order</h2>
          <p className="mt-2 text-sm text-black/55">Record an order to test automatic tier evaluation.</p>
          <div className="mt-6 flex gap-3">
            <input type="number" min="0.01" step="0.01" value={orderValue} onChange={(e) => setOrderValue(e.target.value)} className="field" placeholder="Order value ₹" />
            <button disabled={busy} onClick={recordOrder} className="btn-primary px-5"><ArrowRight /></button>
          </div>
          <p className="mt-4 text-xs text-black/45">Gold at 5 orders or ₹2,000. Platinum at 15 orders or ₹8,000.</p>
        </section>
      </div>

      <section className="mt-8">
        <div className="mb-5">
          <p className="text-sm font-bold uppercase tracking-widest text-club-600">Membership level</p>
          <h2 className="mt-2 font-['Playfair_Display'] text-3xl font-bold">Explore other tiers</h2>
        </div>
        <div className="grid gap-5 md:grid-cols-3">
          {tiers.map((tier) => {
            const current = tier.id === membership.tier.id
            return (
              <button disabled={busy || current} key={tier.id} onClick={() => changeTier(tier)} className={`card p-6 text-left transition disabled:cursor-default ${current ? 'ring-2 ring-club-600' : 'hover:-translate-y-1'}`}>
                <div className="flex items-center justify-between"><TierBadge name={tier.name} /><span className="text-xs font-bold text-club-600">{current ? 'CURRENT' : tier.rank > membership.tier.rank ? 'UPGRADE' : 'DOWNGRADE'}</span></div>
                <p className="mt-5 text-sm text-black/50">{tier.benefits.length} active benefits</p>
                <p className="mt-1 font-semibold">{tier.minOrderCount ? `${tier.minOrderCount} orders or ₹${tier.minOrderValue}` : 'A strong place to start'}</p>
              </button>
            )
          })}
        </div>
      </section>

      <div className="mt-12 border-t border-black/10 pt-8 text-center">
        <button onClick={() => setCancelOpen(true)} className="text-sm font-semibold text-rose-600 hover:underline">Cancel membership</button>
      </div>

      <ConfirmModal open={cancelOpen} title="Cancel membership?" confirmLabel="Yes, cancel" danger onClose={() => setCancelOpen(false)} onConfirm={cancel} busy={busy}>
        <p className="text-black/60">This ends the active membership immediately and records the cancellation in its event history.</p>
      </ConfirmModal>
      <Toast toast={toast} onClose={() => setToast(null)} />
    </main>
  )
}

function Stat({ icon: Icon, label, value }) {
  return <div className="flex items-center gap-4 bg-white p-6"><span className="grid h-10 w-10 place-items-center rounded-xl bg-club-50 text-club-600"><Icon size={19} /></span><div><p className="text-xs font-bold uppercase tracking-wider text-black/40">{label}</p><p className="mt-1 text-lg font-bold">{value}</p></div></div>
}
