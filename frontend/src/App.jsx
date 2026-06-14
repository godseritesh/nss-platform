import { Component, lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route, Navigate, Link } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar               from './components/Navbar';
import Footer               from './components/Footer';
import HomePage             from './pages/HomePage';
import EventsPage           from './pages/EventsPage';
import EventDetailPage      from './pages/EventDetailPage';
import LoginPage            from './pages/LoginPage';
import RegisterPage         from './pages/RegisterPage';
import AdminDashboard       from './pages/AdminDashboard';
import ProfilePage          from './pages/ProfilePage';

const BloodRequestsPage     = lazy(() => import('./pages/BloodRequestsPage'));
const BloodRequestDetailPage = lazy(() => import('./pages/BloodRequestDetailPage'));
const SubmitRequestPage     = lazy(() => import('./pages/SubmitRequestPage'));
const AdminUsersPage        = lazy(() => import('./pages/AdminUsersPage'));

function Lazy({ children }) {
  return <Suspense fallback={<div className="flex-center" style={{ height: '60vh' }}><div className="spinner" /></div>}>{children}</Suspense>;
}

class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }
  static getDerivedStateFromError() { return { hasError: true }; }
  render() {
    if (this.state.hasError) {
      return (
        <div className="page-wrap flex-center" style={{ minHeight: '60vh', flexDirection: 'column', gap: '1rem', textAlign: 'center', padding: '2rem' }}>
          <h1 style={{ fontSize: '3rem', marginBottom: '0.5rem' }}>⚠️</h1>
          <h2>Something went wrong</h2>
          <p style={{ color: 'var(--text-2)', maxWidth: 400 }}>
            An unexpected error occurred. Please refresh the page or try again later.
          </p>
          <Link to="/" className="btn btn-primary" onClick={() => this.setState({ hasError: false })}>Go Home</Link>
        </div>
      );
    }
    return this.props.children;
  }
}

function NotFoundPage() {
  return (
    <div className="page-wrap flex-center" style={{ minHeight: '60vh', flexDirection: 'column', gap: '1rem', textAlign: 'center', padding: '2rem' }}>
      <h1 style={{ fontSize: '4rem', fontWeight: 800, margin: 0 }}>404</h1>
      <p style={{ color: 'var(--text-2)' }}>The page you&#39;re looking for doesn&#39;t exist.</p>
      <Link to="/" className="btn btn-primary">Go Home</Link>
    </div>
  );
}

function AdminRoute({ children }) {
  const { loading, isAdmin } = useAuth();
  if (loading) return <div className="flex-center" style={{ height: '80vh' }}><div className="spinner" /></div>;
  return isAdmin ? children : <Navigate to="/login" replace />;
}

function GuestRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="flex-center" style={{ height: '80vh' }}><div className="spinner" /></div>;
  return user ? <Navigate to="/" replace /> : children;
}

function AppRoutes() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/"                         element={<HomePage />} />
        <Route path="/events"                   element={<EventsPage />} />
        <Route path="/events/:id"               element={<EventDetailPage />} />
        <Route path="/blood-requests"           element={<Lazy><BloodRequestsPage /></Lazy>} />
        <Route path="/blood-requests/submit"    element={<Lazy><SubmitRequestPage /></Lazy>} />
        <Route path="/blood-requests/:id"       element={<Lazy><BloodRequestDetailPage /></Lazy>} />
        <Route path="/login"    element={<GuestRoute><LoginPage /></GuestRoute>} />
        <Route path="/register" element={<GuestRoute><RegisterPage /></GuestRoute>} />
        <Route path="/profile"  element={<ProfilePage />} />
        <Route path="/admin"    element={<AdminRoute><AdminDashboard /></AdminRoute>} />
        <Route path="/admin/users" element={<AdminRoute><Lazy><AdminUsersPage /></Lazy></AdminRoute>} />
        <Route path="*"         element={<NotFoundPage />} />
      </Routes>
      <Footer />
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ErrorBoundary>
          <AppRoutes />
        </ErrorBoundary>
      </AuthProvider>
    </BrowserRouter>
  );
}
