import { Link, Route, Routes } from 'react-router-dom'
import { Crown } from 'lucide-react'
import PlanSelector from './pages/PlanSelector'
import Dashboard from './pages/Dashboard'
import History from './pages/History'

function Header() {
  return (
    <header className="border-b border-black/5 bg-cream/90 backdrop-blur">
      <div className="shell flex h-20 items-center justify-between">
        <Link to="/" className="flex items-center gap-3">
          <span className="grid h-10 w-10 place-items-center rounded-2xl bg-club-900 text-white">
            <Crown size={21} />
          </span>
          <span>
            <strong className="block font-['Playfair_Display'] text-xl">FirstClub</strong>
            <span className="block text-[10px] font-bold uppercase tracking-[0.22em] text-club-600">Membership</span>
          </span>
        </Link>
        <Link to="/" className="text-sm font-semibold text-club-700">Explore plans</Link>
      </div>
    </header>
  )
}

export default function App() {
  return (
    <div className="min-h-screen">
      <Header />
      <Routes>
        <Route path="/" element={<PlanSelector />} />
        <Route path="/dashboard/:userId" element={<Dashboard />} />
        <Route path="/dashboard/:userId/history" element={<History />} />
      </Routes>
    </div>
  )
}
