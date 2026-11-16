// src/pages/RencanaDetailPage.jsx
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { rencanaApi, pesertaApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../hooks/useToast';
import {
  formatDate, levelLabel, levelColor, statusRencanaColor, statusRencanaLabel,
  statusPesertaColor, statusPesertaLabel, getInitials, getErrMsg, getMtnEmoji, formatRupiah
} from '../utils/helpers';

export default function RencanaDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { toast } = useToast();

  const [rencana, setRencana]     = useState(null);
  const [peserta, setPeserta]     = useState([]);
  const [loading, setLoading]     = useState(true);
  const [joining, setJoining]     = useState(false);
  const [leaving, setLeaving]     = useState(false);
  const [packedItems, setPackedItems] = useState({});

  const load = async () => {
    try {
      const [r, p] = await Promise.all([
        rencanaApi.getById(id),
        pesertaApi.getByRencana(id)
      ]);
      setRencana(r.data.data);
      setPeserta(p.data.data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [id]);

  if (loading) return (
    <div className="loading-screen">
      <div className="spinner" />
      <div className="body-md">Memuat detail open trip...</div>
    </div>
  );

  if (!rencana) return null;

  const isHost       = user?.id === rencana.pembuatId;
  const myPeserta    = peserta.find(p => p.penggunaId === user?.id);
  const acceptedCount = rencana.jumlahPesertaDiterima || 0;
  const isFull       = acceptedCount >= rencana.kuota;
  const isOpen       = rencana.status === 'DIBUKA';
  const remaining    = Math.max(0, rencana.kuota - acceptedCount);

  const togglePacked = (item) => {
    setPackedItems(prev => ({ ...prev, [item]: !prev[item] }));
  };

  const handleJoin = async () => {
    if (!user) { navigate('/login'); return; }
    setJoining(true);
    try {
      await pesertaApi.join(id);
      toast('Pengajuan gabung berhasil dikirim ke Host! 🎉', 'success');
      await load();
    } catch (e) { toast(getErrMsg(e), 'error'); }
    finally { setJoining(false); }
  };

  const handleLeave = async () => {
    setLeaving(true);
    try {
      await pesertaApi.leave(id);
      toast('Berhasil meninggalkan rencana open trip', 'info');
      await load();
    } catch (e) { toast(getErrMsg(e), 'error'); }
    finally { setLeaving(false); }
  };

  const handleUpdatePeserta = async (pesertaId, status) => {
    try {
      await pesertaApi.updateStatus(id, pesertaId, status);
      toast(`Peserta ${status === 'DITERIMA' ? 'diterima ✅' : 'ditolak ❌'}`, status === 'DITERIMA' ? 'success' : 'info');
      await load();
    } catch (e) { toast(getErrMsg(e), 'error'); }
  };

  const handleUpdatePlanStatus = async (newStatus) => {
    try {
      await rencanaApi.updateStatus(id, newStatus);
      toast(`Status rencana berhasil diubah menjadi ${newStatus}`, 'success');
      await load();
    } catch (e) {
      toast(getErrMsg(e), 'error');
    }
  };

  const handleShare = async () => {
    const shareData = {
      title: `Open Trip ke ${rencana.namaGunung}`,
      text: `Ayo gabung open trip ke ${rencana.namaGunung} via ${rencana.namaJalur} tanggal ${formatDate(rencana.tanggalNaik)} di KawanNaik!`,
      url: window.location.href,
    };
    if (navigator.share) {
      try {
        await navigator.share(shareData);
      } catch (err) {
        /* user dismissed */
      }
    } else {
      navigator.clipboard.writeText(window.location.href);
      toast('Tautan open trip berhasil disalin ke clipboard! 📋', 'success');
    }
  };

  const handleCancel = async () => {
    if (!confirm('Yakin ingin membatalkan rencana pendakian ini? Semua peserta akan diberitahu.')) return;
    try {
      await rencanaApi.cancel(id);
      toast('Rencana pendakian berhasil dibatalkan', 'info');
      navigate('/rencana');
    } catch (e) { toast(getErrMsg(e), 'error'); }
  };

  const quota_pct = Math.round((acceptedCount / rencana.kuota) * 100);

  // Parse equipment list
  const gearList = rencana.perlengkapan
    ? rencana.perlengkapan.split(/[,\\n]+/).map(s => s.trim()).filter(Boolean)
    : [];

  const packedCount = gearList.filter(g => packedItems[g]).length;

  // Clean WhatsApp number
  const waNumber = rencana.kontak ? rencana.kontak.replace(/[^0-9]/g, '').replace(/^0/, '62') : '';

  return (
    <div className="page page-enter" style={{ background: 'var(--color-bg)', paddingBottom: 80 }}>
      {/* Hero Header with Topo Adventure Style */}
      <div style={{
        background: 'linear-gradient(135deg, #173c2b 0%, #1f7a4d 100%)',
        color: '#ffffff', padding: '24px 16px 28px', position: 'relative'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <button
            onClick={() => navigate(-1)}
            style={{
              background: 'rgba(255,255,255,0.15)', border: '1px solid rgba(255,255,255,0.25)',
              borderRadius: 'var(--radius-full)', color: '#ffffff',
              padding: '6px 14px', fontSize: 13, fontWeight: 600, cursor: 'pointer',
              display: 'inline-flex', alignItems: 'center', gap: 6
            }}
          >
            ← Kembali
          </button>

          <button
            onClick={handleShare}
            style={{
              background: 'rgba(255,255,255,0.15)', border: '1px solid rgba(255,255,255,0.25)',
              borderRadius: 'var(--radius-full)', color: '#ffffff',
              padding: '6px 14px', fontSize: 13, fontWeight: 600, cursor: 'pointer',
              display: 'inline-flex', alignItems: 'center', gap: 6
            }}
          >
            📢 Bagikan
          </button>
        </div>

        <div style={{ textAlign: 'center', position: 'relative', zIndex: 1, padding: '10px 0 6px' }}>
          <div style={{ fontSize: 50, lineHeight: 1, marginBottom: 10 }}>
            {getMtnEmoji(rencana.jalurId || 0)}
          </div>

          <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginBottom: 8 }}>
            <span className={`badge ${statusRencanaColor(rencana.status)}`} style={{ background: '#ffffff', color: '#173c2b', fontWeight: 800 }}>
              {statusRencanaLabel(rencana.status)}
            </span>
            <span className={`badge ${levelColor(rencana.levelMinimum)}`}>
              Min. {levelLabel(rencana.levelMinimum)}
            </span>
          </div>

          <h1 style={{ fontSize: 26, fontWeight: 800, color: '#ffffff', margin: 0, letterSpacing: '-0.02em' }}>
            {rencana.namaGunung}
          </h1>
          <div style={{ fontSize: 15, color: 'rgba(255,255,255,0.9)', marginTop: 4, fontWeight: 500 }}>
            Via {rencana.namaJalur}
          </div>
        </div>
      </div>

      <div className="container" style={{ paddingTop: 16 }}>
        {/* Expedition Metrics Dashboard (2x2 Grid) */}
        <div className="trip-detail-grid">
          {/* Tanggal & Waktu */}
          <div className="trip-detail-cell">
            <div className="cell-icon">⏰</div>
            <div className="cell-label">Jadwal Kumpul</div>
            <div className="cell-value">{formatDate(rencana.tanggalNaik)}</div>
            <div style={{ fontSize: 12, color: 'var(--color-primary)', fontWeight: 600 }}>
              Pukul {rencana.waktuKumpul || '06:00 WIB'}
            </div>
          </div>

          {/* Titik Kumpul */}
          <div className="trip-detail-cell">
            <div className="cell-icon">📍</div>
            <div className="cell-label">Meeting Point</div>
            <div className="cell-value" style={{ wordBreak: 'break-word' }}>
              {rencana.titikKumpul || 'Basecamp Jalur'}
            </div>
          </div>

          {/* Biaya */}
          <div className="trip-detail-cell">
            <div className="cell-icon">💰</div>
            <div className="cell-label">Estimasi Biaya</div>
            <div className="cell-value" style={{ color: 'var(--color-primary)', fontSize: 14 }}>
              {formatRupiah(rencana.estimasiBiaya)}
            </div>
            <div style={{ fontSize: 11, color: 'var(--color-text-muted)' }}>Per orang / patungan</div>
          </div>

          {/* Kuota Status */}
          <div className="trip-detail-cell">
            <div className="cell-icon">👥</div>
            <div className="cell-label">Ketersediaan Slot</div>
            <div className="cell-value">
              {acceptedCount} / {rencana.kuota} Orang
            </div>
            <div style={{ fontSize: 11, fontWeight: 700, color: isFull ? 'var(--color-red)' : 'var(--color-primary)' }}>
              {isFull ? 'Slot Penuh' : `Sisa ${remaining} slot lagi`}
            </div>
          </div>
        </div>

        {/* Quota Progress Bar */}
        <div className="card" style={{ marginBottom: 16 }}>
          <div className="card-body">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
              <span className="body-sm" style={{ fontWeight: 600 }}>Kapasitas Tim Terisi</span>
              <span style={{ fontWeight: 800, fontSize: 13, color: isFull ? 'var(--color-red)' : 'var(--color-primary)' }}>
                {quota_pct}%
              </span>
            </div>
            <div style={{ height: 10, background: 'var(--color-border)', borderRadius: 99, overflow: 'hidden' }}>
              <div style={{
                height: '100%', width: `${Math.min(quota_pct, 100)}%`,
                background: isFull ? 'var(--color-red)' : 'var(--gradient-primary)',
                borderRadius: 99, transition: 'width 0.4s ease'
              }} />
            </div>
          </div>
        </div>

        {/* Interactive Gear Checklist */}
        {gearList.length > 0 && (
          <div className="card" style={{ marginBottom: 16 }}>
            <div className="card-body">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <span style={{ fontSize: 20 }}>🎒</span>
                  <div>
                    <div className="heading-sm">Daftar Perlengkapan Trip</div>
                    <div className="body-sm" style={{ color: 'var(--color-text-muted)' }}>
                      Centang perlengkapan yang sudah kamu siapkan di tas:
                    </div>
                  </div>
                </div>
                <span className="badge badge-green" style={{ fontSize: 11, fontWeight: 700 }}>
                  {packedCount}/{gearList.length} Siap
                </span>
              </div>

              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 8 }}>
                {gearList.map(item => {
                  const isDone = Boolean(packedItems[item]);
                  return (
                    <div
                      key={item}
                      onClick={() => togglePacked(item)}
                      className={`gear-interactive-chip${isDone ? ' active' : ''}`}
                      style={{ cursor: 'pointer' }}
                    >
                      <span className="chip-check">{isDone ? '✓' : '○'}</span>
                      <span style={{ textDecoration: isDone ? 'line-through' : 'none', opacity: isDone ? 0.8 : 1 }}>
                        {item}
                      </span>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        )}

        {/* Host & WhatsApp Coordination Card */}
        <div className="card" style={{ marginBottom: 16 }}>
          <div className="card-body">
            <div className="body-sm" style={{ color: 'var(--color-text-muted)', marginBottom: 10 }}>
              Host & Penyelenggara Ekspedisi
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div className="avatar" style={{ width: 44, height: 44, fontSize: 16 }}>
                {getInitials(rencana.namaPembuat)}
              </div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontWeight: 700, fontSize: 15, color: 'var(--color-text)' }}>
                  {rencana.namaPembuat}
                </div>
                <div className="body-sm" style={{ color: 'var(--color-primary)', fontWeight: 600 }}>
                  Pendaki {levelLabel(rencana.levelPembuat)}
                </div>
              </div>
              {isHost && <span className="badge badge-green">Kamu (Host)</span>}
            </div>

            {/* Direct WhatsApp Call to Action */}
            {waNumber && (
              <div style={{ marginTop: 14 }}>
                <a
                  href={`https://wa.me/${waNumber}?text=${encodeURIComponent(
                    `Halo ${rencana.namaPembuat}, saya ingin tanya mengenai open trip ke ${rencana.namaGunung} via ${rencana.namaJalur} tanggal ${formatDate(rencana.tanggalNaik)} di KawanNaik.`
                  )}`}
                  target="_blank"
                  rel="noreferrer"
                  className="wa-btn-styled"
                >
                  <span style={{ fontSize: 18 }}>💬</span>
                  <span>Hubungi Host via WhatsApp</span>
                </a>
              </div>
            )}
          </div>
        </div>

        {/* Catatan / Itinerary Notes */}
        {rencana.catatan && (
          <div className="card" style={{ marginBottom: 16 }}>
            <div className="card-body">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
                <span style={{ fontSize: 18 }}>📝</span>
                <div className="heading-sm">Catatan & Ketentuan Perjalanan</div>
              </div>
              <div style={{
                fontSize: 14, lineHeight: 1.7, color: 'var(--color-text-2)',
                background: 'var(--color-surface-2)', padding: '12px 14px', borderRadius: 'var(--radius-md)',
                border: '1px solid var(--color-border)', whiteSpace: 'pre-line'
              }}>
                {rencana.catatan}
              </div>
            </div>
          </div>
        )}

        {/* Crew Roster / Daftar Peserta */}
        <div style={{ marginBottom: 20 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 }}>
            <div className="heading-sm">
              Rombongan Pendaki ({peserta.length})
            </div>
            <span className="body-sm" style={{ color: 'var(--color-text-muted)' }}>
              {acceptedCount} terkonfirmasi
            </span>
          </div>

          {peserta.length === 0 ? (
            <div className="card" style={{ textAlign: 'center', padding: '24px 16px' }}>
              <div style={{ fontSize: 32, marginBottom: 8 }}>🧗</div>
              <div className="heading-sm" style={{ fontSize: 15 }}>Belum Ada Peserta yang Bergabung</div>
              <div className="body-sm" style={{ color: 'var(--color-text-muted)', marginTop: 4 }}>
                Jadilah pendaki pertama yang mengajukan diri untuk rombongan ini!
              </div>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              {peserta.map(p => (
                <div key={p.id} className="card">
                  <div className="card-body" style={{ padding: 12 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                      <div className="avatar" style={{ width: 38, height: 38, fontSize: 13 }}>
                        {getInitials(p.namaPengguna)}
                      </div>
                      <div style={{ flex: 1, minWidth: 0 }}>
                        <div style={{ fontSize: 14, fontWeight: 700 }}>{p.namaPengguna}</div>
                        <div className="body-sm" style={{ fontSize: 12 }}>{p.emailPengguna}</div>
                      </div>
                      <span className={`badge ${statusPesertaColor(p.status)}`}>
                        {statusPesertaLabel(p.status)}
                      </span>
                    </div>

                    {/* Host action buttons */}
                    {isHost && p.status === 'MENUNGGU' && (
                      <div style={{ display: 'flex', gap: 8, marginTop: 12, paddingTop: 10, borderTop: '1px dashed var(--color-border)' }}>
                        <button className="btn btn-ghost btn-sm" style={{ flex: 1, color: 'var(--color-primary)' }}
                          onClick={() => handleUpdatePeserta(p.id, 'DITERIMA')}>✅ Terima</button>
                        <button className="btn btn-danger btn-sm" style={{ flex: 1 }}
                          onClick={() => handleUpdatePeserta(p.id, 'DITOLAK')}>❌ Tolak</button>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Action Panel for Host & Non-Host */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {/* Non-host join action */}
          {!isHost && user && (
            <>
              {!myPeserta && isOpen && !isFull && (
                <button
                  id="btn-join"
                  className="btn btn-primary btn-block"
                  onClick={handleJoin}
                  disabled={joining}
                  style={{
                    padding: '16px 20px', fontSize: 16, fontWeight: 800,
                    boxShadow: '0 4px 14px rgba(31, 122, 77, 0.35)'
                  }}
                >
                  {joining ? (
                    <><span className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }} /> Mengirim Pengajuan...</>
                  ) : (
                    '🙋 Ajukan Bergabung ke Rombongan'
                  )}
                </button>
              )}

              {!myPeserta && isFull && isOpen && (
                <div style={{
                  textAlign: 'center', padding: 14, background: 'rgba(180,35,24,0.08)',
                  borderRadius: 'var(--radius-md)', fontSize: 14, color: 'var(--color-red)', fontWeight: 600
                }}>
                  ⚠️ Kuota rombongan ini sudah penuh
                </div>
              )}

              {myPeserta && (
                <div style={{
                  padding: 16, background: 'var(--color-surface)',
                  border: '1.5px solid var(--color-border)', borderRadius: 'var(--radius-lg)'
                }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                    <span className="body-sm" style={{ fontWeight: 600 }}>Status Keikutsertaan:</span>
                    <span className={`badge ${statusPesertaColor(myPeserta.status)}`}>
                      {statusPesertaLabel(myPeserta.status)}
                    </span>
                  </div>

                  {myPeserta.status === 'MENUNGGU' && (
                    <div style={{ marginTop: 8 }}>
                      <p className="body-sm" style={{ color: 'var(--color-text-muted)', marginBottom: 8 }}>
                        Permohonanmu sedang menunggu persetujuan dari host trip.
                      </p>
                      <button className="btn btn-danger btn-sm btn-block"
                        onClick={handleLeave} disabled={leaving}>
                        {leaving ? 'Membatalkan...' : 'Batalkan Pengajuan'}
                      </button>
                    </div>
                  )}

                  {myPeserta.status === 'DITERIMA' && (
                    <div style={{ marginTop: 8 }}>
                      <p className="body-sm" style={{ color: 'var(--color-primary)', fontWeight: 700, marginBottom: 10 }}>
                        🎉 Selamat! Kamu telah resmi terdaftar di rombongan ini. Segera hubungi host untuk persiapan keberangkatan.
                      </p>
                      <button
                        className="btn btn-ghost btn-sm btn-block"
                        style={{ color: 'var(--color-red)' }}
                        onClick={() => {
                          if (confirm('Yakin ingin meninggalkan rencana open trip ini?')) {
                            handleLeave();
                          }
                        }}
                        disabled={leaving}
                      >
                        {leaving ? 'Memproses...' : 'Tinggalkan Rombongan'}
                      </button>
                    </div>
                  )}
                </div>
              )}
            </>
          )}

          {/* Host management controls */}
          {isHost && (
            <div className="card" style={{ border: '1.5px dashed var(--color-primary)' }}>
              <div className="card-body">
                <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 6 }}>
                  <span>⚙️</span>
                  <div className="heading-sm">Panel Kontrol Host</div>
                </div>
                <div className="body-sm" style={{ marginBottom: 12 }}>
                  Kelola status pendaftaran sesuai kapasitas dan jadwal rombongan:
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
                  {rencana.status === 'DIBUKA' && (
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleUpdatePlanStatus('DITUTUP')}
                    >
                      🔒 Tutup Pendaftaran
                    </button>
                  )}
                  {rencana.status === 'DITUTUP' && (
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleUpdatePlanStatus('DIBUKA')}
                    >
                      🔓 Buka Pendaftaran
                    </button>
                  )}
                  {rencana.status !== 'SELESAI' && rencana.status !== 'DIBATALKAN' && (
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => {
                        if (confirm('Tandai pendakian ini telah SELESAI?')) {
                          handleUpdatePlanStatus('SELESAI');
                        }
                      }}
                    >
                      🏁 Selesai Mendaki
                    </button>
                  )}
                </div>
                {rencana.status === 'DIBUKA' && (
                  <button
                    className="btn btn-danger btn-sm btn-block"
                    onClick={handleCancel}
                    style={{ marginTop: 10 }}
                  >
                    🗑️ Batalkan Rencana
                  </button>
                )}
              </div>
            </div>
          )}

          {/* Not logged in */}
          {!user && (
            <button
              className="btn btn-primary btn-block"
              onClick={() => navigate('/login')}
              style={{ padding: '14px', fontSize: 15, fontWeight: 700 }}
            >
              Masuk untuk Bergabung ke Trip
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
