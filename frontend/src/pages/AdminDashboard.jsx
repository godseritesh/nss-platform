import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { analyticsApi, eventsApi, pollsApi } from '../api';
import { useAuth } from '../context/AuthContext';
import { CATEGORY_META, formatDate } from '../utils/constants';

const CATEGORIES = ['BLOOD_DONATION', 'AWARENESS_DRIVE', 'VOLUNTEER_RECRUITMENT', 'CAMPUS_EVENT', 'FEEDBACK_POLL'];

// ── Admin Create Event Form ────────────────────────────
function EventForm({ onCreated }) {
  const [form, setForm] = useState({ title: '', description: '', eventDate: '', location: '', category: 'BLOOD_DONATION' });
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState(null);

  const handleChange = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async e => {
    e.preventDefault(); setMsg(null); setLoading(true);
    try {
      await eventsApi.create({ ...form, eventDate: form.eventDate || null });
      setMsg({ type: 'success', text: 'Event created!' });
      setForm({ title: '', description: '', eventDate: '', location: '', category: 'BLOOD_DONATION' });
      onCreated?.();
    } catch (err) {
      setMsg({ type: 'error', text: err.response?.data?.message ?? 'Failed to create event.' });
    } finally { setLoading(false); }
  };

  return (
    <div className="card" style={{ marginBottom: '2rem' }}>
      <h3 style={{ marginBottom: '1.25rem' }}>➕ Create New Event</h3>
      {msg && <div className={`alert alert-${msg.type}`}>{msg.text}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label" htmlFor="ev-title">Title *</label>
          <input id="ev-title" name="title" className="form-control" value={form.title} onChange={handleChange} required placeholder="Event title" />
        </div>
        <div className="form-group">
          <label className="form-label" htmlFor="ev-desc">Description</label>
          <textarea id="ev-desc" name="description" className="form-control" value={form.description} onChange={handleChange} rows={3} placeholder="Event details…" />
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label className="form-label" htmlFor="ev-date">Date</label>
            <input id="ev-date" name="eventDate" type="date" className="form-control" value={form.eventDate} onChange={handleChange} />
          </div>
          <div className="form-group">
            <label className="form-label" htmlFor="ev-loc">Location</label>
            <input id="ev-loc" name="location" className="form-control" value={form.location} onChange={handleChange} placeholder="VIIT Campus…" />
          </div>
        </div>
        <div className="form-group">
          <label className="form-label" htmlFor="ev-cat">Category *</label>
          <select id="ev-cat" name="category" className="form-control" value={form.category} onChange={handleChange}>
            {CATEGORIES.map(c => <option key={c} value={c}>{CATEGORY_META[c].icon} {CATEGORY_META[c].label}</option>)}
          </select>
        </div>
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Creating…' : '✅ Create Event'}
        </button>
      </form>
    </div>
  );
}

// ── Admin Create Poll Form ─────────────────────────────
function PollForm({ events }) {
  const [eventId, setEventId]     = useState('');
  const [question, setQuestion]   = useState('');
  const [options, setOptions]     = useState(['', '']);
  const [expiresAt, setExpiresAt] = useState('');
  const [loading, setLoading]     = useState(false);
  const [msg, setMsg]             = useState(null);

  const addOption    = () => setOptions(o => [...o, '']);
  const updateOption = (i, v) => setOptions(o => o.map((x, idx) => idx === i ? v : x));
  const removeOption = (i) => setOptions(o => o.filter((_, idx) => idx !== i));

  const handleSubmit = async e => {
    e.preventDefault(); setMsg(null);
    if (!eventId) { setMsg({ type: 'error', text: 'Select an event.' }); return; }
    setLoading(true);
    try {
      await pollsApi.create(eventId, {
        question, options: options.filter(o => o.trim()),
        expiresAt: expiresAt ? new Date(expiresAt).toISOString() : null,
      });
      setMsg({ type: 'success', text: 'Poll created!' });
      setQuestion(''); setOptions(['', '']); setExpiresAt('');
    } catch (err) {
      setMsg({ type: 'error', text: err.response?.data?.message ?? 'Failed to create poll.' });
    } finally { setLoading(false); }
  };

  return (
    <div className="card" style={{ marginBottom: '2rem' }}>
      <h3 style={{ marginBottom: '1.25rem' }}>📊 Create Poll for Event</h3>
      {msg && <div className={`alert alert-${msg.type}`}>{msg.text}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label" htmlFor="poll-event">Event *</label>
          <select id="poll-event" className="form-control" value={eventId} onChange={e => setEventId(e.target.value)} required>
            <option value="">— Select event —</option>
            {events.map(ev => <option key={ev.id} value={ev.id}>{ev.title}</option>)}
          </select>
        </div>
        <div className="form-group">
          <label className="form-label" htmlFor="poll-q">Question *</label>
          <input id="poll-q" className="form-control" value={question} onChange={e => setQuestion(e.target.value)} required placeholder="Poll question…" />
        </div>
        <div className="form-group">
          <label className="form-label" htmlFor="poll-opt-0">Options (min 2)</label>
          {options.map((opt, i) => (
            <div key={i} className="flex gap-1" style={{ marginBottom: '0.5rem' }}>
              <input className="form-control" value={opt} onChange={e => updateOption(i, e.target.value)} placeholder={`Option ${i + 1}`} />
              {options.length > 2 && (
                <button type="button" className="btn btn-danger btn-sm" onClick={() => removeOption(i)}>✕</button>
              )}
            </div>
          ))}
          <button type="button" className="btn btn-secondary btn-sm mt-1" onClick={addOption}>+ Add Option</button>
        </div>
        <div className="form-group">
          <label className="form-label" htmlFor="poll-exp">Expires At (optional)</label>
          <input id="poll-exp" type="datetime-local" className="form-control" value={expiresAt} onChange={e => setExpiresAt(e.target.value)} />
        </div>
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Creating…' : '✅ Create Poll'}
        </button>
      </form>
    </div>
  );
}

// ── Main Admin Dashboard ──────────────────────────────
export default function AdminDashboard() {
  const { isAdmin }   = useAuth();
  const navigate      = useNavigate();
  const [stats, setStats]   = useState(null);
  const [events, setEvents] = useState([]);
  const [refresh, setRefresh] = useState(0);

  useEffect(() => {
    if (!isAdmin) { navigate('/'); return; }
    analyticsApi.overview().then(({ data }) => setStats(data));
    eventsApi.list(0, 50).then(({ data }) => setEvents(data.content));
  }, [isAdmin, navigate, refresh]);

  if (!isAdmin) return null;

  const STAT_TILES = stats ? [
    { label: 'Total Users',        value: stats.totalUsers },
    { label: 'Events',             value: stats.totalEvents },
    { label: 'Active Polls',       value: stats.activePolls },
    { label: 'Total Votes',        value: stats.totalVotes },
    { label: 'Blood Donation Events', value: stats.bloodDonationEvents },
    { label: 'Blood Pledges',      value: stats.bloodDonationVotes },
  ] : [];

  return (
    <div className="page-wrapper">
      <div className="container section">
        <div className="section-header">
          <h1>🛡️ Admin Dashboard</h1>
          <p>NSS VIIT Pune platform management</p>
        </div>

        {/* Stats */}
        {stats && (
          <div className="admin-grid" style={{ marginBottom: '2.5rem' }}>
            {STAT_TILES.map(({ label, value }) => (
              <div key={label} className="admin-stat-card">
                <div className="value">{value?.toLocaleString()}</div>
                <div className="label">{label}</div>
              </div>
            ))}
          </div>
        )}

        {/* Blood Donation Impact Banner */}
        {stats && (
          <div className="card" style={{ marginBottom: '2rem', borderColor: 'var(--border-red)', background: 'rgba(229,62,62,0.05)' }}>
            <h3 style={{ color: 'var(--red-primary)', marginBottom: '0.5rem' }}>🩸 Blood Donation Impact</h3>
            <p style={{ color: 'var(--text-secondary)' }}>
              Estimated <strong style={{ color: 'var(--accent-green)' }}>+{stats.estimatedBloodDonationIncrease}%</strong> increase in blood donation participation
              across <strong>{stats.bloodDonationEvents}</strong> events with <strong>{stats.bloodDonationVotes?.toLocaleString()}</strong> pledges.
            </p>
          </div>
        )}

        {/* Create Event */}
        <EventForm onCreated={() => setRefresh(r => r + 1)} />

        {/* Create Poll */}
        <PollForm events={events} />

        {/* Recent Events List */}
        <h2 style={{ marginBottom: '1rem' }}>📋 Recent Events</h2>
        <div className="events-grid">
          {events.slice(0, 6).map(ev => {
            const meta = CATEGORY_META[ev.category] ?? CATEGORY_META.CAMPUS_EVENT;
            return (
              <div key={ev.id} className="card" style={{ cursor: 'pointer' }} onClick={() => navigate(`/events/${ev.id}`)}>
                <span className={`badge ${meta.badgeClass}`}>{meta.icon} {meta.label}</span>
                <h3 style={{ marginTop: '0.75rem', fontSize: '0.95rem' }}>{ev.title}</h3>
                <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '0.35rem' }}>
                  {formatDate(ev.eventDate)} {ev.location && `· ${ev.location}`}
                </p>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
