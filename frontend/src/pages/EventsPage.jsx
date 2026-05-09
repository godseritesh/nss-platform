import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { eventsApi } from '../api';
import { CATEGORY_META, formatDate, truncate } from '../utils/constants';

const CATEGORIES = ['ALL', 'BLOOD_DONATION', 'AWARENESS_DRIVE', 'VOLUNTEER_RECRUITMENT', 'CAMPUS_EVENT', 'FEEDBACK_POLL'];

function EventCard({ event }) {
  const navigate = useNavigate();
  const meta = CATEGORY_META[event.category] ?? CATEGORY_META.CAMPUS_EVENT;
  return (
    <article className="card event-card" key={event.id}>
      <div className="event-card-header">
        <span className={`badge ${meta.badgeClass}`}>{meta.icon} {meta.label}</span>
      </div>
      <h3>{event.title}</h3>
      <div className="event-card-meta">
        {event.eventDate && <span>📅 {formatDate(event.eventDate)}</span>}
        {event.location   && <span>📍 {event.location}</span>}
      </div>
      <p className="event-card-body">{truncate(event.description, 130)}</p>
      <div className="event-card-footer">
        <button className="btn btn-secondary btn-sm" onClick={() => navigate(`/events/${event.id}`)}>
          View & Vote →
        </button>
      </div>
    </article>
  );
}

export default function EventsPage() {
  const [events, setEvents]       = useState([]);
  const [loading, setLoading]     = useState(true);
  const [error, setError]         = useState(null);
  const [page, setPage]           = useState(0);
  const [totalPages, setTotal]    = useState(1);
  const [category, setCategory]   = useState('ALL');

  useEffect(() => {
    setLoading(true);
    eventsApi.list(page, 9)
      .then(({ data }) => {
        const items = category === 'ALL'
          ? data.content
          : data.content.filter(e => e.category === category);
        setEvents(items);
        setTotal(data.totalPages);
      })
      .catch(() => setError('Failed to load events.'))
      .finally(() => setLoading(false));
  }, [page, category]);

  return (
    <div className="page-wrapper">
      <div className="container section">
        <div className="section-header">
          <h1>Events</h1>
          <p>Browse all NSS VIIT Pune events. Vote on polls, register, and make an impact.</p>
        </div>

        {/* Category filter */}
        <div className="flex gap-1" style={{ flexWrap: 'wrap', justifyContent: 'center', marginBottom: '2rem' }}>
          {CATEGORIES.map(cat => {
            const meta = CATEGORY_META[cat];
            return (
              <button
                key={cat}
                className={`btn btn-sm ${category === cat ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => { setCategory(cat); setPage(0); }}
              >
                {meta ? `${meta.icon} ${meta.label}` : '🔍 All'}
              </button>
            );
          })}
        </div>

        {error   && <div className="alert alert-error">{error}</div>}
        {loading && <div className="spinner" role="status" aria-label="Loading events" />}

        {!loading && events.length === 0 && (
          <div className="empty-state">
            <div className="icon">📋</div>
            <p>No events found in this category.</p>
          </div>
        )}

        {!loading && events.length > 0 && (
          <div className="events-grid">
            {events.map(e => <EventCard key={e.id} event={e} />)}
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="pagination" role="navigation" aria-label="Page navigation">
            {Array.from({ length: totalPages }, (_, i) => (
              <button key={i} className={`page-btn ${page === i ? 'active' : ''}`} onClick={() => setPage(i)}>
                {i + 1}
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
