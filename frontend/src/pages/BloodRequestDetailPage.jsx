import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { bloodApi } from '../api';
import { URGENCY_META, formatDate, formatDatetime } from '../utils/constants';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl:       'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl:     'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

function InterestForm({ requestId, onSuccess }) {
  const [form, setForm] = useState({ name: '', phone: '', email: '', message: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const onChange = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async e => {
    e.preventDefault(); setError(null); setLoading(true);
    try {
      await bloodApi.registerInterest(requestId, form);
      onSuccess();
    } catch (err) {
      setError(err.response?.data?.message ?? 'Failed to register. Please try again.');
    } finally { setLoading(false); }
  };

  return (
    <div className="card" style={{ marginTop: '1.5rem' }}>
      <h3 style={{ marginBottom: '1rem', fontSize: '1rem', fontWeight: 700 }}>Register as Donor</h3>
      <p className="text-mute text-sm" style={{ marginBottom: '1.1rem' }}>
        Your contact details will be shared with the requestor. Please be available on the provided number.
      </p>
      {error && <div className="alert alert-error">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="grid-2">
          <div className="form-group">
            <label className="label">Your Name *</label>
            <input name="name" className="input" value={form.name} onChange={onChange} required placeholder="Full name" />
          </div>
          <div className="form-group">
            <label className="label">Mobile *</label>
            <input name="phone" className="input" value={form.phone} onChange={onChange} required placeholder="10-digit number" maxLength={10} />
          </div>
        </div>
        <div className="form-group">
          <label className="label">Email <span className="text-mute">(optional)</span></label>
          <input name="email" type="email" className="input" value={form.email} onChange={onChange} placeholder="your@email.com" />
        </div>
        <div className="form-group">
          <label className="label">Message <span className="text-mute">(optional)</span></label>
          <textarea name="message" className="input" value={form.message} onChange={onChange} rows={2} placeholder="e.g. My blood type is confirmed A+. Can reach hospital by 6pm." />
        </div>
        <button type="submit" className="btn btn-primary w-full" disabled={loading}>
          {loading ? 'Registering…' : '🩸 I Can Donate — Register Interest'}
        </button>
      </form>
    </div>
  );
}

export default function BloodRequestDetailPage() {
  const { id } = useParams();
  const nav = useNavigate();
  const [req, setReq]         = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);
  const [donated, setDonated] = useState(false);

  useEffect(() => {
    bloodApi.getOne(id)
      .then(({ data }) => setReq(data))
      .catch(() => setError('Request not found or no longer available.'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="page-wrap flex-center"><div className="spinner" /></div>;
  if (error)   return <div className="page-wrap container" style={{ paddingTop: '5rem' }}><div className="alert alert-error">{error}</div></div>;

  const u = URGENCY_META[req.urgency] ?? URGENCY_META.STANDARD;
  const hasMap = req.latitude && req.longitude;
  const daysClass = req.urgency === 'CRITICAL' ? 'critical' : req.urgency === 'URGENT' ? 'urgent' : 'standard';

  return (
    <div className="page-wrap">
      <div className="container" style={{ padding: '2.5rem 1.5rem', maxWidth: 820 }}>
        <button className="btn btn-ghost btn-sm mb-2" onClick={() => nav('/blood-requests')}>← All Requests</button>

        {/* Status bar */}
        <div className={`urgency-bar ${req.urgency}`} style={{ marginBottom: '1.5rem', height: '4px', borderRadius: '2px' }} />

        {/* Header card */}
        <div className="card" style={{ marginBottom: '1.25rem' }}>
          <div className="flex-between" style={{ flexWrap: 'wrap', gap: '1rem', marginBottom: '1.25rem' }}>
            <div className="flex gap-2 items-center">
              <div className="blood-pill blood-pill-lg">{req.bloodGroup}</div>
              <div>
                <h1 style={{ fontSize: '1.4rem', fontWeight: 800, marginBottom: '4px' }}>{req.patientName}</h1>
                <span className={`badge badge-${req.urgency.toLowerCase()}`}>{u.label}</span>
              </div>
            </div>

          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '0.75rem', marginBottom: '1rem' }}>
            {[
              { icon: '🏥', label: 'Hospital',   val: req.hospital },
              { icon: '📍', label: 'Location',   val: `${req.city}, ${req.district}` },
              { icon: '🩸', label: 'Units',       val: `${req.unitsNeeded} unit${req.unitsNeeded > 1 ? 's' : ''} needed` },
              { icon: '📅', label: 'Posted',      val: formatDatetime(req.createdAt) },
            ].map(({ icon, label, val }) => (
              <div key={label} style={{ background: 'var(--bg-1)', borderRadius: 'var(--r-sm)', padding: '0.75rem 1rem', border: '1px solid var(--border)' }}>
                <div className="text-tiny text-mute" style={{ marginBottom: 2 }}>{icon} {label}</div>
                <div style={{ fontSize: '0.875rem', fontWeight: 500 }}>{val}</div>
              </div>
            ))}
          </div>

          {req.description && (
            <div style={{ background: 'var(--bg-1)', borderRadius: 'var(--r-sm)', padding: '1rem', border: '1px solid var(--border)', fontSize: '0.875rem', color: 'var(--text-2)', lineHeight: 1.7 }}>
              {req.description}
            </div>
          )}
        </div>

        {/* Contact card */}
        <div className="card" style={{ marginBottom: '1.25rem' }}>
          <h3 style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-2)', textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '1rem' }}>Contact the Family</h3>
          <div className="flex gap-2" style={{ flexWrap: 'wrap' }}>
            <a href={`tel:${req.contactPhone}`} className="btn btn-primary">📞 Call {req.contactName}</a>
            {req.contactEmail && <a href={`mailto:${req.contactEmail}`} className="btn btn-outline">✉ Email</a>}
          </div>
          <p className="text-mute text-tiny" style={{ marginTop: '0.75rem' }}>
            👥 {req.donorCount} person{req.donorCount !== 1 ? 's have' : ' has'} already expressed interest in donating.
          </p>
        </div>

        {/* Map */}
        {hasMap && (
          <div className="card" style={{ padding: 0, overflow: 'hidden', marginBottom: '1.25rem' }}>
            <div style={{ padding: '1rem 1.25rem 0.75rem', borderBottom: '1px solid var(--border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ fontSize: '0.9rem', fontWeight: 600 }}>📍 Hospital Location</h3>
              <a href={`https://www.google.com/maps/dir/?api=1&destination=${req.latitude},${req.longitude}`} target="_blank" rel="noreferrer" className="btn btn-sm btn-outline">
                🗺 Navigate (Google Maps)
              </a>
            </div>
            <div className="map-container" style={{ height: 300, borderRadius: 0, border: 'none' }}>
              <MapContainer center={[req.latitude, req.longitude]} zoom={14} style={{ height: '100%', width: '100%' }}>
                <TileLayer
                  attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                  url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                <Marker position={[req.latitude, req.longitude]}>
                  <Popup>{req.hospital}<br />{req.city}</Popup>
                </Marker>
              </MapContainer>
            </div>
          </div>
        )}

        {/* Donor interest form */}
        {req.status === 'OPEN' && (
          donated ? (
            <div className="alert alert-success">✅ Thank you! Your interest has been registered. The family will contact you soon.</div>
          ) : (
            <InterestForm requestId={id} onSuccess={() => setDonated(true)} />
          )
        )}

        {req.status === 'FULFILLED' && (
          <div className="alert alert-info">✅ This request has been fulfilled. Thank you to all who responded.</div>
        )}
      </div>
    </div>
  );
}
