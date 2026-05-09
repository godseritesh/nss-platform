import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { bloodApi } from '../api';
import { BLOOD_GROUPS, URGENCY_META } from '../utils/constants';

const DISTRICTS = [
  'Ahmednagar','Akola','Amravati','Aurangabad','Beed','Bhandara','Buldhana',
  'Chandrapur','Dhule','Gadchiroli','Gondia','Hingoli','Jalgaon','Jalna',
  'Kolhapur','Latur','Mumbai','Mumbai Suburban','Nagpur','Nanded','Nandurbar',
  'Nashik','Osmanabad','Palghar','Parbhani','Pune','Raigad','Ratnagiri',
  'Sangli','Satara','Sindhudurg','Solapur','Thane','Wardha','Washim','Yavatmal'
];

const INITIAL = {
  patientName: '', bloodGroup: '', unitsNeeded: 1, hospital: '',
  city: '', district: '', contactName: '', contactPhone: '',
  contactEmail: '', description: '', urgency: 'STANDARD', deadline: '',
  latitude: '', longitude: ''
};

export default function SubmitRequestPage() {
  const nav = useNavigate();
  const [form, setForm]     = useState(INITIAL);
  const [error, setError]   = useState(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));
  const onChange = e => set(e.target.name, e.target.value);

  const handleSubmit = async e => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const payload = {
        ...form,
        unitsNeeded: Number(form.unitsNeeded),
        latitude:    form.latitude  ? Number(form.latitude)  : null,
        longitude:   form.longitude ? Number(form.longitude) : null,
      };
      await bloodApi.submit(payload);
      setSuccess(true);
      setTimeout(() => nav('/blood-requests'), 2500);
    } catch (err) {
      setError(err.response?.data?.message ?? 'Submission failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (success) return (
    <div className="page-wrap flex-center" style={{ flexDirection: 'column', gap: '1rem', minHeight: 'calc(100vh - 68px)', textAlign: 'center', padding: '2rem' }}>
      <div style={{ fontSize: '3rem' }}>✅</div>
      <h2>Request Submitted</h2>
      <p className="text-mute">Your blood request is now live. Donors will be able to contact you directly.</p>
      <p className="text-mute text-sm">Redirecting to requests list…</p>
    </div>
  );

  return (
    <div className="page-wrap">
      <div className="container" style={{ maxWidth: 720, padding: '3rem 1.5rem' }}>
        <button className="btn btn-ghost btn-sm mb-2" onClick={() => nav('/blood-requests')}>← Back</button>
        <h1 style={{ fontSize: '1.8rem', fontWeight: 800, marginBottom: '0.4rem' }}>Submit Blood Request</h1>
        <p className="text-mute text-sm" style={{ marginBottom: '2rem' }}>
          Fill in accurate details. Donors will use this information to contact you directly.
        </p>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          {/* Patient Info */}
          <div className="card" style={{ marginBottom: '1.25rem' }}>
            <h3 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '1.1rem', color: 'var(--text-2)', textTransform: 'uppercase', letterSpacing: '0.06em' }}>Patient Details</h3>
            <div className="grid-2">
              <div className="form-group">
                <label className="label">Patient Name *</label>
                <input name="patientName" className="input" value={form.patientName} onChange={onChange} required placeholder="Full name" />
              </div>
              <div className="form-group">
                <label className="label">Blood Group *</label>
                <select name="bloodGroup" className="input" value={form.bloodGroup} onChange={onChange} required>
                  <option value="">— Select —</option>
                  {BLOOD_GROUPS.map(bg => <option key={bg.value} value={bg.value}>{bg.label}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="label">Units Needed *</label>
                <input name="unitsNeeded" type="number" min={1} max={20} className="input" value={form.unitsNeeded} onChange={onChange} required />
              </div>
              <div className="form-group">
                <label className="label">Urgency *</label>
                <select name="urgency" className="input" value={form.urgency} onChange={onChange} required>
                  {Object.entries(URGENCY_META).map(([k, v]) => <option key={k} value={k}>{v.label}</option>)}
                </select>
              </div>
            </div>
            <div className="form-group">
              <label className="label">Description</label>
              <textarea name="description" className="input" value={form.description} onChange={onChange} rows={3} placeholder="Patient condition, surgery type, any important context…" />
            </div>
          </div>

          {/* Location */}
          <div className="card" style={{ marginBottom: '1.25rem' }}>
            <h3 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '1.1rem', color: 'var(--text-2)', textTransform: 'uppercase', letterSpacing: '0.06em' }}>Location</h3>
            <div className="form-group">
              <label className="label">Hospital / Blood Bank *</label>
              <input name="hospital" className="input" value={form.hospital} onChange={onChange} required placeholder="e.g. Sassoon General Hospital, Pune" />
            </div>
            <div className="grid-2">
              <div className="form-group">
                <label className="label">City *</label>
                <input name="city" className="input" value={form.city} onChange={onChange} required placeholder="City" />
              </div>
              <div className="form-group">
                <label className="label">District *</label>
                <select name="district" className="input" value={form.district} onChange={onChange} required>
                  <option value="">— Select District —</option>
                  {DISTRICTS.map(d => <option key={d} value={d}>{d}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="label">Latitude <span className="text-mute">(for map pin)</span></label>
                <input name="latitude" type="number" step="any" className="input" value={form.latitude} onChange={onChange} placeholder="e.g. 18.5204" />
              </div>
              <div className="form-group">
                <label className="label">Longitude <span className="text-mute">(for map pin)</span></label>
                <input name="longitude" type="number" step="any" className="input" value={form.longitude} onChange={onChange} placeholder="e.g. 73.8567" />
              </div>
            </div>
            <p className="text-tiny text-mute">Tip: Right-click on Google Maps → "What's here?" to get lat/lng.</p>
          </div>

          {/* Contact */}
          <div className="card" style={{ marginBottom: '1.5rem' }}>
            <h3 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '1.1rem', color: 'var(--text-2)', textTransform: 'uppercase', letterSpacing: '0.06em' }}>Contact Details</h3>
            <div className="grid-2">
              <div className="form-group">
                <label className="label">Contact Name *</label>
                <input name="contactName" className="input" value={form.contactName} onChange={onChange} required placeholder="Who to call" />
              </div>
              <div className="form-group">
                <label className="label">Mobile Number *</label>
                <input name="contactPhone" className="input" value={form.contactPhone} onChange={onChange} required placeholder="10-digit number" maxLength={10} />
              </div>
              <div className="form-group">
                <label className="label">Email <span className="text-mute">(optional)</span></label>
                <input name="contactEmail" type="email" className="input" value={form.contactEmail} onChange={onChange} placeholder="For donor communication" />
              </div>
              <div className="form-group">
                <label className="label">Deadline *</label>
                <input name="deadline" type="date" className="input" value={form.deadline} onChange={onChange} required min={new Date().toISOString().split('T')[0]} />
              </div>
            </div>
          </div>

          <button type="submit" className="btn btn-primary w-full" style={{ padding: '0.85rem' }} disabled={loading}>
            {loading ? 'Submitting…' : '🩸 Submit Blood Request'}
          </button>
        </form>
      </div>
    </div>
  );
}
