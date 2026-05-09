export default function Footer() {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-brand">
          <img src="/National_Service_Scheme_logo.svg" alt="NSS" />
          <span>NSS <em>VIIT</em> Pune</span>
        </div>
        <div className="footer-links">
          <a href="/">Home</a>
          <a href="/events">Events</a>
          <a href="/blood-requests">Blood Requests</a>
          <a href="/register">Register</a>
        </div>
        <p className="footer-copy">
          © {new Date().getFullYear()} NSS VIIT Pune · National Service Scheme ·{' '}
          <a href="https://github.com/ritesh-godse" target="_blank" rel="noopener noreferrer">
            Developed by Ritesh Godse
          </a>
        </p>
      </div>
    </footer>
  );
}
