import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowRight, Leaf, LoaderCircle } from 'lucide-react'
import api, { errorMessage } from '../api/client'
import { BenefitBadge, TierBadge } from '../components/Badges'
import ConfirmModal from '../components/ConfirmModal'
import Toast from '../components/Toast'

export default function PlanSelector() {
  const [plans, setPlans] = useState([])
  const [selectedPlan, setSelectedPlan] = useState(null)
  const [selectedTier, setSelectedTier] = useState(null)
  const [modal, setModal] = useState(false)
  const [userId, setUserId] = useState('')
  const [cohortTag, setCohortTag] = useState('')
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [toast, setToast] = useState(null)
  const navigate = useNavigate()

  useEffect(() => {
    api.get('/plans').then(({ data }) => {
      setPlans(data)
      setSelectedPlan(data[0])
      setSelectedTier(data[0]?.tiers?.[0])
    }).catch((error) => setToast({ type: 'error', message: errorMessage(error) }))
      .finally(() => setLoading(false))
  }, [])

  const chooseDuration = (plan, tierName) => {
    setSelectedPlan(plan)
    setSelectedTier(plan.tiers.find((tier) => tier.name === tierName) || plan.tiers[0])
  }

  const subscribe = async () => {
    if (!userId || Number(userId) <= 0) {
      setToast({ type: 'error', message: 'Enter a valid User ID' })
      return
    }
    setBusy(true)
    try {
      await api.post('/subscriptions', {
        userId: Number(userId),
        planId: selectedPlan.id,
        tierId: selectedTier.id,
        cohortTag: cohortTag || null,
      })
      navigate(`/dashboard/${userId}`)
    } catch (error) {
      setToast({ type: 'error', message: errorMessage(error) })
    } finally {
      setBusy(false)
    }
  }

  if (loading) return <div className="grid min-h-[70vh] place-items-center"><LoaderCircle className="animate-spin text-club-600" /></div>

  return (
    <main className="pb-32">
      <section className="shell py-14 text-center sm:py-20">
        <span className="mb-4 inline-flex items-center gap-2 rounded-full bg-club-100 px-4 py-2 text-xs font-bold uppercase tracking-widest text-club-700">
          <Leaf size={14} /> Membership, made rewarding
        </span>
        <h1 className="mx-auto max-w-3xl font-['Playfair_Display'] text-4xl font-bold leading-tight sm:text-6xl">More value in every FirstClub order.</h1>
        <p className="mx-auto mt-5 max-w-2xl text-base text-black/60 sm:text-lg">Choose your rhythm, pick your starting tier, and unlock better benefits as you shop.</p>
      </section>

      {selectedPlan && (
        <section className="shell">
          <div className="mb-6">
            <p className="text-sm font-bold uppercase tracking-widest text-club-600">Choose your tier</p>
            <h2 className="mt-2 font-['Playfair_Display'] text-3xl font-bold">Pick a tier and billing period</h2>
          </div>
          <div className="grid gap-5 lg:grid-cols-3">
            {plans[0].tiers.map((baseTier) => {
              const tier = selectedPlan.tiers.find((item) => item.name === baseTier.name)
              const active = selectedTier?.name === tier.name
              return (
                <article key={tier.name} className={`card p-6 text-left transition ${active ? 'ring-2 ring-club-600' : 'hover:border-club-500/30'}`}>
                  <div className="flex items-center justify-between">
                    <TierBadge name={tier.name} large />
                    <span className={`grid h-6 w-6 place-items-center rounded-full border-2 ${active ? 'border-club-600' : 'border-black/20'}`}>
                      {active && <span className="h-3 w-3 rounded-full bg-club-600" />}
                    </span>
                  </div>
                  <div className="mt-5 grid grid-cols-3 gap-2">
                    {plans.map((plan) => {
                      const durationTier = plan.tiers.find((item) => item.name === tier.name)
                      const selected = active && selectedPlan.id === plan.id
                      return (
                        <button
                          key={plan.id}
                          onClick={() => chooseDuration(plan, tier.name)}
                          className={`rounded-xl px-2 py-2.5 text-xs font-bold transition ${
                            selected
                              ? 'bg-club-900 text-white'
                              : 'bg-club-50 text-club-700 hover:bg-club-100'
                          }`}
                        >
                          {plan.name}
                        </button>
                      )
                    })}
                  </div>
                  <div className="mt-6 flex items-end gap-1 border-b border-black/5 pb-5">
                    <span className="mb-1 text-lg font-semibold">₹</span>
                    <strong className="font-['Playfair_Display'] text-4xl">{tier.price}</strong>
                    <span className="mb-1 text-xs text-black/45">for {selectedPlan.durationMonths} {selectedPlan.durationMonths === 1 ? 'month' : 'months'}</span>
                  </div>
                  <div className="mt-5 space-y-4">
                    {tier.benefits.map((benefit) => <BenefitBadge key={benefit.id} benefit={benefit} compact />)}
                  </div>
                </article>
              )
            })}
          </div>
        </section>
      )}

      <div className="fixed inset-x-0 bottom-0 z-30 border-t border-black/5 bg-white/90 p-4 backdrop-blur">
        <div className="shell flex items-center justify-between gap-4">
          <div className="hidden sm:block">
            <p className="text-xs font-bold uppercase tracking-wider text-black/40">Your selection</p>
            <p className="font-semibold">{selectedTier?.name} · {selectedPlan?.name} billing</p>
          </div>
          <button disabled={!selectedPlan || !selectedTier} onClick={() => setModal(true)} className="btn-primary ml-auto w-full gap-2 sm:w-auto">
            Subscribe <ArrowRight size={18} />
          </button>
        </div>
      </div>

      <ConfirmModal open={modal} title="Welcome to the club" confirmLabel="Start membership" onClose={() => setModal(false)} onConfirm={subscribe} busy={busy}>
        <p className="mb-5 text-sm text-black/60">Use a numeric demo user ID. You’ll land directly on that user’s membership dashboard.</p>
        <label className="mb-4 block">
          <span className="mb-2 block text-sm font-semibold">User ID</span>
          <input autoFocus type="number" min="1" value={userId} onChange={(e) => setUserId(e.target.value)} className="field" placeholder="e.g. 101" />
        </label>
        <label className="block">
          <span className="mb-2 block text-sm font-semibold">Cohort tag <span className="font-normal text-black/40">(optional)</span></span>
          <input value={cohortTag} onChange={(e) => setCohortTag(e.target.value)} className="field" placeholder="VIP or EMPLOYEE" />
        </label>
      </ConfirmModal>
      <Toast toast={toast} onClose={() => setToast(null)} />
    </main>
  )
}
