import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
});

export const authApi = {
  register:        (data) => api.post('/auth/register', data),
  login:           (data) => api.post('/auth/login',    data),
  logout:          ()     => api.post('/auth/logout'),
  me:              ()     => api.get('/auth/me'),
  forgotPassword:  (data) => api.post('/auth/forgot-password', data),
  resetPassword:   (data) => api.post('/auth/reset-password', data),
};

export const profileApi = {
  update: (data) => api.put('/profile', data),
};

export const eventsApi = {
  list:   (page = 0, size = 12) => api.get('/events', { params: { page, size } }),
  getOne: (id)                  => api.get(`/events/${id}`),
  create: (data)                => api.post('/admin/events', data),
  update: (id, data)            => api.put(`/admin/events/${id}`, data),
  remove: (id)                  => api.delete(`/admin/events/${id}`),
};

export const pollsApi = {
  byEvent: (eventId)       => api.get(`/events/${eventId}/polls`),
  getOne:  (pollId)        => api.get(`/polls/${pollId}`),
  create:  (eventId, data) => api.post(`/admin/events/${eventId}/polls`, data),
};

export const votesApi = {
  vote:    (pollId, pollOptionId) => api.post(`/polls/${pollId}/vote`, { pollOptionId }),
  results: (pollId)               => api.get(`/polls/${pollId}/results`),
};

export const analyticsApi = {
  publicStats:          () => api.get('/analytics/public/stats'),
  overview:             () => api.get('/admin/analytics/overview'),
  bloodDonationImpact:  () => api.get('/admin/analytics/blood-donation-impact'),
};

export const bloodApi = {
  list:             (params)     => api.get('/blood-requests', { params }),
  forMap:           ()           => api.get('/blood-requests/map'),
  getOne:           (id)         => api.get(`/blood-requests/${id}`),
  submit:           (data)       => api.post('/blood-requests', data),
  registerInterest: (id, data)   => api.post(`/blood-requests/${id}/interest`, data),
  fulfill:          (id)         => api.patch(`/blood-requests/${id}/fulfill`),
};

export const adminApi = {
  listUsers: (params) => api.get('/admin/users', { params }),
  deleteUser: (id)    => api.delete(`/admin/users/${id}`),
};

export default api;
