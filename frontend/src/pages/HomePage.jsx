import { useNavigate } from 'react-router-dom';
import LiveCounter from '../components/LiveCounter';

const IMPACT_ITEMS = [
  { icon: '🩸', value: '25%', label: 'Increase in Blood Donations' },
  { icon: '👥', value: '3500+', label: 'Students Engaged' },
  { icon: '⚡', value: '100%', label: 'Open Source' },
];

const AWARENESS_ITEMS = [
  { icon: '🩸', title: 'Who Can Donate?', desc: 'Anyone 18–65 years, weighing ≥45 kg, and in good health. One donation can save up to 3 lives.' },
  { icon: '⏱️', title: 'Donation Takes', desc: 'The entire process takes about 45 minutes. The actual blood draw is only 10 minutes.' },
  { icon: '🔄', title: 'Recovery', desc: 'Your body replenishes plasma within 24 hours and red blood cells within 56 days.' },
  { icon: '🏥', title: 'Who Needs It?', desc: 'Accident victims, cancer patients, surgery patients, and thalassemia patients need regular supply.' },
];

export default function HomePage() {
  const navigate = useNavigate();

  return (
    <div className="page-wrapper">
      {/* ── Hero ── */}
      <section className="hero">
        <span className="blood-drop-float" style={{ top: '10%', right: '8%' }} aria-hidden>🩸</span>
        <span className="blood-drop-float" style={{ bottom: '20%', left: '6%', animationDelay: '3s' }} aria-hidden>❤️</span>

        <div className="hero-eyebrow">
          <span>🩸</span> NSS VIIT Pune &nbsp;·&nbsp; 2023–24
        </div>
        <h1>Vote. Participate.<br />Save Lives.</h1>
        <p className="hero-subtitle">
          The official event polling &amp; blood donation awareness platform of
          NSS VIIT Pune — empowering 3500+ students to take action.
        </p>
        <div className="hero-actions">
          <button className="btn btn-primary btn-lg" onClick={() => navigate('/events')}>
            🗓️ Browse Events
          </button>
          <button className="btn btn-secondary btn-lg" onClick={() => navigate('/register')}>
            Join NSS
          </button>
        </div>
        <div className="hero-scroll-hint" aria-hidden>
          <span>Scroll to explore</span>
          <span>↓</span>
        </div>
      </section>

      {/* ── Live Counters ── */}
      <LiveCounter />

      {/* ── Impact Numbers ── */}
      <section className="section">
        <div className="container">
          <div className="section-header">
            <h2>Our Impact</h2>
            <p>Real numbers from NSS VIIT Pune's 2023–24 campaign, built and deployed in 48 hours.</p>
          </div>
          <div className="admin-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))' }}>
            {IMPACT_ITEMS.map(({ icon, value, label }) => (
              <div key={label} className="admin-stat-card">
                <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>{icon}</div>
                <div className="value">{value}</div>
                <div className="label">{label}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── Blood Donation Awareness ── */}
      <section className="section" style={{ background: 'var(--bg-surface)', borderTop: '1px solid var(--border)', borderBottom: '1px solid var(--border)' }}>
        <div className="container">
          <div className="section-header">
            <h2>🩸 Blood Donation Awareness</h2>
            <p>Every donation matters. Here's what you need to know.</p>
          </div>
          <div className="events-grid">
            {AWARENESS_ITEMS.map(({ icon, title, desc }) => (
              <div key={title} className="card">
                <div style={{ fontSize: '2rem', marginBottom: '0.75rem' }}>{icon}</div>
                <h3 style={{ marginBottom: '0.5rem', fontSize: '1rem' }}>{title}</h3>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA ── */}
      <section className="section" style={{ textAlign: 'center' }}>
        <div className="container">
          <h2 style={{ fontSize: 'clamp(1.6rem, 4vw, 2.2rem)', marginBottom: '0.75rem' }}>
            Ready to make a difference?
          </h2>
          <p className="text-sec" style={{ marginBottom: '2rem' }}>
            Register now, vote on events, and help NSS VIIT Pune reach the next milestone.
          </p>
          <button className="btn btn-primary btn-lg" onClick={() => navigate('/register')}>
            🙋 Register as Volunteer
          </button>
        </div>
      </section>
    </div>
  );
}
