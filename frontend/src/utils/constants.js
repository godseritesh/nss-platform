// ── Category metadata (mirrors backend Event.Category enum) ──
export const CATEGORY_META = {
  BLOOD_DONATION:        { label: 'Blood Donation',        badgeClass: 'badge-red',   icon: '🩸' },
  AWARENESS_DRIVE:       { label: 'Awareness Drive',       badgeClass: 'badge-blue',  icon: '📢' },
  VOLUNTEER_RECRUITMENT: { label: 'Volunteer Recruitment', badgeClass: 'badge-green', icon: '🙋' },
  CAMPUS_EVENT:          { label: 'Campus Event',          badgeClass: 'badge-amber', icon: '🎓' },
  FEEDBACK_POLL:         { label: 'Feedback Poll',         badgeClass: 'badge-muted', icon: '📊' },
};

// ── Poll status (mirrors backend Poll.Status enum) ──
export const POLL_STATUS = {
  ACTIVE: { label: 'Active', badgeClass: 'badge-green' },
  CLOSED: { label: 'Closed', badgeClass: 'badge-muted' },
};

// ── Blood group display (mirrors backend BloodRequest.BloodGroup enum) ──
export const BLOOD_GROUPS = [
  { value: 'A_POS',  label: 'A+' },
  { value: 'A_NEG',  label: 'A−' },
  { value: 'B_POS',  label: 'B+' },
  { value: 'B_NEG',  label: 'B−' },
  { value: 'O_POS',  label: 'O+' },
  { value: 'O_NEG',  label: 'O−' },
  { value: 'AB_POS', label: 'AB+' },
  { value: 'AB_NEG', label: 'AB−' },
];

// ── Urgency metadata (mirrors backend BloodRequest.Urgency enum) ──
export const URGENCY_META = {
  CRITICAL: { label: 'Critical', color: '#e53e3e', pulse: true,  badgeClass: 'badge-critical' },
  URGENT:   { label: 'Urgent',   color: '#dd6b20', pulse: false, badgeClass: 'badge-urgent'   },
  STANDARD: { label: 'Standard', color: '#4f8ef7', pulse: false, badgeClass: 'badge-standard' },
};

// ── Shared helpers ──
export const formatDate = (d) => {
  if (!d) return '—';
  return new Date(d).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
};

export const formatDatetime = (dt) => {
  if (!dt) return '—';
  return new Date(dt).toLocaleString('en-IN', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' });
};

export const truncate = (str, n = 130) =>
  str && str.length > n ? str.slice(0, n) + '…' : str;
