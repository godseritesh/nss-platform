import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { profileApi } from '../api';

export default function ProfilePage() {
  const { user, logout } = useAuth();
  const [name, setName]       = useState(user?.name ?? '');
  const [currentPassword, setCurrent] = useState('');
  const [newPassword, setNew]         = useState('');
  const [loading, setLoading]         = useState(false);
  const [msg, setMsg]                 = useState(null);

  const handleSubmit = async e => {
    e.preventDefault();
    setMsg(null);
    setLoading(true);
    try {
      const body = {};
      if (name.trim() !== user?.name) body.name = name;
      if (newPassword) { body.currentPassword = currentPassword; body.newPassword = newPassword; }
      if (Object.keys(body).length === 0) { setMsg({ type: 'info', text: 'No changes to save.' }); return; }
      await profileApi.update(body);
      setMsg({ type: 'success', text: 'Profile updated.' });
      setCurrent(''); setNew('');
    } catch (err) {
      setMsg({ type: 'error', text: err.response?.data?.message ?? 'Update failed.' });
    } finally { setLoading(false); }
  };

  return (
    <div className="page-wrap">
      <div className="container" style={{ maxWidth: 600, padding: '3rem 1.5rem' }}>
        <h1 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '0.5rem' }}>👤 My Profile</h1>
        <p className="text-mute text-sm" style={{ marginBottom: '2rem' }}>{user?.email}</p>

        {msg && <div className={`alert alert-${msg.type}`} style={{ marginBottom: '1rem' }}>{msg.text}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Name</label>
            <input className="form-control" value={name} onChange={e => setName(e.target.value)} required />
          </div>
          <hr style={{ margin: '1.5rem 0', borderColor: 'var(--border)' }} />
          <p className="text-sm" style={{ marginBottom: '1rem', color: 'var(--text-2)' }}>Change password (leave blank to keep current)</p>
          <div className="form-group">
            <label className="form-label">Current Password</label>
            <input className="form-control" type="password" value={currentPassword} onChange={e => setCurrent(e.target.value)} />
          </div>
          <div className="form-group">
            <label className="form-label">New Password</label>
            <input className="form-control" type="password" value={newPassword} onChange={e => setNew(e.target.value)} minLength={8} />
          </div>
          <button type="submit" className="btn btn-primary w-full" disabled={loading}>
            {loading ? 'Saving…' : '💾 Save Changes'}
          </button>
        </form>

        <hr style={{ margin: '2rem 0', borderColor: 'var(--border)' }} />
        <button className="btn btn-outline w-full" onClick={logout}>🚪 Logout</button>
      </div>
    </div>
  );
}
