import { useEffect, useState, useRef } from 'react';
import { analyticsApi } from '../api';

function useCountUp(target, dur = 1800) {
  const [v, setV] = useState(0);
  const ref = useRef(null);
  useEffect(() => {
    if (!target) return;
    const start = performance.now();
    const run = (now) => {
      const p = Math.min((now - start) / dur, 1);
      const e = 1 - Math.pow(1 - p, 3);
      setV(Math.round(e * target));
      if (p < 1) ref.current = requestAnimationFrame(run);
    };
    ref.current = requestAnimationFrame(run);
    return () => cancelAnimationFrame(ref.current);
  }, [target, dur]);
  return v;
}

const FALLBACK = { totalUsers: 3500, totalVotes: 8200, totalEvents: 18, bloodDonationVotes: 1200 };

const DEFS = [
  { key: 'totalUsers',        label: 'Students Reached',       suffix: '+', live: true  },
  { key: 'bloodDonationVotes',label: 'Blood Pledges',          suffix: '+', live: true  },
  { key: 'totalEvents',       label: 'Events Organised',       suffix: '',  live: false },
  { key: 'totalVotes',        label: 'Votes Cast',             suffix: '',  live: false },
];

function Tile({ value, label, suffix, live }) {
  const n = useCountUp(value);
  return (
    <div className="stat-tile">
      <div className="stat-num">
        {live && <span className="live-dot" />}
        {n.toLocaleString()}{suffix}
      </div>
      <div className="stat-lbl">{label}</div>
    </div>
  );
}

export default function LiveCounter() {
  const [stats, setStats] = useState(FALLBACK);
  const [live, setLive] = useState(false);
  useEffect(() => {
    analyticsApi.publicStats()
      .then(({ data }) => { setStats(data); setLive(true); })
      .catch(() => {});
  }, []);
  return (
    <section style={{ background: 'var(--bg-1)', borderTop: '1px solid var(--border)', borderBottom: '1px solid var(--border)' }}>
      <div className="container">
        <div className="grid-4" style={{ padding: '0.5rem 0' }}>
          {DEFS.map(d => (
            <Tile key={d.key} value={stats[d.key] ?? FALLBACK[d.key]} label={d.label} suffix={d.suffix} live={live && d.live} />
          ))}
        </div>
      </div>
    </section>
  );
}
