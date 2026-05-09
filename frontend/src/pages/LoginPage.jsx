import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async e => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await login(form);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message ?? 'Login failed. Check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-wrapper auth-container">
      <div className="card auth-card">
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <img src="/National_Service_Scheme_Logo.svg" alt="NSS VIIT" style={{ width: 52, height: 52, borderRadius: 10, objectFit: 'contain', marginBottom: 12 }} />
          <h2>Welcome back</h2>
          <p className="subtitle">Sign in to your NSS VIIT account</p>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className="form-group">
            <label className="form-label" htmlFor="email">Email</label>
            <input
              id="email" name="email" type="email"
              className="form-control" placeholder="you@viit.ac.in"
              value={form.email} onChange={handleChange} required
            />
          </div>
          <div className="form-group">
            <label className="form-label" htmlFor="password">Password</label>
            <input
              id="password" name="password" type="password"
              className="form-control" placeholder="••••••••"
              value={form.password} onChange={handleChange} required
            />
          </div>
          <button type="submit" className="btn btn-primary w-full mt-2" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign In'}
          </button>
        </form>

        <div className="divider" />
        <p style={{ textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ color: 'var(--red-primary)', fontWeight: 600 }}>Register</Link>
        </p>
      </div>
    </div>
  );
}
