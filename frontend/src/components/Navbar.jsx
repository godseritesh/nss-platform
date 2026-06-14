import { useState } from 'react';
import { useNavigate, NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const LINKS = [
  { to: '/',               label: 'Home'            },
  { to: '/events',         label: 'Events'          },
  { to: '/blood-requests', label: '🩸 Blood Requests', cls: 'blood-link' },
];

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

  const handleLogout = async () => { await logout(); navigate('/'); };

  return (
    <nav className="nav" role="navigation" aria-label="Main navigation">
      <div className="nav-inner">
        <a href="/" className="nav-brand">
          <img src="/National_Service_Scheme_logo.svg" alt="NSS" />
          <span className="nav-brand-text">NSS <em>VIIT</em> Pune</span>
        </a>

        <div className={`nav-links ${open ? 'open' : ''}`}>
          {LINKS.map(({ to, label, cls }) => (
            <NavLink key={to} to={to}
              className={({ isActive }) => `nav-link ${cls || ''} ${isActive ? 'active' : ''}`}
              onClick={() => setOpen(false)}>
              {label}
            </NavLink>
          ))}
          {isAdmin && (
            <NavLink to="/admin"
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              onClick={() => setOpen(false)}>
              Admin
            </NavLink>
          )}
        </div>

        <div className="nav-actions">
          {user ? (
            <>
              <NavLink to="/profile" className="nav-link" onClick={() => setOpen(false)} style={{ fontSize: '0.8rem', padding: '0.25rem 0.5rem' }}>{user.name.split(' ')[0]}</NavLink>
              <button className="btn btn-outline btn-sm" onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <>
              <button className="btn btn-ghost btn-sm" onClick={() => navigate('/login')}>Login</button>
              <button className="btn btn-primary btn-sm" onClick={() => navigate('/register')}>Register</button>
            </>
          )}
          <button className="hamburger" onClick={() => setOpen(o => !o)} aria-label="Menu">
            {open ? '✕' : '☰'}
          </button>
        </div>
      </div>
    </nav>
  );
}
