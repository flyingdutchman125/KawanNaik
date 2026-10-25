// src/pages/PanduanPage.jsx
import { useState, useEffect } from 'react';
import { panduanApi } from '../lib/api';

const KATEGORI_ICON = { Kesehatan: '🏥', Navigasi: '🧭', Darurat: '🚨', default: '📖' };

export default function PanduanPage() {
  const [panduan, setPanduan]   = useState([]);
  const [loading, setLoading]   = useState(true);
  const [expanded, setExpanded] = useState(null);
  const [tab, setTab]           = useState('Semua');

  useEffect(() => {
    panduanApi.getAll()
      .then(r => setPanduan(r.data.data || []))
      .finally(() => setLoading(false));
  }, []);

  const categories = ['Semua', ...new Set(panduan.map(p => p.kategori).filter(Boolean))];
  const filtered = tab === 'Semua' ? panduan : panduan.filter(p => p.kategori === tab);

  return (
    <div className="page page-enter">
      <div className="app-bar">
        <div>
          <div className="app-bar-title">Panduan keselamatan</div>
          <div className="app-bar-subtitle">Tips aman & nyaman mendaki</div>
        </div>
      </div>

      <div className="container" style={{ paddingTop: 16 }}>
        {/* Category tabs */}
        {categories.length > 1 && (
          <div className="scroll-x" style={{ padding: '0 0 12px', marginLeft: 0, marginRight: 0 }}>
            {categories.map(cat => (
              <button key={cat} onClick={() => setTab(cat)}
                style={{
                  flexShrink: 0, padding: '9px 18px',
                  borderRadius: 'var(--radius-full)', fontSize: 13, fontWeight: 600,
                  border: `1.5px solid ${tab === cat ? 'var(--color-primary)' : 'var(--color-border)'}`,
                  background: tab === cat ? '#e7f5ee' : 'var(--color-surface)',
                  color: tab === cat ? 'var(--color-primary)' : 'var(--color-text-muted)',
                  cursor: 'pointer', transition: 'all 0.2s', whiteSpace: 'nowrap'
                }}>
                {KATEGORI_ICON[cat] || KATEGORI_ICON.default} {cat}
              </button>
            ))}
          </div>
        )}

        {/* Panduan list */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {loading ? [1,2,3].map(i => (
            <div key={i} className="skeleton" style={{ height: 80, borderRadius: 'var(--radius-lg)' }} />
          )) : filtered.length === 0 ? (
            <div className="empty-state">
              <div className="empty-icon">📖</div>
              <div className="heading-sm">Belum ada panduan</div>
            </div>
          ) : filtered.map(p => (
            <div key={p.id} className="card" style={{ cursor: 'pointer' }}
              onClick={() => setExpanded(expanded === p.id ? null : p.id)}>
              <div className="card-body">
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: 12 }}>
                  <div style={{
                    width: 44, height: 44, borderRadius: 'var(--radius-md)', flexShrink: 0,
                    background: 'var(--color-surface-2)', border: '1px solid var(--color-border)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 22
                  }}>
                    {KATEGORI_ICON[p.kategori] || KATEGORI_ICON.default}
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div className="heading-sm">{p.judul}</div>
                    {p.kategori && (
                      <span className="badge badge-blue" style={{ marginTop: 6, fontSize: 10 }}>{p.kategori}</span>
                    )}
                  </div>
                  <div style={{
                    color: 'var(--color-text-muted)', fontSize: 20, transition: 'transform 0.2s',
                    transform: expanded === p.id ? 'rotate(180deg)' : 'rotate(0)'
                  }}>⌄</div>
                </div>

                {expanded === p.id && (
                  <div style={{
                    marginTop: 16, paddingTop: 16, borderTop: '1px solid var(--color-border)',
                    fontSize: 14, lineHeight: 1.8, color: 'var(--color-text-2)',
                    whiteSpace: 'pre-wrap'
                  }}>
                    {p.isi}
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>

        {/* Emergency numbers */}
        <div style={{ marginTop: 20, padding: 20, background: 'rgba(239,68,68,0.06)',
          border: '1px solid rgba(239,68,68,0.15)', borderRadius: 'var(--radius-xl)' }}>
          <div className="heading-sm" style={{ marginBottom: 10, color: 'var(--color-red)' }}>🚨 Nomor Darurat</div>
          {[
            { label: 'SAR Nasional (Basarnas)', number: '115' },
            { label: 'Darurat Umum', number: '112' },
            { label: 'Ambulans', number: '118' },
          ].map(n => (
            <a key={n.label} href={`tel:${n.number}`} style={{
              display: 'flex', justifyContent: 'space-between', alignItems: 'center',
              padding: '10px 0', borderBottom: '1px solid rgba(239,68,68,0.1)', textDecoration: 'none'
            }}>
              <span style={{ fontSize: 13, color: 'var(--color-text-2)' }}>{n.label}</span>
              <span style={{ fontSize: 16, fontWeight: 800, color: 'var(--color-red)', letterSpacing: 1 }}>{n.number}</span>
            </a>
          ))}
        </div>
      </div>
    </div>
  );
}
