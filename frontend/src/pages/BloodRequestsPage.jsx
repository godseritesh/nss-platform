import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { bloodApi } from '../api';
import { BLOOD_GROUPS, URGENCY_META, formatDate, truncate } from '../utils/constants';

// Fix default Leaflet marker icons (webpack/vite asset issue)
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl:       'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl:     'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

const URGENCY_ICONS = {
  CRITICAL: L.divIcon({ className: '', html: `<div style="width:14px;height:14px;border-radius:50%;background:#dc2626;border:2px solid #fff;box-shadow:0 0 8px rgba(220,38,38,0.8)"></div>`, iconSize: [14,14], iconAnchor: [7,7] }),
  URGENT:   L.divIcon({ className: '', html: `<div style="width:12px;height:12px;border-radius:50%;background:#ea580c;border:2px solid #fff;box-shadow:0 0 6px rgba(234,88,12,0.7)"></div>`, iconSize: [12,12], iconAnchor: [6,6] }),
  STANDARD: L.divIcon({ className: '', html: `<div style="width:10px;height:10px;border-radius:50%;background:#3b82f6;border:2px solid #fff"></div>`, iconSize: [10,10], iconAnchor: [5,5] }),
};

function daysClass(d) {
  if (d <= 1) return 'critical';
  if (d <= 3) return 'urgent';
  return 'standard';
}

function RequestCard({ r }) {
  const nav = useNavigate();
  const u = URGENCY_META[r.urgency] ?? URGENCY_META.STANDARD;
  const dc = daysClass(r.daysLeft);
  return (
    <div className="card req-card card-clickable" onClick={() => nav(`/blood-requests/${r.id}`)}>
      <div className={`urgency-bar ${r.urgency}`} />
      <div className="req-card-top">
        <div className="req-card-header">
          <div className="blood-pill">{r.bloodGroup}</div>
          <div style={{ flex: 1, marginLeft: '0.75rem' }}>
            <div className="req-card-title">{r.patientName}</div>
            <div style={{ marginTop: 4 }}>
              <span className={`badge badge-${r.urgency.toLowerCase()}`}>{u.label}</span>
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div className={`days-left ${dc}`} style={{ fontSize: '1.1rem' }}>{r.daysLeft}d</div>
            <div className="text-tiny text-mute">left</div>
          </div>
        </div>
        <div className="req-card-meta">
          <span>🏥 {r.hospital}</span>
          <span>📍 {r.city}, {r.district}</span>
          <span>🩸 {r.unitsNeeded} unit{r.unitsNeeded > 1 ? 's' : ''}</span>
        </div>
        <p className="req-card-desc">{truncate(r.description, 110)}</p>
      </div>
      <div className="req-card-footer">
        <span className="donor-count">👥 {r.donorCount} donor{r.donorCount !== 1 ? 's' : ''} responded</span>
        <span style={{ fontSize: '0.78rem', color: 'var(--text-2)' }}>Deadline: {formatDate(r.deadline)}</span>
      </div>
    </div>
  );
}

