// src/pages/ProfilPage.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { rencanaApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../hooks/useToast';
import { userApi } from '../lib/api';
import { getErrMsg, levelLabel, levelColor, getInitials,
  formatDate, statusRencanaColor, statusRencanaLabel, getMtnEmoji } from '../utils/helpers';

export default function ProfilPage() {
  const navigate = useNavigate();
  const { user, logout, refreshUser } = useAuth();
  const { toast } = useToast();

  const [myRencana, setMyRencana] = useState([]);
  const [rencanaLoading, setRencanaLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    nama: '', levelFisik: 'PEMULA', pengalaman: '', kontakDarurat: ''
  });

  useEffect(() => {
    if (user) {
      setForm({ nama: user.nama || '', levelFisik: user.levelFisik || 'PEMULA',
        pengalaman: user.pengalaman || '', kontakDarurat: user.kontakDarurat || '' });
      rencanaApi.getMy()
        .then(r => setMyRencana(r.data.data || []))
        .finally(() => setRencanaLoading(false));
    }
  }, [user]);

  if (!user) {
    return (
      <div className="page page-enter">
        <div className="empty-state" style={{ minHeight: '80dvh' }}>
          <div className="empty-icon">👤</div>
          <div className="heading-md">Masuk untuk Melihat Profil</div>
          <div className="body-md" style={{ marginBottom: 16 }}>Daftar dan mulai cari teman mendaki!</div>
          <div style={{ display: 'flex', gap: 10 }}>
            <button className="btn btn-primary" onClick={() => navigate('/login')}>Masuk</button>
            <button className="btn btn-ghost" onClick={() => navigate('/register')}>Daftar</button>
          </div>
        </div>
      </div>
    );
  }

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await userApi.updateProfile(form);
      await refreshUser();
      toast('Profil berhasil diperbarui ✅', 'success');
      setEditing(false);
    } catch (err) {
      toast(getErrMsg(err), 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleLogout = () => {
    logout();
    toast('Berhasil keluar', 'info');
    navigate('/login');
  };

  const set = (k, v) => setForm(p => ({ ...p, [k]: v }));

  const totalRencana    = myRencana.length;
  const rencanaAktif    = myRencana.filter(r => r.status === 'DIBUKA').length;

  return (
    <div className="page page-enter">
      {/* Profile Hero */}
      <div style={{
        padding: '40px 16px 24px', background: 'var(--color-surface)',
        position: 'relative', overflow: 'hidden'
      }}>
        <div style={{ textAlign: 'center', position: 'relative', zIndex: 1 }}>
          {/* Avatar */}
          <div style={{
            width: 88, height: 88, borderRadius: '50%', margin: '0 auto 12px',
            background: 'var(--gradient-primary)', display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 34, fontWeight: 700, color: '#fff',
            border: '2px solid var(--color-border)', boxShadow: 'none'
          }}>
            {getInitials(user.nama)}
          </div>
          <div className="heading-md">{user.nama}</div>
          <div className="body-sm" style={{ marginTop: 4 }}>{user.email}</div>
          <div style={{ marginTop: 10, display: 'flex', justifyContent: 'center', gap: 8 }}>
            <span className={`badge ${levelColor(user.levelFisik)}`}>{levelLabel(user.levelFisik)}</span>
            {user.peran === 'ADMIN' && <span className="badge badge-amber">👑 Admin</span>}
          </div>
        </div>

        {/* Stats */}
        <div style={{ display: 'flex', gap: 12, marginTop: 20, justifyContent: 'center' }}>
          {[
            { label: 'Rencana', value: totalRencana },
            { label: 'Aktif', value: rencanaAktif },
          ].map(s => (
            <div key={s.label} style={{
              background: 'var(--color-surface-2)', border: '1px solid var(--color-border)',
              borderRadius: 'var(--radius-md)', padding: '10px 24px', textAlign: 'center'
            }}>
              <div style={{ fontSize: 22, fontWeight: 800, color: 'var(--color-accent)' }}>{s.value}</div>
              <div className="body-sm">{s.label}</div>
            </div>
          ))}
        </div>
      </div>

      <div className="container" style={{ paddingTop: 20 }}>
        {/* Edit Profile toggle */}
        {!editing ? (
          <>
            {/* Profile info */}
            <div className="card" style={{ marginBottom: 16 }}>
              <div className="card-body">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 14 }}>
                  <div className="heading-sm">Informasi Akun</div>
                  <button onClick={() => setEditing(true)}
                    style={{ fontSize: 13, color: 'var(--color-primary)', fontWeight: 600, background: 'none', border: 'none', cursor: 'pointer' }}>
                    ✏️ Edit
                  </button>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                  {[
                    { icon: '🧗', label: 'Pengalaman', value: user.pengalaman || 'Belum diisi' },
                    { icon: '📞', label: 'Kontak Darurat', value: user.kontakDarurat || 'Belum diisi' },
                  ].map(item => (
                    <div key={item.label}>
                      <div className="body-sm" style={{ color: 'var(--color-text-muted)', marginBottom: 3 }}>{item.icon} {item.label}</div>
                      <div style={{ fontSize: 14, color: item.value.includes('Belum') ? 'var(--color-text-muted)' : 'var(--color-text-2)' }}>
                        {item.value}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Admin Management Section */}
            {user.peran === 'ADMIN' && (
              <div className="card" style={{ marginBottom: 16, borderLeft: '4px solid var(--color-primary)' }}>
                <div className="card-body">
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <div className="heading-sm" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                        👑 <span>Panel Administrator</span>
                      </div>
                      <div className="body-sm" style={{ marginTop: 2 }}>
                        Kelola data jalur, pos waypoint, dan import GPX.
                      </div>
                    </div>
                  </div>
                  <button
                    className="btn btn-secondary btn-block"
                    onClick={() => navigate('/admin/jalur')}
                    style={{ marginTop: 12, justifyContent: 'center' }}
                  >
                    🛠️ Buka Kelola Jalur & GPX
                  </button>
                </div>
              </div>
            )}

            {/* My Rencana */}
            <div className="heading-sm" style={{ marginBottom: 10 }}>Rencana Pendakianku</div>
            {rencanaLoading ? (
              <div className="skeleton" style={{ height: 80, borderRadius: 'var(--radius-lg)' }} />
            ) : myRencana.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '24px 0', color: 'var(--color-text-muted)', fontSize: 14 }}>
                Belum ada rencana.{' '}
                <button onClick={() => navigate('/rencana/buat')} style={{ color: 'var(--color-primary)', fontWeight: 600, background: 'none', border: 'none', cursor: 'pointer' }}>
                  Buat sekarang →
                </button>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginBottom: 20 }}>
                {myRencana.map(r => (
                  <div key={r.id} className="card" style={{ cursor: 'pointer' }}
                    onClick={() => navigate(`/rencana/${r.id}`)}>
                    <div className="card-body" style={{ padding: 14 }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <div>
                          <div style={{ fontWeight: 600, fontSize: 14 }}>
                            {getMtnEmoji(r.jalurId || 0)} {r.namaGunung}
                          </div>
                          <div className="body-sm" style={{ marginTop: 2 }}>Via {r.namaJalur}</div>
                          <div className="body-sm" style={{ marginTop: 4 }}>📅 {formatDate(r.tanggalNaik)}</div>
                        </div>
                        <span className={`badge ${statusRencanaColor(r.status)}`}>{statusRencanaLabel(r.status)}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Logout */}
            <button className="btn btn-danger btn-block" onClick={handleLogout} style={{ marginBottom: 8 }}>
              Keluar dari Akun
            </button>
          </>
        ) : (
          /* Edit form */
          <form onSubmit={handleSave}>
            <div style={{ marginBottom: 20, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div className="heading-sm">Edit Profil</div>
              <button type="button" onClick={() => setEditing(false)}
                style={{ fontSize: 13, color: 'var(--color-text-muted)', background: 'none', border: 'none', cursor: 'pointer' }}>
                Batal
              </button>
            </div>

            <div className="form-group">
              <label className="form-label">Nama Lengkap</label>
              <input type="text" className="form-input" value={form.nama} onChange={e => set('nama', e.target.value)} />
            </div>

            <div className="form-group">
              <label className="form-label">Level Fisik</label>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
                {['PEMULA', 'MENENGAH', 'MAHIR'].map(lvl => (
                  <button type="button" key={lvl} onClick={() => set('levelFisik', lvl)}
                    style={{
                      padding: '11px 4px', borderRadius: 'var(--radius-md)', fontSize: 12, fontWeight: 600,
                      border: `2px solid ${form.levelFisik === lvl ? 'var(--color-primary)' : 'var(--color-border)'}`,
                      background: form.levelFisik === lvl ? '#e7f5ee' : 'var(--color-surface)',
                      color: form.levelFisik === lvl ? 'var(--color-primary)' : 'var(--color-text-muted)',
                      cursor: 'pointer', transition: 'all 0.2s', textAlign: 'center'
                    }}>
                    {lvl}
                  </button>
                ))}
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Pengalaman Mendaki</label>
              <textarea className="form-input" rows={3} value={form.pengalaman}
                onChange={e => set('pengalaman', e.target.value)}
                style={{ resize: 'none', lineHeight: 1.6 }} />
            </div>

            <div className="form-group">
              <label className="form-label">Kontak Darurat</label>
              <input type="tel" className="form-input" value={form.kontakDarurat}
                onChange={e => set('kontakDarurat', e.target.value)} placeholder="08xxxxxxxxxx" />
            </div>

            <button type="submit" className="btn btn-primary btn-block" disabled={saving}>
              {saving ? 'Menyimpan...' : 'Simpan Perubahan'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
