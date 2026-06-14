import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-brand">
          <img src="/National_Service_Scheme_logo.svg" alt="NSS" />
          <span>NSS <em>VIIT</em> Pune</span>
        </div>
        <div className="footer-links">
          <Link to="/">Home</Link>
          <Link to="/events">Events</Link>
          <Link to="/blood-requests">Blood Requests</Link>
          <Link to="/register">Register</Link>
        </div>
        <p className="footer-copy">
          &copy; {new Date().getFullYear()} NSS VIIT Pune &middot; National Service Scheme &middot;{' '}
          <a href="https://github.com/ritesh-godse" target="_blank" rel="noopener noreferrer">
            Developed by Ritesh Godse
          </a>
        </p>
      </div>
    </footer>
  );
}