export default function BloodRequestsPage() {
  const nav = useNavigate();
  const [requests, setRequests]   = useState([]);
  const [mapData, setMapData]     = useState([]);
  const [loading, setLoading]     = useState(true);
  const [page, setPage]           = useState(0);
  const [totalPages, setTotal]    = useState(1);
  const [bgFilter, setBgFilter]   = useState('');
  const [view, setView]           = useState('list'); // 'list' | 'map'

  useEffect(() => {
    setLoading(true);
    const params = { page, size: 12 };
    if (bgFilter) params.bloodGroup = bgFilter;
    bloodApi.list(params)
      .then(({ data }) => { setRequests(data.content); setTotal(data.totalPages); })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [page, bgFilter]);

  useEffect(() => {
    bloodApi.forMap().then(({ data }) => setMapData(data)).catch(() => {});
  }, []);

  const critical = requests.filter(r => r.urgency === 'CRITICAL');
  const others   = requests.filter(r => r.urgency !== 'CRITICAL');

  return (
    <div className="page-wrap">
      <div className="container section-sm">

        {/* Header */}
        <div className="flex-between" style={{ flexWrap: 'wrap', gap: '1rem', marginBottom: '2rem' }}>
          <div>
            <h1 style={{ fontSize: 'clamp(1.6rem, 3.5vw, 2.2rem)', fontWeight: 800, marginBottom: '0.3rem' }}>
              🩸 Blood Requests
            </h1>
            <p className="text-mute text-sm">Active requests across Maharashtra, sorted by urgency.</p>
          </div>
          <button className="btn btn-primary" onClick={() => nav('/blood-requests/submit')}>
            + Submit Request
          </button>
        </div>

        {/* Filters + View Toggle */}
        <div className="flex-between" style={{ flexWrap: 'wrap', gap: '0.75rem', marginBottom: '1.75rem' }}>
          <div className="flex gap-1" style={{ flexWrap: 'wrap' }}>
            <button className={`btn btn-sm ${!bgFilter ? 'btn-primary' : 'btn-outline'}`} onClick={() => { setBgFilter(''); setPage(0); }}>All Groups</button>
            {BLOOD_GROUPS.map(bg => (
              <button key={bg.value} className={`btn btn-sm ${bgFilter === bg.value ? 'btn-primary' : 'btn-outline'}`}
                onClick={() => { setBgFilter(bg.value); setPage(0); }}>
                {bg.label}
              </button>
            ))}
          </div>
          <div className="flex gap-1">
            <button className={`btn btn-sm ${view === 'list' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setView('list')}>☰ List</button>
            <button className={`btn btn-sm ${view === 'map'  ? 'btn-primary' : 'btn-outline'}`} onClick={() => setView('map')}>🗺 Map</button>
          </div>
        </div>

        {/* Map View */}
        {view === 'map' && (
          <div className="map-container" style={{ marginBottom: '2rem' }}>
            <MapContainer center={[19.0, 76.0]} zoom={7} style={{ height: '100%', width: '100%' }}>
              <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              />
              {mapData.filter(r => r.latitude && r.longitude).map(r => (
                <Marker key={r.id} position={[r.latitude, r.longitude]} icon={URGENCY_ICONS[r.urgency] ?? URGENCY_ICONS.STANDARD}>
                  <Popup>
                    <div style={{ minWidth: 180 }}>
                      <strong>{r.bloodGroup}</strong> — {r.patientName}<br />
                      🏥 {r.hospital}<br />
                      📍 {r.city}<br />
                      ⚡ {URGENCY_META[r.urgency]?.label} · {r.daysLeft}d left<br />
                      <a href={`/blood-requests/${r.id}`} style={{ color: '#dc2626', fontWeight: 600 }}>View details →</a>
                    </div>
                  </Popup>
                </Marker>
              ))}
            </MapContainer>
          </div>
        )}

        {loading && <div className="spinner" />}

        {/* Critical banner */}
        {!loading && critical.length > 0 && (
          <div style={{ marginBottom: '2rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
              <span style={{ fontSize: '0.75rem', fontWeight: 700, color: '#f87171', textTransform: 'uppercase', letterSpacing: '0.08em' }}>
                🚨 Critical — Immediate donors needed
              </span>
            </div>
            <div className="grid-auto">
              {critical.map(r => <RequestCard key={r.id} r={r} />)}
            </div>
          </div>
        )}

        {/* Other requests */}
        {!loading && others.length > 0 && (
          <div className="grid-auto">
            {others.map(r => <RequestCard key={r.id} r={r} />)}
          </div>
        )}

        {!loading && requests.length === 0 && (
          <div className="empty"><div className="empty-icon">🩸</div><p>No open blood requests match your filter.</p></div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex-center gap-1" style={{ marginTop: '2rem', flexWrap: 'wrap' }}>
            {Array.from({ length: totalPages }, (_, i) => (
              <button key={i} onClick={() => setPage(i)}
                className={`btn btn-sm ${page === i ? 'btn-primary' : 'btn-outline'}`}>
                {i + 1}
              </button>
            ))}
          </div>
        )}

      </div>
    </div>
  );
}
