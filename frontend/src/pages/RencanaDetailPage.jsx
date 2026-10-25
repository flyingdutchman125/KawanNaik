// src/pages/RencanaDetailPage.jsx
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { rencanaApi, pesertaApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../hooks/useToast';
import { formatDate, levelLabel, levelColor, statusRencanaColor, statusRencanaLabel,
  statusPesertaColor, statusPesertaLabel, getInitials, getErrMsg, getMtnEmoji } from '../utils/helpers';

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
      <div className="body-md">Memuat rencana...</div>
    </div>
  );

  if (!rencana) return null;

  const isHost     = user?.id === rencana.pembuatId;
  const myPeserta  = peserta.find(p => p.penggunaId === user?.id);
  const isFull     = (rencana.jumlahPesertaDiterima || 0) >= rencana.kuota;
  const isOpen     = rencana.status === 'DIBUKA';

  const handleJoin = async () => {
    if (!user) { navigate('/login'); return; }
    setJoining(true);
    try {
      await pesertaApi.join(id);
      toast('Berhasil mengajukan bergabung! 🎉', 'success');
      await load();
    } catch (e) { toast(getErrMsg(e), 'error'); }
    finally { setJoining(false); }
  };

  const handleLeave = async () => {
    setLeaving(true);
    try {
      await pesertaApi.leave(id);
      toast('Berhasil meninggalkan rencana', 'info');
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
      title: `Mendaki bareng ke ${rencana.namaGunung}`,
      text: `Ayo gabung rencana mendaki ke ${rencana.namaGunung} via ${rencana.namaJalur} tanggal ${formatDate(rencana.tanggalNaik)} di KawanNaik!`,
      url: window.location.href,
    };
    if (navigator.share) {
      try {
        await navigator.share(shareData);
      } catch (err) {
        /* user dismissed share dialog */
      }
    } else {
      navigator.clipboard.writeText(window.location.href);
      toast('Tautan rencana berhasil disalin ke clipboard! 📋', 'success');
    }
  };

  const handleCancel = async () => {
    if (!confirm('Yakin batalkan rencana pendakian ini?')) return;
    try {
      await rencanaApi.cancel(id);
      toast('Rencana pendakian berhasil dibatalkan', 'info');
      navigate('/rencana');
    } catch (e) { toast(getErrMsg(e), 'error'); }
  };

  const quota_pct = Math.round(((rencana.jumlahPesertaDiterima || 0) / rencana.kuota) * 100);

  return (
    <div className="page page-enter">
      {/* Hero */}
      <div style={{
        padding: '48px 16px 24px', position: 'relative', overflow: 'hidden',
        background: 'var(--color-surface)',
      }}>
        <button onClick={() => navigate(-1)} style={{
          position: 'absolute', top: 16, left: 16, background: 'var(--color-surface-2)',
          border: '1px solid var(--color-border)', borderRadius: 'var(--radius-full)',
          color: 'var(--color-text)', padding: '8px 14px', fontSize: 13, cursor: 'pointer'
        }}>← Kembali</button>

        <div style={{ textAlign: 'center', position: 'relative', zIndex: 1 }}>
          <div style={{ fontSize: 56, lineHeight: 1, marginBottom: 12 }}>
            {getMtnEmoji(rencana.jalurId || 0)}
          </div>
          <span className={`badge ${statusRencanaColor(rencana.status)}`} style={{ marginBottom: 10, display: 'inline-flex' }}>
            {statusRencanaLabel(rencana.status)}
          </span>
          <div className="heading-xl">{rencana.namaGunung}</div>
          <div className="body-md" style={{ marginTop: 4 }}>Via {rencana.namaJalur}</div>
        </div>
      </div>

      <div className="container" style={{ paddingTop: 20 }}>
        {/* Stats grid */}
        <div className="stat-row">
          <div className="stat-box">
            <div className="body-sm">Tanggal</div>
            <div style={{ fontWeight: 700, marginTop: 4, fontSize: 14 }}>{formatDate(rencana.tanggalNaik)}</div>
          </div>
          <div className="stat-box">
            <div className="body-sm">Level Min</div>
            <div style={{ marginTop: 4 }}>
              <span className={`badge ${levelColor(rencana.levelMinimum)}`}>{levelLabel(rencana.levelMinimum)}</span>
            </div>
          </div>
        </div>

        {/* Quota */}
        <div className="card" style={{ marginBottom: 16 }}>
          <div className="card-body">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
              <div className="body-sm">Kuota Peserta</div>
              <div style={{ fontWeight: 700, fontSize: 14, color: isFull ? 'var(--color-red)' : 'var(--color-text)' }}>
                {rencana.jumlahPesertaDiterima || 0} / {rencana.kuota}
              </div>
            </div>
            <div style={{ height: 8, background: 'var(--color-border)', borderRadius: 99, overflow: 'hidden' }}>
              <div style={{
                height: '100%', width: `${Math.min(quota_pct, 100)}%`,
                background: isFull ? 'var(--color-red)' : 'var(--gradient-primary)',
                borderRadius: 99, transition: 'width 0.5s ease'
              }} />
            </div>
            {isFull && <div style={{ fontSize: 11, color: 'var(--color-red)', marginTop: 6 }}>⚠️ Kuota penuh</div>}
          </div>
        </div>

        {/* Host */}
        <div className="card" style={{ marginBottom: 16 }}>
          <div className="card-body">
            <div className="body-sm" style={{ color: 'var(--color-text-muted)', marginBottom: 10 }}>Host / Pembuat</div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div className="avatar">{getInitials(rencana.namaPembuat)}</div>
              <div>
                <div style={{ fontWeight: 600, fontSize: 14 }}>{rencana.namaPembuat}</div>
                <div className="body-sm">{levelLabel(rencana.levelPembuat)}</div>
              </div>
              {isHost && <span className="badge badge-green" style={{ marginLeft: 'auto' }}>Kamu (Host)</span>}
            </div>
          </div>
        </div>

        {/* Catatan */}
        {rencana.catatan && (
          <div className="card" style={{ marginBottom: 16 }}>
            <div className="card-body">
              <div className="body-sm" style={{ color: 'var(--color-text-muted)', marginBottom: 6 }}>📋 Catatan</div>
              <div style={{ fontSize: 14, lineHeight: 1.7, color: 'var(--color-text-2)' }}>{rencana.catatan}</div>
            </div>
          </div>
        )}

        {/* Peserta list */}
        <div className="heading-sm" style={{ marginBottom: 10 }}>
          Daftar Peserta ({peserta.length})
        </div>
        {peserta.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '20px 0', color: 'var(--color-text-muted)', fontSize: 14 }}>
            Belum ada peserta yang mendaftar
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginBottom: 20 }}>
            {peserta.map(p => (
              <div key={p.id} className="card">
                <div className="card-body" style={{ padding: 12 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div className="avatar" style={{ width: 36, height: 36, fontSize: 13 }}>
                      {getInitials(p.namaPengguna)}
                    </div>
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 14, fontWeight: 600 }}>{p.namaPengguna}</div>
                      <div className="body-sm">{p.emailPengguna}</div>
                    </div>
                    <span className={`badge ${statusPesertaColor(p.status)}`}>
                      {statusPesertaLabel(p.status)}
                    </span>
                  </div>

                  {/* Host actions: approve/reject pending */}
                  {isHost && p.status === 'MENUNGGU' && (
                    <div style={{ display: 'flex', gap: 8, marginTop: 10 }}>
                      <button className="btn btn-ghost btn-sm" style={{ flex: 1 }}
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

        {/* Action Buttons */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10, paddingBottom: 16 }}>
          {/* Share Button */}
          <button
            type="button"
            className="btn btn-secondary btn-block"
            onClick={handleShare}
            style={{ justifyContent: 'center' }}
          >
            📢 Bagikan Rencana Ini
          </button>

          {/* Non-host: join/leave */}
          {!isHost && user && (
            <>
              {!myPeserta && isOpen && !isFull && (
                <button id="btn-join" className="btn btn-primary btn-block" onClick={handleJoin} disabled={joining}>
                  {joining ? <><span className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }} /> Mengajukan...</> : '🙋 Bergabung ke Rencana'}
                </button>
              )}
              {!myPeserta && isFull && isOpen && (
                <div style={{ textAlign: 'center', padding: 12, background: 'rgba(239,68,68,0.08)',
                  borderRadius: 'var(--radius-md)', fontSize: 13, color: 'var(--color-red)' }}>
                  ⚠️ Kuota sudah penuh
                </div>
              )}
              {myPeserta && (
                <div style={{ textAlign: 'center', padding: 14, background: 'var(--color-surface-2)',
                  border: '1px solid var(--color-border)', borderRadius: 'var(--radius-md)' }}>
                  <div style={{ fontSize: 14, color: 'var(--color-text-2)', marginBottom: 6 }}>
                    Status permohonanmu:{' '}
                    <span className={`badge ${statusPesertaColor(myPeserta.status)}`}>
                      {statusPesertaLabel(myPeserta.status)}
                    </span>
                  </div>
                  {myPeserta.status === 'MENUNGGU' && (
                    <button className="btn btn-danger btn-sm" style={{ marginTop: 8 }}
                      onClick={handleLeave} disabled={leaving}>
                      {leaving ? 'Membatalkan...' : 'Batalkan Ajuan'}
                    </button>
                  )}
                  {myPeserta.status === 'DITERIMA' && (
                    <div style={{ marginTop: 8 }}>
                      <p className="body-sm" style={{ color: 'var(--color-primary)', fontWeight: 600, marginBottom: 8 }}>
                        🎉 Kamu telah disetujui bergabung ke ekspedisi ini!
                      </p>
                      <button
                        className="btn btn-ghost btn-sm"
                        style={{ color: 'var(--color-red)' }}
                        onClick={() => {
                          if (confirm('Tinggalkan rencana pendakian ini?')) {
                            handleLeave();
                          }
                        }}
                        disabled={leaving}
                      >
                        {leaving ? 'Memproses...' : 'Tinggalkan Rencana'}
                      </button>
                    </div>
                  )}
                </div>
              )}
            </>
          )}

          {/* Host management actions */}
          {isHost && (
            <div className="card" style={{ marginTop: 6, border: '1px dashed var(--color-primary)' }}>
              <div className="card-body">
                <div className="heading-sm" style={{ marginBottom: 4 }}>⚙️ Kontrol Status Host</div>
                <div className="body-sm" style={{ marginBottom: 12 }}>
                  Ubah status rencana pendakian sesuai perkembangan tim:
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
                        if (confirm('Tandai rencana ini sebagai SELESAI?')) {
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
            <button className="btn btn-primary btn-block" onClick={() => navigate('/login')}>
              Masuk untuk Bergabung
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
