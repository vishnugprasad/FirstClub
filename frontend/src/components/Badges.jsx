import { Check, Gift, Headphones, Percent, Sparkles, Timer, Truck } from 'lucide-react'

const tierStyles = {
  SILVER: 'bg-slate-100 text-slate-700 ring-slate-200',
  GOLD: 'bg-amber-100 text-amber-800 ring-amber-200',
  PLATINUM: 'bg-violet-100 text-violet-800 ring-violet-200',
}

const statusStyles = {
  ACTIVE: 'bg-emerald-100 text-emerald-700',
  CANCELLED: 'bg-rose-100 text-rose-700',
  EXPIRED: 'bg-zinc-200 text-zinc-700',
  PENDING: 'bg-amber-100 text-amber-700',
}

const icons = {
  FREE_DELIVERY: Truck,
  EXTRA_DISCOUNT: Percent,
  EXCLUSIVE_DEALS: Gift,
  EARLY_SALE_ACCESS: Timer,
  PRIORITY_SUPPORT: Headphones,
}

export function TierBadge({ name, large = false }) {
  return <span className={`inline-flex rounded-full px-3 py-1 font-bold ring-1 ${large ? 'text-sm' : 'text-xs'} ${tierStyles[name] || tierStyles.SILVER}`}>{name}</span>
}

export function StatusBadge({ status }) {
  return <span className={`rounded-full px-3 py-1 text-xs font-bold ${statusStyles[status] || statusStyles.PENDING}`}>{status}</span>
}

export function BenefitBadge({ benefit, compact = false }) {
  const Icon = icons[benefit.benefitType] || Sparkles
  const label = benefit.discountPercent
    ? `${benefit.discountPercent}% extra discount`
    : benefit.description || benefit.benefitType.replaceAll('_', ' ').toLowerCase()
  return (
    <div className={`flex items-center gap-3 ${compact ? 'text-sm' : ''}`}>
      <span className="grid h-9 w-9 shrink-0 place-items-center rounded-xl bg-club-50 text-club-600">
        <Icon size={17} />
      </span>
      <span className="font-medium capitalize">{label}</span>
      {!compact && <Check className="ml-auto text-club-500" size={17} />}
    </div>
  )
}
