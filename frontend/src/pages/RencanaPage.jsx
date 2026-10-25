// src/pages/RencanaPage.jsx — Find hiking partner
import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { rencanaApi, gunungApi } from '../lib/api';
import { formatDate, levelLabel, levelColor, statusRencanaLabel, statusRencanaColor, getMtnEmoji } from '../utils/helpers';
import { useAuth } from '../context/AuthContext';

export default function RencanaPage() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const { isAuthenticated } = useAuth();

  const [rencana, setRencana]   = useState([]);
  const [gunung, setGunung]     = useState([]);
  const [loading, setLoading]   = useState(true);
  const [showFilter, setShow]   = useState(false);
  const [filter, setFilter]     = useState({
    gunungId: params.get('gunungId') || '',
    level: '',
    tanggal: ''
  });

  const load = useCallback(() => {
    setLoading(true);
    const p = {};
    if (filter.gunungId) p.gunungId = filter.gunungId;
    if (filter.level)    p.level    = filter.level;
    if (filter.tanggal)  p.tanggal  = filter.tanggal;
    rencanaApi.getAll(p)
      .then(r => setRencana(r.data.data || []))
      .finally(() => setLoading(false));
  }, [filter]);

  useEffect(() => { gunungApi.getAll().then(r => setGunung(r.data.data || [])); }, []);
  useEffect(() => { load(); }, [load]);

  const activeFilters = [filter.gunungId, filter.level, filter.tanggal].filter(Boolean).length;

  return (
    <div className="page page-enter">
      {/* App Bar */}
      <div className="app-bar">
        <div style={{ flex: 1 }}>
          <div className="app-bar-title">Cari kawan naik</div>
          <div className="app-bar-subtitle">Temukan teman mendaki</div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <button onClick={() => setShow(p => !p)} style={{
            padding: '8px 14px', borderRadius: 'var(--radius-full)', fontSize: 13, fontWeight: 600,
            background: activeFilters > 0 ? '#e7f5ee' : 'var(--color-surface)',
            border: `1.5px solid ${activeFilters > 0 ? 'var(--color-primary)' : 'var(--color-border)'}`,
            color: activeFilters > 0 ? 'var(--color-primary)' : 'var(--color-text-2)', cursor: 'pointer'
          }}>
            🔍 Filter{activeFilters > 0 ? ` (${activeFilters})` : ''}
          </button>
          {isAuthenticated && (
            <button onClick={() => navigate('/rencana/buat')} style={{
              padding: '8px 14px', borderRadius: 'var(--radius-full)', fontSize: 13, fontWeight: 600,
              background: 'var(--gradient-primary)', color: '#fff', cursor: 'pointer', border: 'none'
            }}>+ Buat</button>
          )}
        </div>
      </div>

      {/* Filter Panel */}
      {showFilter && (
        <div style={{ padding: '16px', background: 'var(--color-surface-2)', borderBottom: '1px solid var(--color-border)' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            <div>
              <div className="form-label" style={{ marginBottom: 6 }}>Gunung</div>
              <select className="form-select" value={filter.gunungId}
                onChange={e => setFilter(p => ({ ...p, gunungId: e.target.value }))}>
                <option value="">Semua Gunung</option>
                {gunung.map(g => <option key={g.id} value={g.id}>{g.nama.replace('Gunung ','')}</option>)}
              </select>
            </div>
            <div>
              <div className="form-label" style={{ marginBottom: 6 }}>Level</div>
              <select className="form-select" value={filter.level}
                onChange={e => setFilter(p => ({ ...p, level: e.target.value }))}>
                <option value="">Semua Level</option>
                <option value="PEMULA">Pemula</option>
                <option value="MENENGAH">Menengah</option>
                <option value="MAHIR">Mahir</option>
              </select>
            </div>
          </div>
          <div style={{ marginTop: 10 }}>
            <div className="form-label" style={{ marginBottom: 6 }}>Tanggal Naik</div>
            <input type="date" className="form-input" value={filter.tanggal}
              onChange={e => setFilter(p => ({ ...p, tanggal: e.target.value }))}
              min={new Date().toISOString().split('T')[0]}
            />
          </div>
          {activeFilters > 0 && (
            <button className="btn btn-ghost btn-sm btn-block" style={{ marginTop: 10 }}
              onClick={() => setFilter({ gunungId: '', level: '', tanggal: '' })}>
              Hapus Filter
            </button>
          )}
        </div>
      )}

      <div className="container" style={{ paddingTop: 16 }}>
        {/* Count */}
        {!loading && (
          <div className="body-sm" style={{ marginBottom: 12 }}>
            {rencana.length} rencana ditemukan
          </div>
        )}

        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {loading ? [1,2,3].map(i => (
            <div key={i} className="skeleton" style={{ height: 140, borderRadius: 'var(--radius-lg)' }} />
          )) : rencana.length === 0 ? (
            <div className="empty-state">
              <div className="empty-icon">🔍</div>
              <div className="heading-sm">Tidak ada rencana ditemukan</div>
              <div className="body-md">Coba ubah filter atau buat rencana baru</div>
              {isAuthenticated && (
                <button className="btn btn-primary btn-sm" style={{ marginTop: 8 }}
                  onClick={() => navigate('/rencana/buat')}>+ Buat Rencana</button>
              )}
            </div>
          ) : rencana.map(r => (
            <div key={r.id} className="rencana-card" onClick={() => navigate(`/rencana/${r.id}`)}>
              <div className="rencana-card-header">
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div className="heading-sm">
                    {getMtnEmoji(r.jalurId || 0)} {r.namaGunung}
                  </div>
                  <div className="body-sm" style={{ marginTop: 2 }}>Via {r.namaJalur}</div>
                </div>
                <span className={`badge ${statusRencanaColor(r.status)}`}>
                  {statusRencanaLabel(r.status)}
                </span>
              </div>

              <div className="rencana-meta">
                <div className="rencana-meta-item">📅 {formatDate(r.tanggalNaik)}</div>
                <div className="rencana-meta-item">
                  {r.jumlahPesertaDiterima || 0}/{r.kuota} peserta
                  {r.jumlahPesertaDiterima >= r.kuota && <span style={{ color: 'var(--color-red)', marginLeft: 4 }}>Penuh</span>}
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 10 }}>
                <span className={`badge ${levelColor(r.levelMinimum)}`}>
                  Min: {levelLabel(r.levelMinimum)}
                </span>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                  <div className="avatar" style={{ width: 24, height: 24, fontSize: 10 }}>
                    {r.namaPembuat?.[0] || '?'}
                  </div>
                  <span className="body-sm">{r.namaPembuat}</span>
                </div>
              </div>

              {r.catatan && (
                <div className="body-sm truncate" style={{ marginTop: 8, color: 'var(--color-text-muted)', fontSize: 12 }}>
                  💬 {r.catatan}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
