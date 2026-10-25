// src/pages/GunungPage.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { gunungApi } from '../lib/api';
import { getMtnEmoji } from '../utils/helpers';

function IconSearch() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
      <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
    </svg>
  );
}

const REGION_FILTERS = ['Semua', 'Jawa Timur', 'Jawa Tengah', 'Jawa Barat', 'Luar Jawa'];

export default function GunungPage() {
  const navigate = useNavigate();
  const [gunung, setGunung] = useState([]);
  const [search, setSearch] = useState('');
  const [regionFilter, setRegionFilter] = useState('Semua');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    gunungApi.getAll(search || undefined)
      .then(r => setGunung(r.data.data || []))
      .finally(() => setLoading(false));
  }, [search]);

  const filtered = gunung.filter(g => {
    const matchSearch =
      g.nama.toLowerCase().includes(search.toLowerCase()) ||
      g.lokasi.toLowerCase().includes(search.toLowerCase());

    if (!matchSearch) return false;

    if (regionFilter === 'Semua') return true;
    if (regionFilter === 'Luar Jawa') {
      return !g.lokasi.toLowerCase().includes('jawa');
    }
    return g.lokasi.toLowerCase().includes(regionFilter.toLowerCase());
  });

  return (
    <div className="page page-enter">
      {/* App Bar */}
      <div className="app-bar">
        <div>
          <div className="app-bar-title">Gunung</div>
          <div className="app-bar-subtitle">Katalog gunung Indonesia</div>
        </div>
      </div>

      <div className="container" style={{ paddingTop: 16 }}>
        {/* Search Input */}
        <div style={{ position: 'relative', marginBottom: 12 }}>
          <div style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)', color: 'var(--color-text-muted)' }}>
            <IconSearch />
          </div>
          <input
            id="gunung-search"
            type="search"
            className="form-input"
            placeholder="Cari nama atau lokasi gunung..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{ paddingLeft: 42 }}
          />
        </div>

        {/* Region Filter Chips */}
        <div className="scroll-x" style={{ padding: '0 0 14px', marginLeft: 0, marginRight: 0 }}>
          {REGION_FILTERS.map(reg => (
            <button
              key={reg}
              onClick={() => setRegionFilter(reg)}
              style={{
                flexShrink: 0,
                padding: '7px 14px',
                borderRadius: 'var(--radius-full)',
                fontSize: 12,
                fontWeight: 600,
                border: `1.5px solid ${regionFilter === reg ? 'var(--color-primary)' : 'var(--color-border)'}`,
                background: regionFilter === reg ? '#e7f5ee' : 'var(--color-surface)',
                color: regionFilter === reg ? 'var(--color-primary)' : 'var(--color-text-muted)',
                cursor: 'pointer',
                transition: 'all 0.2s',
                whiteSpace: 'nowrap'
              }}
            >
              {reg}
            </button>
          ))}
        </div>

        {/* Count */}
        {!loading && (
          <div className="body-sm" style={{ marginBottom: 12 }}>
            Menampilkan <strong style={{ color: 'var(--color-text-2)' }}>{filtered.length}</strong> gunung
          </div>
        )}

        {/* Mountain List */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {loading ? [1, 2, 3].map(i => (
            <div key={i} className="skeleton" style={{ height: 120, borderRadius: 'var(--radius-lg)' }} />
          )) : filtered.length === 0 ? (
            <div className="empty-state">
              <div className="empty-icon">🔍</div>
              <div className="heading-sm">Gunung tidak ditemukan</div>
              <div className="body-md">Coba kata kunci atau filter wilayah lain</div>
            </div>
          ) : filtered.map(g => (
            <div
              key={g.id}
              className="card"
              style={{ cursor: 'pointer', transition: 'transform 0.15s ease' }}
              onClick={() => navigate(`/gunung/${g.id}`)}
            >
              <div className="card-body">
                <div style={{ display: 'flex', gap: 14, alignItems: 'center' }}>
                  {/* Mountain Graphic / Emoji */}
                  <div style={{
                    width: 58,
                    height: 58,
                    borderRadius: 'var(--radius-md)',
                    background: 'var(--color-surface-2)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: 30,
                    flexShrink: 0,
                    border: '1px solid var(--color-border)'
                  }}>
                    {getMtnEmoji(g.id)}
                  </div>

                  {/* Info */}
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div className="heading-sm">{g.nama}</div>
                    <div className="body-sm" style={{ marginTop: 2 }}>📍 {g.lokasi}</div>
                    <div style={{ display: 'flex', gap: 6, marginTop: 8, flexWrap: 'wrap' }}>
                      <span className="badge badge-green" style={{ fontSize: 11 }}>
                        {g.ketinggianMdpl?.toLocaleString()} mdpl
                      </span>
                      {g.jumlahJalur > 0 && (
                        <span className="badge badge-blue" style={{ fontSize: 11 }}>
                          {g.jumlahJalur} jalur pendakian
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Arrow */}
                  <div style={{ color: 'var(--color-text-muted)', fontSize: 20 }}>›</div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
