// src/pages/HomePage.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { gunungApi, rencanaApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { formatDateShort, levelLabel, levelColor, getMtnEmoji } from '../utils/helpers';
import DashboardTools from '../components/DashboardTools';
import InstallButton from '../components/InstallButton';

export default function HomePage() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [gunung, setGunung]     = useState([]);
  const [rencana, setRencana]   = useState([]);
  const [loading, setLoading]   = useState(true);

  useEffect(() => {
    Promise.all([gunungApi.getAll(), rencanaApi.getAll({})])
      .then(([g, r]) => {
        setGunung(g.data.data || []);
        setRencana((r.data.data || []).filter(r => r.status === 'DIBUKA').slice(0, 5));
      })
      .finally(() => setLoading(false));
  }, []);

  const hour = new Date().getHours();
  const greeting = hour < 12 ? 'Selamat Pagi' : hour < 17 ? 'Selamat Siang' : 'Selamat Malam';

  return (
    <div className="page page-enter">
      {/* Hero */}
      <div className="hero">
        <div style={{ position: 'relative', zIndex: 1 }}>
          <div className="body-sm" style={{ color: 'var(--color-text-3)', marginBottom: 4 }}>
            {greeting}
          </div>
          <div className="heading-xl">
            Halo, {user?.nama?.split(' ')[0] || 'Pendaki'}
          </div>
          <div className="body-md" style={{ marginTop: 6 }}>Siap mencari teman mendaki hari ini?</div>

          {/* Quick stats */}
          <div style={{ display: 'flex', gap: 16, marginTop: 20 }}>
            {[
              { label: 'Gunung', value: gunung.length },
              { label: 'Rencana Aktif', value: rencana.length },
            ].map(s => (
              <div key={s.label} style={{
                flex: 1, background: 'var(--color-surface)', border: '1px solid var(--color-border)',
                borderRadius: 'var(--radius-md)', padding: '12px 14px', backdropFilter: 'blur(8px)'
              }}>
                <div style={{ fontSize: 22, fontWeight: 800, color: 'var(--color-accent)', lineHeight: 1 }}>
                  {loading ? '—' : s.value}
                </div>
                <div className="body-sm" style={{ marginTop: 2 }}>{s.label}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <DashboardTools />
      <div className="dashboard-install"><InstallButton /></div>

      {/* Gunung Section */}
      <div style={{ marginTop: 24 }}>
        <div className="section-header">
          <div className="heading-sm">Jelajahi Gunung</div>
          <button onClick={() => navigate('/gunung')}
            style={{ fontSize: 13, color: 'var(--color-primary)', fontWeight: 600, background: 'none', border: 'none', cursor: 'pointer' }}>
            Lihat Semua →
          </button>
        </div>

        <div className="scroll-x">
          {loading ? [1,2,3].map(i => (
            <div key={i} className="skeleton" style={{ width: 160, height: 180, flexShrink: 0, borderRadius: 'var(--radius-lg)' }} />
          )) : gunung.map(g => (
            <div key={g.id} className="mountain-card" style={{ width: 160, flexShrink: 0 }}
              onClick={() => navigate(`/gunung/${g.id}`)}>
              <div className="mountain-card-fake">
                <div style={{ position: 'relative', zIndex: 1 }}>
                  <div className="mountain-index">{String(g.id).padStart(2, '0')}</div>
                  <div className="heading-sm" style={{ marginTop: 8 }}>{g.nama.replace('Gunung ', '')}</div>
                  <div className="body-sm" style={{ marginTop: 2 }}>{g.lokasi}</div>
                  <div style={{ marginTop: 6 }}>
                    <span className="badge badge-green" style={{ fontSize: 10 }}>
                      {g.ketinggianMdpl?.toLocaleString()} mdpl
                    </span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Rencana Section */}
      <div style={{ marginTop: 24, paddingBottom: 8 }}>
        <div className="section-header">
          <div className="heading-sm">Rencana Terbuka</div>
          <button onClick={() => navigate('/rencana')}
            style={{ fontSize: 13, color: 'var(--color-primary)', fontWeight: 600, background: 'none', border: 'none', cursor: 'pointer' }}>
            Lihat Semua →
          </button>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 10, padding: '0 16px' }}>
          {loading ? [1,2].map(i => (
            <div key={i} className="skeleton" style={{ height: 100, borderRadius: 'var(--radius-lg)' }} />
          )) : rencana.length === 0 ? (
            <div className="empty-state">
              <div className="empty-icon">—</div>
              <div className="heading-sm">Belum ada rencana terbuka</div>
              <button className="btn btn-primary btn-sm" onClick={() => navigate('/rencana')}>Buat Rencana</button>
            </div>
          ) : rencana.map(r => (
            <div key={r.id} className="rencana-card" onClick={() => navigate(`/rencana/${r.id}`)}>
              <div className="rencana-card-header">
                <div>
                  <div className="heading-sm">{getMtnEmoji(r.jalurId || 0)} {r.namaGunung}</div>
                  <div className="body-sm" style={{ marginTop: 3 }}>Via {r.namaJalur}</div>
                </div>
                <span className={`badge badge-green`}>Dibuka</span>
              </div>
              <div className="rencana-meta">
                <div className="rencana-meta-item">📅 {formatDateShort(r.tanggalNaik)}</div>
                <div className="rencana-meta-item">{r.jumlahPesertaDiterima || 0}/{r.kuota} peserta</div>
                <div className="rencana-meta-item">
                  <span className={`badge ${levelColor(r.levelMinimum)}`} style={{ fontSize: 10 }}>
                    {levelLabel(r.levelMinimum)}
                  </span>
                </div>
              </div>
              {r.catatan && (
                <div className="body-sm truncate" style={{ marginTop: 8, color: 'var(--color-text-muted)' }}>
                  {r.catatan}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* CTA Banner */}
      <div style={{ margin: '20px 16px 8px', padding: 20,
        background: 'var(--color-surface)',
        border: '1px solid var(--color-border)', borderRadius: 'var(--radius-xl)' }}>
        <div className="heading-sm" style={{ marginBottom: 6 }}>Buat rencana sendiri</div>
        <div className="body-sm" style={{ marginBottom: 14, color: 'var(--color-text-2)' }}>
          Ajak teman baru bergabung dan jelajahi puncak bersama!
        </div>
        <button className="btn btn-primary btn-sm" onClick={() => navigate('/rencana/buat')}>
          + Buat Rencana
        </button>
      </div>
    </div>
  );
}
