import { useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { authApi } from '../api';

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token');

  const [password, setPassword] = useState('');
  const [confirm, setConfirm]   = useState('');
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState(null);
  const [done, setDone]         = useState(false);

  const handleSubmit = async e => {
    e.preventDefault();
    setError(null);
    if (password !== confirm) { setError('Passwords do not match.'); return; }
    if (password.length < 8) { setError('Password must be at least 8 characters.'); return; }
    setLoading(true);
    try {
      await authApi.resetPassword({ token, newPassword: password });
      setDone(true);
    } catch (err) {
      setError(err.response?.data?.message ?? 'Reset failed. The link may have expired.');
    } finally { setLoading(false); }
  };

  if (!token) {
    return (
      <div className="page-wrap flex-center" style={{ minHeight: '60vh', padding: '2rem' }}>
        <div className="card" style={{ maxWidth: 440, width: '100%', textAlign: 'center', padding: '2.5rem' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>⚠️</div>
          <h2 style={{ marginBottom: '0.75rem' }}>Invalid Link</h2>
          <p style={{ color: 'var(--text-2)', marginBottom: '1.5rem' }}>This password reset link is missing or invalid.</p>
          <Link to="/forgot-password" className="btn btn-primary">Request New Link</Link>
        </div>
      </div>
    );
  }

  if (done) {
    return (
      <div className="page-wrap flex-center" style={{ minHeight: '60vh', padding: '2rem' }}>
        <div className="card" style={{ maxWidth: 440, width: '100%', textAlign: 'center', padding: '2.5rem' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>✅</div>
          <h2 style={{ marginBottom: '0.75rem' }}>Password Reset!</h2>
          <p style={{ color: 'var(--text-2)', marginBottom: '1.5rem' }}>Your password has been changed successfully.</p>
          <button className="btn btn-primary" onClick={() => navigate('/login')}>Go to Login</button>
        </div>
      </div>
    );
  }

  return (
    <div className="page-wrap flex-center" style={{ minHeight: '60vh', padding: '2rem' }}>
      <div className="card" style={{ maxWidth: 440, width: '100%', padding: '2.5rem' }}>
        <h1 style={{ fontSize: '1.3rem', fontWeight: 800, marginBottom: '1.5rem' }}>🔐 Set New Password</h1>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">New Password</label>
            <input className="form-control" type="password" value={password} onChange={e => setPassword(e.target.value)} required minLength={8} placeholder="Min 8 characters" />
          </div>
          <div className="form-group">
            <label className="form-label">Confirm Password</label>
            <input className="form-control" type="password" value={confirm} onChange={e => setConfirm(e.target.value)} required placeholder="Re-enter password" />
          </div>
          <button type="submit" className="btn btn-primary w-full" disabled={loading}>
            {loading ? 'Resetting…' : '🔄 Reset Password'}
          </button>
        </form>
      </div>
    </div>
  );
}
