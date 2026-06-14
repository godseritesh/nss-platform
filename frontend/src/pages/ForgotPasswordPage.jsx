import { useState } from 'react';
import { Link } from 'react-router-dom';
import { authApi } from '../api';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [sent, setSent]   = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState(null);

  const handleSubmit = async e => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await authApi.forgotPassword({ email });
      setSent(true);
    } catch (err) {
      setError(err.response?.data?.message ?? 'Something went wrong.');
    } finally { setLoading(false); }
  };

  if (sent) {
    return (
      <div className="page-wrap flex-center" style={{ minHeight: '60vh', padding: '2rem' }}>
        <div className="card" style={{ maxWidth: 440, width: '100%', textAlign: 'center', padding: '2.5rem' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📧</div>
          <h2 style={{ marginBottom: '0.75rem' }}>Check your email</h2>
          <p style={{ color: 'var(--text-2)', marginBottom: '1.5rem' }}>
            If an account exists for <strong>{email}</strong>, we've sent a password reset link.
          </p>
          <Link to="/login" className="btn btn-primary">Back to Login</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="page-wrap flex-center" style={{ minHeight: '60vh', padding: '2rem' }}>
      <div className="card" style={{ maxWidth: 440, width: '100%', padding: '2.5rem' }}>
        <h1 style={{ fontSize: '1.3rem', fontWeight: 800, marginBottom: '0.5rem' }}>🔑 Forgot Password</h1>
        <p className="text-mute text-sm" style={{ marginBottom: '1.5rem' }}>Enter your email and we'll send you a reset link.</p>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email</label>
            <input className="form-control" type="email" value={email} onChange={e => setEmail(e.target.value)} required placeholder="your@email.com" />
          </div>
          <button type="submit" className="btn btn-primary w-full" disabled={loading}>
            {loading ? 'Sending…' : '📨 Send Reset Link'}
          </button>
        </form>
        <div style={{ marginTop: '1.25rem', textAlign: 'center' }}>
          <Link to="/login" className="text-sm" style={{ color: 'var(--accent-blue)' }}>← Back to Login</Link>
        </div>
      </div>
    </div>
  );
}
