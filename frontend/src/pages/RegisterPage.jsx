import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate     = useNavigate();
  const [form, setForm]     = useState({ name: '', email: '', password: '' });
  const [error, setError]   = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async e => {
    e.preventDefault();
    setError(null);
    if (form.password.length < 8) { setError('Password must be at least 8 characters.'); return; }
    setLoading(true);
    try {
      await register(form);
      navigate('/events');
    } catch (err) {
      setError(err.response?.data?.message ?? 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-wrapper auth-container">
      <div className="card auth-card">
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <img src="/National_Service_Scheme_logo.svg" alt="NSS VIIT" style={{ width: 52, height: 52, borderRadius: 10, objectFit: 'contain', marginBottom: 12 }} />
          <h2>Join NSS VIIT</h2>
          <p className="subtitle">Create your volunteer account</p>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className="form-group">
            <label className="form-label" htmlFor="name">Full Name</label>
            <input
              id="name" name="name" type="text"
              className="form-control" placeholder="Ritesh Godse"
              value={form.name} onChange={handleChange} required
            />
          </div>
          <div className="form-group">
            <label className="form-label" htmlFor="reg-email">Email</label>
            <input
              id="reg-email" name="email" type="email"
              className="form-control" placeholder="you@viit.ac.in"
              value={form.email} onChange={handleChange} required
            />
          </div>
          <div className="form-group">
            <label className="form-label" htmlFor="reg-password">Password</label>
            <input
              id="reg-password" name="password" type="password"
              className="form-control" placeholder="Min. 8 characters"
              value={form.password} onChange={handleChange} required minLength={8}
            />
          </div>
          <button type="submit" className="btn btn-primary w-full mt-2" disabled={loading}>
            {loading ? 'Creating account…' : '🙋 Create Account'}
          </button>
        </form>

        <div className="divider" />
        <p style={{ textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          Already a member?{' '}
          <Link to="/login" style={{ color: 'var(--red-primary)', fontWeight: 600 }}>Sign in</Link>
        </p>
      </div>
    </div>
  );
}
