import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar               from './components/Navbar';
import Footer               from './components/Footer';
import HomePage             from './pages/HomePage';
import EventsPage           from './pages/EventsPage';
import EventDetailPage      from './pages/EventDetailPage';
import LoginPage            from './pages/LoginPage';
import RegisterPage         from './pages/RegisterPage';
import AdminDashboard       from './pages/AdminDashboard';
import BloodRequestsPage    from './pages/BloodRequestsPage';
import BloodRequestDetailPage from './pages/BloodRequestDetailPage';
import SubmitRequestPage    from './pages/SubmitRequestPage';

function AdminRoute({ children }) {
  const { user, loading, isAdmin } = useAuth();
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
        <Route path="/blood-requests"           element={<BloodRequestsPage />} />
        <Route path="/blood-requests/submit"    element={<SubmitRequestPage />} />
        <Route path="/blood-requests/:id"       element={<BloodRequestDetailPage />} />
        <Route path="/login"    element={<GuestRoute><LoginPage /></GuestRoute>} />
        <Route path="/register" element={<GuestRoute><RegisterPage /></GuestRoute>} />
        <Route path="/admin"    element={<AdminRoute><AdminDashboard /></AdminRoute>} />
        <Route path="*"         element={<Navigate to="/" replace />} />
      </Routes>
      <Footer />
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}
