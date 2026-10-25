// src/pages/GunungDetailPage.jsx
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { gunungApi, jalurApi } from '../lib/api';
import { levelLabel, levelColor, getMtnEmoji } from '../utils/helpers';
import JalurMap from '../components/JalurMap';

const JENIS_ICON = {
  BASECAMP: '🏠', POS: '⛺', SUMBER_AIR: '💧', TITIK_RAWAN: '⚠️'
};

export default function GunungDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [gunung, setGunung]         = useState(null);
  const [jalur, setJalur]           = useState([]);
  const [selectedJalur, setSelected] = useState(null);
  const [titik, setTitik]           = useState([]);
  const [loading, setLoading]       = useState(true);

  useEffect(() => {
    Promise.all([gunungApi.getById(id), jalurApi.getByGunung(id)])
      .then(([g, j]) => {
        setGunung(g.data.data);
        const jalurList = j.data.data || [];
        setJalur(jalurList);
        if (jalurList.length > 0) {
          setSelected(jalurList[0]);
          setTitik(jalurList[0].titikJalurList || []);
        }
      })
      .finally(() => setLoading(false));
  }, [id]);

  const selectJalur = (j) => {
    setSelected(j);
    setTitik(j.titikJalurList || []);
  };

  if (loading) return (
    <div className="loading-screen">
      <div className="spinner" />
      <div className="body-md">Memuat data gunung...</div>
    </div>
  );

  if (!gunung) return (
    <div className="page page-enter">
      <div className="empty-state">
        <div className="empty-icon">❌</div>
        <div className="heading-sm">Gunung tidak ditemukan</div>
        <button className="btn btn-ghost btn-sm" onClick={() => navigate('/gunung')}>Kembali</button>
      </div>
    </div>
  );

  return (
    <div className="page page-enter">
      {/* Hero */}
      <div style={{
        position: 'relative', padding: '48px 16px 24px',
        background: 'var(--color-surface)',
        overflow: 'hidden'
      }}>
        <button onClick={() => navigate(-1)}
          style={{ position: 'absolute', top: 16, left: 16, background: 'var(--color-surface-2)',
            border: '1px solid var(--color-border)', borderRadius: 'var(--radius-full)',
            color: 'var(--color-text)', padding: '8px 14px', fontSize: 13, cursor: 'pointer' }}>
          ← Kembali
        </button>

        <div style={{ textAlign: 'center', position: 'relative', zIndex: 1 }}>
          <div style={{ fontSize: 72, lineHeight: 1, marginBottom: 12 }}>{getMtnEmoji(gunung.id)}</div>
          <div className="heading-xl">{gunung.nama}</div>
          <div className="body-md" style={{ marginTop: 6 }}>📍 {gunung.lokasi}</div>
          <div style={{ display: 'flex', gap: 10, justifyContent: 'center', marginTop: 14, flexWrap: 'wrap' }}>
            <span className="badge badge-green">{gunung.ketinggianMdpl?.toLocaleString()} mdpl</span>
            <span className="badge badge-blue">{jalur.length} jalur pendakian</span>
          </div>
        </div>

        {/* BG decoration */}
        <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0, height: '40%',
          background: 'linear-gradient(to top, var(--color-bg), transparent)', zIndex: 0 }} />
      </div>

      <div className="container" style={{ paddingTop: 20 }}>
        {/* Coordinates */}
        {gunung.latitude && (
          <div className="card" style={{ marginBottom: 20 }}>
            <div className="card-body">
              <div className="body-sm" style={{ color: 'var(--color-text-muted)', marginBottom: 6 }}>🌐 Koordinat</div>
              <div style={{ fontSize: 14, color: 'var(--color-text-2)', fontFamily: 'monospace' }}>
                {gunung.latitude}°, {gunung.longitude}°
              </div>
            </div>
          </div>
        )}

        {/* Jalur selector */}
        {jalur.length > 0 && (
          <>
            <div className="heading-sm" style={{ marginBottom: 10 }}>Pilih Jalur</div>
            <div className="scroll-x" style={{ padding: '4px 0 4px', marginBottom: 16, marginLeft: 0, marginRight: 0 }}>
              {jalur.map(j => (
                <button key={j.id} onClick={() => selectJalur(j)}
                  style={{
                    flexShrink: 0,
                    padding: '10px 16px',
                    borderRadius: 'var(--radius-full)',
                    fontSize: 13, fontWeight: 600,
                    border: `1.5px solid ${selectedJalur?.id === j.id ? 'var(--color-primary)' : 'var(--color-border)'}`,
                    background: selectedJalur?.id === j.id ? '#e7f5ee' : 'var(--color-surface)',
                    color: selectedJalur?.id === j.id ? 'var(--color-primary)' : 'var(--color-text-muted)',
                    cursor: 'pointer', whiteSpace: 'nowrap', transition: 'all 0.2s'
                  }}>
                  {j.namaJalur}
                </button>
              ))}
            </div>

            {selectedJalur && (
              <div className="card" style={{ marginBottom: 20 }}>
                <div className="card-body">
                  <div className="heading-sm">{selectedJalur.namaJalur}</div>
                  <div className="stat-row" style={{ marginTop: 14 }}>
                    <div className="stat-box">
                      <div className="body-sm">Kesulitan</div>
                      <div style={{ fontSize: 14, fontWeight: 700, color: 'var(--color-text)', marginTop: 4 }}>
                        {selectedJalur.tingkatKesulitan}
                      </div>
                    </div>
                    <div className="stat-box">
                      <div className="body-sm">Est. Waktu</div>
                      <div style={{ fontSize: 14, fontWeight: 700, color: 'var(--color-text)', marginTop: 4 }}>
                        {selectedJalur.estimasiJam} jam
                      </div>
                    </div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                    <span className="body-sm">Level minimum:</span>
                    <span className={`badge ${levelColor(selectedJalur.levelMinimum)}`}>
                      {levelLabel(selectedJalur.levelMinimum)}
                    </span>
                  </div>
                </div>
              </div>
            )}

            <JalurMap titik={titik} />

            {/* Titik jalur */}
            {titik.length > 0 && (
              <>
                <div className="heading-sm" style={{ marginBottom: 12 }}>Titik Jalur ({titik.length})</div>
                <div style={{ position: 'relative', paddingLeft: 16 }}>
                  {/* Timeline line */}
                  <div style={{ position: 'absolute', left: 19, top: 20, bottom: 20, width: 2,
                    background: 'linear-gradient(to bottom, var(--color-primary), transparent)' }} />
                  <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                    {titik.sort((a,b) => a.urutan - b.urutan).map((t, idx) => (
                      <div key={t.id} style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
                        {/* dot */}
                        <div style={{
                          width: 36, height: 36, borderRadius: '50%', flexShrink: 0,
                          background: idx === 0 ? 'var(--gradient-primary)' : 'var(--color-surface-2)',
                          border: `2px solid ${idx === 0 ? 'var(--color-primary)' : 'var(--color-border)'}`,
                          display: 'flex', alignItems: 'center', justifyContent: 'center',
                          fontSize: 16, position: 'relative', zIndex: 1
                        }}>
                          {JENIS_ICON[t.jenis] || '📍'}
                        </div>
                        <div className="card" style={{ flex: 1, padding: 0 }}>
                          <div className="card-body" style={{ padding: 12 }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                              <div className="heading-sm" style={{ fontSize: 14 }}>{t.nama}</div>
                              <span className="badge badge-muted" style={{ fontSize: 10, flexShrink: 0, marginLeft: 6 }}>
                                Pos {t.urutan}
                              </span>
                            </div>
                            {t.deskripsi && (
                              <div className="body-sm" style={{ marginTop: 6 }}>{t.deskripsi}</div>
                            )}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </>
            )}
          </>
        )}

        {/* Find companion CTA */}
        <div style={{ margin: '24px 0 8px', padding: 20,
          background: 'var(--color-surface)',
          border: '1px solid var(--color-border)', borderRadius: 'var(--radius-xl)', textAlign: 'center' }}>
          <div style={{ fontSize: 28, marginBottom: 8 }}>🧗</div>
          <div className="heading-sm" style={{ marginBottom: 6 }}>Mau mendaki {gunung.nama.replace('Gunung ', '')}?</div>
          <div className="body-sm" style={{ marginBottom: 14 }}>Cari rencana terbuka atau buat sendiri!</div>
          <button className="btn btn-primary btn-sm"
            onClick={() => navigate(`/rencana?gunungId=${gunung.id}`)}>
            Cari Teman Mendaki
          </button>
        </div>
      </div>
    </div>
  );
}
