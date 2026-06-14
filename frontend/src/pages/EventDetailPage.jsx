import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { eventsApi, pollsApi, votesApi } from '../api';
import { useAuth } from '../context/AuthContext';
import { CATEGORY_META, POLL_STATUS, formatDate, formatDatetime } from '../utils/constants';

function PollWidget({ poll }) {
  const { user } = useAuth();
  const [selected, setSelected]   = useState(null);
  const [results, setResults]     = useState(null);
  const [hasVoted, setHasVoted]   = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError]           = useState(null);

  const statusMeta = POLL_STATUS[poll.status] ?? POLL_STATUS.ACTIVE;

  useEffect(() => {
    if (hasVoted || !poll.acceptingVotes) {
      votesApi.results(poll.id).then(({ data }) => setResults(data));
    }
  }, [hasVoted, poll.acceptingVotes, poll.id]);

  const handleVote = async () => {
    if (!selected || !user) return;
    setSubmitting(true);
    setError(null);
    try {
      await votesApi.vote(poll.id, selected);
      setHasVoted(true);
    } catch (e) {
      setError(e.response?.data?.message ?? 'Failed to submit vote.');
    } finally {
      setSubmitting(false);
    }
  };

  const showResults = hasVoted || !poll.acceptingVotes;

  return (
    <div className="card poll-card">
      <div className="flex" style={{ justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
        <span className={`badge ${statusMeta.badgeClass}`}>{statusMeta.label}</span>
        {poll.expiresAt && (
          <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
            Expires: {formatDatetime(poll.expiresAt)}
          </span>
        )}
      </div>
      <p className="poll-question">{poll.question}</p>

      {error && <div className="alert alert-error" style={{ marginBottom: '1rem' }}>{error}</div>}

      {showResults && results ? (
        <div className="poll-options">
          {results.results.map(r => (
            <div key={r.optionId} className="poll-option voted">
              <div className="poll-option-bar" style={{ width: `${r.percentage}%` }} />
              <div className="poll-option-content">
                <span>{r.optionText}</span>
                <span className="poll-pct">{r.percentage}%</span>
              </div>
            </div>
          ))}
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
            {results.totalVotes} total votes
          </p>
        </div>
      ) : (
        <>
          <div className="poll-options">
            {poll.options.map(opt => (
              <div
                key={opt.id}
                className={`poll-option ${selected === opt.id ? 'selected' : ''}`}
                onClick={() => user && setSelected(opt.id)}
                role="radio"
                aria-checked={selected === opt.id}
                tabIndex={0}
                onKeyDown={e => e.key === 'Enter' && setSelected(opt.id)}
              >
                <div className="poll-option-content">
                  <span>{opt.optionText}</span>
                  {selected === opt.id && <span style={{ color: 'var(--red-primary)' }}>✓</span>}
                </div>
              </div>
            ))}
          </div>
          {!user ? (
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '1rem' }}>
              <a href="/login" style={{ color: 'var(--red-primary)' }}>Login</a> to cast your vote.
            </p>
          ) : (
            <button
              className="btn btn-primary mt-3"
              onClick={handleVote}
              disabled={!selected || submitting}
            >
              {submitting ? 'Submitting…' : '🗳️ Submit Vote'}
            </button>
          )}
        </>
      )}

      {hasVoted && (
        <div className="alert alert-success mt-2">✅ Your vote has been recorded!</div>
      )}
    </div>
  );
}

export default function EventDetailPage() {
  const { id }    = useParams();
  const navigate  = useNavigate();

  const [event, setEvent]   = useState(null);
  const [polls, setPolls]   = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);

  useEffect(() => {
    let cancelled = false;
    Promise.all([eventsApi.getOne(id), pollsApi.byEvent(id)])
      .then(([evRes, poRes]) => { if (!cancelled) { setEvent(evRes.data); setPolls(poRes.data); } })
      .catch(() => { if (!cancelled) setError('Failed to load event.'); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [id]);

  if (loading) return <div className="page-wrapper flex-center"><div className="spinner" /></div>;
  if (error)   return <div className="page-wrapper container section"><div className="alert alert-error">{error}</div></div>;

  const meta = CATEGORY_META[event.category] ?? CATEGORY_META.CAMPUS_EVENT;

  return (
    <div className="page-wrapper">
      <div className="container section">
        <button className="btn btn-ghost btn-sm mb-2" onClick={() => navigate('/events')}>
          ← Back to Events
        </button>

        {/* Event header */}
        <div className="card" style={{ marginBottom: '2rem' }}>
          <div className="flex gap-1" style={{ marginBottom: '0.75rem', flexWrap: 'wrap', alignItems: 'center' }}>
            <span className={`badge ${meta.badgeClass}`}>{meta.icon} {meta.label}</span>
          </div>
          <h1 style={{ fontSize: 'clamp(1.4rem, 3vw, 2rem)', marginBottom: '0.75rem' }}>{event.title}</h1>
          <div className="event-card-meta">
            {event.eventDate && <span>📅 {formatDate(event.eventDate)}</span>}
            {event.location   && <span>📍 {event.location}</span>}
            <span>👤 {event.createdByName}</span>
          </div>
          {event.description && (
            <p style={{ color: 'var(--text-secondary)', lineHeight: 1.7, marginTop: '1rem' }}>
              {event.description}
            </p>
          )}
        </div>

        {/* Polls */}
        <h2 style={{ marginBottom: '1.25rem' }}>📊 Polls</h2>
        {polls.length === 0 ? (
          <div className="empty-state">
            <div className="icon">📊</div>
            <p>No polls yet for this event.</p>
          </div>
        ) : (
          polls.map(poll => <PollWidget key={poll.id} poll={poll} />)
        )}
      </div>
    </div>
  );
}
