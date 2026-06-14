import { useEffect, useState } from 'react';
import { adminApi } from '../api';

export default function AdminUsersPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);

  useEffect(() => {
    let cancelled = false;
    adminApi.listUsers({ page, size: 20 })
      .then(({ data }) => { if (!cancelled) setUsers(data.content ?? []); })
      .catch(() => {})
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [page]);

  const handleDelete = async (id) => {
    if (!confirm('Delete this user?')) return;
    try {
      await adminApi.deleteUser(id);
      setUsers(u => u.filter(x => x.id !== id));
    } catch {}
  };

  return (
    <div className="page-wrapper">
      <div className="container section">
        <h1 style={{ marginBottom: '1.5rem' }}>👥 User Management</h1>
        {loading ? <div className="spinner" /> : (
          <div className="card" style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.875rem' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border)', textAlign: 'left' }}>
                  <th style={{ padding: '0.75rem' }}>ID</th>
                  <th style={{ padding: '0.75rem' }}>Name</th>
                  <th style={{ padding: '0.75rem' }}>Email</th>
                  <th style={{ padding: '0.75rem' }}>Role</th>
                  <th style={{ padding: '0.75rem' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map(u => (
                  <tr key={u.id} style={{ borderBottom: '1px solid var(--border)' }}>
                    <td style={{ padding: '0.75rem', color: 'var(--text-2)' }}>{u.id}</td>
                    <td style={{ padding: '0.75rem', fontWeight: 500 }}>{u.name}</td>
                    <td style={{ padding: '0.75rem' }}>{u.email}</td>
                    <td style={{ padding: '0.75rem' }}><span className={`badge ${u.role === 'ROLE_ADMIN' ? 'badge-critical' : 'badge-info'}`}>{u.role === 'ROLE_ADMIN' ? 'Admin' : 'User'}</span></td>
                    <td style={{ padding: '0.75rem' }}>
                      {u.role !== 'ROLE_ADMIN' && (
                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(u.id)}>Delete</button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {users.length === 0 && <p style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-2)' }}>No users found.</p>}
          </div>
        )}
        <div className="flex-center gap-1" style={{ marginTop: '1.5rem' }}>
          {page > 0 && <button className="btn btn-sm btn-outline" onClick={() => setPage(p => p - 1)}>← Previous</button>}
          <button className="btn btn-sm btn-outline" onClick={() => setPage(p => p + 1)}>Next →</button>
        </div>
      </div>
    </div>
  );
}
