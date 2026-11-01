// src/pages/AdminGunungPage.jsx
import { useEffect, useState, useMemo } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { gunungApi } from '../lib/api';
import { useToast } from '../hooks/useToast';
import { getErrMsg, getMtnEmoji } from '../utils/helpers';

export default function AdminGunungPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { toast } = useToast();

  const [activeTab, setActiveTab] = useState(searchParams.get('tab') === 'tambah' ? 'tambah' : 'daftar');
  const [gunungList, setGunungList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [fetchingGps, setFetchingGps] = useState(false);

  // Edit State
  const [editingGunung, setEditingGunung] = useState(null);

  // Form State
  const [formData, setFormData] = useState({
    nama: '',
    lokasi: '',
    ketinggianMdpl: '',
    latitude: '',
    longitude: '',
    kontakBasecamp: ''
  });

  const loadGunung = async () => {
    setLoading(true);
    try {
      const res = await gunungApi.getAll();
      setGunungList(res.data?.data || []);
    } catch (err) {
      toast(getErrMsg(err), 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadGunung();
  }, []);

  // Handle edit id from query param if provided
  useEffect(() => {
    const editId = searchParams.get('edit');
    if (editId && gunungList.length > 0) {
      const found = gunungList.find(g => String(g.id) === String(editId));
      if (found) {
        startEdit(found);
      }
    }
  }, [searchParams, gunungList]);

  const resetForm = () => {
    setFormData({
      nama: '',
      lokasi: '',
      ketinggianMdpl: '',
      latitude: '',
      longitude: '',
      kontakBasecamp: ''
    });
    setEditingGunung(null);
  };

  const startEdit = (gunung) => {
    setEditingGunung(gunung);
    setFormData({
      nama: gunung.nama || '',
      lokasi: gunung.lokasi || '',
      ketinggianMdpl: gunung.ketinggianMdpl != null ? String(gunung.ketinggianMdpl) : '',
      latitude: gunung.latitude != null ? String(gunung.latitude) : '',
      longitude: gunung.longitude != null ? String(gunung.longitude) : '',
      kontakBasecamp: gunung.kontakBasecamp || ''
    });
    setActiveTab('tambah');
  };

  // Get current device GPS
  const handleGetGps = () => {
    if (!navigator.geolocation) {
      return toast('Perangkat tidak mendukung geolokasi GPS', 'error');
    }
    setFetchingGps(true);
    navigator.geolocation.getCurrentPosition(
      pos => {
        setFormData(prev => ({
          ...prev,
          latitude: pos.coords.latitude.toFixed(6),
          longitude: pos.coords.longitude.toFixed(6)
        }));
        setFetchingGps(false);
        toast('Koordinat GPS berhasil diperoleh! 📡', 'success');
      },
      err => {
        setFetchingGps(false);
        toast(`Gagal mengambil GPS: ${err.message}`, 'error');
      },
      { enableHighAccuracy: true, timeout: 10000 }
    );
  };

  // Handle Create or Update
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.nama.trim()) {
      return toast('Nama gunung wajib diisi', 'error');
    }
    if (!formData.lokasi.trim()) {
      return toast('Lokasi gunung wajib diisi', 'error');
    }
    if (!formData.ketinggianMdpl || isNaN(Number(formData.ketinggianMdpl)) || Number(formData.ketinggianMdpl) <= 0) {
      return toast('Ketinggian harus berupa angka positif', 'error');
    }

    setSubmitting(true);
    try {
      const payload = {
        nama: formData.nama.trim(),
        lokasi: formData.lokasi.trim(),
        ketinggianMdpl: parseInt(formData.ketinggianMdpl, 10),
        latitude: formData.latitude ? parseFloat(formData.latitude) : null,
        longitude: formData.longitude ? parseFloat(formData.longitude) : null,
        kontakBasecamp: formData.kontakBasecamp ? formData.kontakBasecamp.trim() : null
      };

      if (editingGunung) {
        await gunungApi.update(editingGunung.id, payload);
        toast(`Data gunung "${payload.nama}" berhasil diperbarui! ⛰️`, 'success');
      } else {
        await gunungApi.create(payload);
        toast(`Gunung "${payload.nama}" berhasil ditambahkan! 🏔️`, 'success');
      }

      resetForm();
      setActiveTab('daftar');
      // clear search params
      setSearchParams({});
      await loadGunung();
    } catch (err) {
      toast(getErrMsg(err), 'error');
    } finally {
      setSubmitting(false);
    }
  };

  // Handle Delete
  const handleDelete = async (gunung) => {
    const confirmMessage = gunung.jumlahJalur > 0
      ? `Gunung "${gunung.nama}" memiliki ${gunung.jumlahJalur} jalur pendakian. Menghapus gunung akan menghapus seluruh data jalur dan waypoint terkait. Lanjutkan?`
      : `Hapus data gunung "${gunung.nama}"?`;

    if (!window.confirm(confirmMessage)) return;

    try {
      await gunungApi.delete(gunung.id);
      toast(`Gunung "${gunung.nama}" berhasil dihapus`, 'info');
      if (editingGunung?.id === gunung.id) {
        resetForm();
      }
      await loadGunung();
    } catch (err) {
      toast(getErrMsg(err), 'error');
    }
  };

  const filteredGunung = useMemo(() => {
    if (!searchQuery) return gunungList;
    const q = searchQuery.toLowerCase();
    return gunungList.filter(g =>
      g.nama.toLowerCase().includes(q) ||
      g.lokasi.toLowerCase().includes(q)
    );
  }, [gunungList, searchQuery]);

  return (
    <div className="page page-enter">
      {/* App Bar */}
      <div className="app-bar">
        <button
          onClick={() => navigate('/profil')}
          style={{
            background: 'none',
            border: 'none',
            color: 'var(--color-text-muted)',
            fontSize: 20,
            cursor: 'pointer',
            padding: '4px 8px 4px 0'
          }}
          title="Kembali ke profil"
        >
          ←
        </button>
        <div>
          <div className="app-bar-title">Kelola Master Gunung</div>
          <div className="app-bar-subtitle">Panel Admin KawanNaik</div>
        </div>
      </div>

      <div className="container" style={{ paddingTop: 16 }}>
        {/* Navigation Switch between Admin Pages */}
        <div
          style={{
            display: 'flex',
            gap: 8,
            marginBottom: 16,
            background: 'var(--color-surface)',
            padding: 6,
            borderRadius: 'var(--radius-lg)',
            border: '1px solid var(--color-border)'
          }}
        >
          <button
            onClick={() => {
              setActiveTab('daftar');
              resetForm();
            }}
            style={{
              flex: 1,
              padding: '8px 12px',
              borderRadius: 'var(--radius-md)',
              fontSize: 13,
              fontWeight: 600,
              background: activeTab === 'daftar' ? 'var(--color-primary)' : 'transparent',
              color: activeTab === 'daftar' ? '#ffffff' : 'var(--color-text-muted)',
              cursor: 'pointer',
              transition: 'all 0.2s',
              textAlign: 'center'
            }}
          >
            📋 Daftar Gunung ({gunungList.length})
          </button>
          <button
            onClick={() => {
              if (activeTab !== 'tambah') {
                resetForm();
              }
              setActiveTab('tambah');
            }}
            style={{
              flex: 1,
              padding: '8px 12px',
              borderRadius: 'var(--radius-md)',
              fontSize: 13,
              fontWeight: 600,
              background: activeTab === 'tambah' ? 'var(--color-primary)' : 'transparent',
              color: activeTab === 'tambah' ? '#ffffff' : 'var(--color-text-muted)',
              cursor: 'pointer',
              transition: 'all 0.2s',
              textAlign: 'center'
            }}
          >
            {editingGunung ? '✏️ Edit Gunung' : '➕ Tambah Gunung'}
          </button>
        </div>

        {/* Quick Link to Jalur & GPX */}
        <div
          style={{
            marginBottom: 16,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            background: 'var(--color-surface-2)',
            padding: '10px 14px',
            borderRadius: 'var(--radius-md)',
            border: '1px solid var(--color-border)'
          }}
        >
          <div className="body-sm" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <span>🗺️ Ingin kelola rute jalur atau import GPX?</span>
          </div>
          <button
            onClick={() => navigate('/admin/jalur')}
            style={{
              fontSize: 12,
              fontWeight: 700,
              color: 'var(--color-primary)',
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              textDecoration: 'underline'
            }}
          >
            Buka Panel Jalur →
          </button>
        </div>

        {/* TAB 1: DAFTAR GUNUNG */}
        {activeTab === 'daftar' && (
          <div>
            {/* Search Input */}
            <div style={{ position: 'relative', marginBottom: 14 }}>
              <input
                type="search"
                className="form-input"
                placeholder="Cari nama atau provinsi gunung..."
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
              />
            </div>

            {loading ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
                {[1, 2, 3].map(i => (
                  <div key={i} className="skeleton" style={{ height: 100, borderRadius: 'var(--radius-md)' }} />
                ))}
              </div>
            ) : filteredGunung.length === 0 ? (
              <div className="empty-state">
                <div className="empty-icon">🏔️</div>
                <div className="heading-sm">Belum ada gunung ditemukan</div>
                <div className="body-md" style={{ marginBottom: 16 }}>
                  {searchQuery ? 'Coba ubah kata kunci pencarian Anda' : 'Silakan tambahkan data gunung baru'}
                </div>
                <button
                  className="btn btn-primary btn-sm"
                  onClick={() => {
                    resetForm();
                    setActiveTab('tambah');
                  }}
                >
                  ➕ Tambah Gunung Sekarang
                </button>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {filteredGunung.map(g => (
                  <div key={g.id} className="card">
                    <div className="card-body">
                      <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
                        {/* Mountain Icon / Emoji */}
                        <div
                          style={{
                            width: 52,
                            height: 52,
                            borderRadius: 'var(--radius-md)',
                            background: 'var(--color-surface-2)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontSize: 26,
                            flexShrink: 0,
                            border: '1px solid var(--color-border)'
                          }}
                        >
                          {getMtnEmoji(g.id)}
                        </div>

                        {/* Details */}
                        <div style={{ flex: 1, minWidth: 0 }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                            <div className="heading-sm" style={{ fontWeight: 700 }}>
                              {g.nama}
                            </div>
                            <span className="badge badge-green" style={{ fontSize: 11, flexShrink: 0 }}>
                              {g.ketinggianMdpl?.toLocaleString()} mdpl
                            </span>
                          </div>

                          <div className="body-sm" style={{ marginTop: 2 }}>
                            📍 {g.lokasi}
                          </div>

                          {g.kontakBasecamp && (
                            <div className="body-sm" style={{ marginTop: 2, color: 'var(--color-text-2)' }}>
                              📞 {g.kontakBasecamp}
                            </div>
                          )}

                          {g.latitude != null && g.longitude != null && (
                            <div className="body-sm" style={{ marginTop: 2, fontFamily: 'monospace', fontSize: 11 }}>
                              🌐 {g.latitude.toFixed(4)}°, {g.longitude.toFixed(4)}°
                            </div>
                          )}

                          <div style={{ marginTop: 8, display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                            <span className="badge badge-blue" style={{ fontSize: 10 }}>
                              {g.jumlahJalur || 0} jalur
                            </span>
                          </div>

                          {/* Action Buttons */}
                          <div
                            style={{
                              marginTop: 12,
                              paddingTop: 10,
                              borderTop: '1px solid var(--color-border)',
                              display: 'flex',
                              gap: 8,
                              justifyContent: 'flex-end',
                              flexWrap: 'wrap'
                            }}
                          >
                            <button
                              className="btn btn-secondary btn-sm"
                              onClick={() => navigate(`/gunung/${g.id}`)}
                              style={{ padding: '6px 12px', fontSize: 12 }}
                            >
                              👁️ Lihat
                            </button>
                            <button
                              className="btn btn-secondary btn-sm"
                              onClick={() => navigate('/admin/jalur')}
                              style={{ padding: '6px 12px', fontSize: 12 }}
                              title="Kelola jalur & GPX untuk gunung ini"
                            >
                              🗺️ Kelola Jalur
                            </button>
                            <button
                              className="btn btn-secondary btn-sm"
                              onClick={() => startEdit(g)}
                              style={{ padding: '6px 12px', fontSize: 12 }}
                            >
                              ✏️ Edit
                            </button>
                            <button
                              className="btn btn-danger btn-sm"
                              onClick={() => handleDelete(g)}
                              style={{ padding: '6px 12px', fontSize: 12 }}
                            >
                              🗑️ Hapus
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* TAB 2: TAMBAH / EDIT GUNUNG */}
        {activeTab === 'tambah' && (
          <form onSubmit={handleSubmit} className="card">
            <div className="card-body">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 }}>
                <div className="heading-sm">
                  {editingGunung ? `Edit Data: ${editingGunung.nama}` : 'Tambah Master Gunung Baru'}
                </div>
                {editingGunung && (
                  <button
                    type="button"
                    onClick={() => {
                      resetForm();
                      setActiveTab('daftar');
                    }}
                    style={{ fontSize: 12, color: 'var(--color-text-muted)', cursor: 'pointer' }}
                  >
                    Batal Edit
                  </button>
                )}
              </div>

              <p className="body-sm" style={{ marginBottom: 16 }}>
                {editingGunung
                  ? 'Perbarui informasi detail gunung di bawah ini.'
                  : 'Lengkapi formulir berikut untuk menambahkan gunung baru ke dalam katalog KawanNaik.'}
              </p>

              {/* Nama Gunung */}
              <div className="form-group">
                <label className="form-label">
                  Nama Gunung <span style={{ color: 'var(--color-red)' }}>*</span>
                </label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="Contoh: Gunung Sindoro, Gunung Slamet"
                  value={formData.nama}
                  onChange={e => setFormData(p => ({ ...p, nama: e.target.value }))}
                  maxLength={100}
                  required
                />
              </div>

              {/* Lokasi */}
              <div className="form-group">
                <label className="form-label">
                  Lokasi / Provinsi <span style={{ color: 'var(--color-red)' }}>*</span>
                </label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="Contoh: Wonosobo & Temanggung, Jawa Tengah"
                  value={formData.lokasi}
                  onChange={e => setFormData(p => ({ ...p, lokasi: e.target.value }))}
                  maxLength={255}
                  required
                />
              </div>

              {/* Ketinggian */}
              <div className="form-group">
                <label className="form-label">
                  Ketinggian (mdpl) <span style={{ color: 'var(--color-red)' }}>*</span>
                </label>
                <input
                  type="number"
                  className="form-input"
                  placeholder="Contoh: 3153"
                  value={formData.ketinggianMdpl}
                  onChange={e => setFormData(p => ({ ...p, ketinggianMdpl: e.target.value }))}
                  min={1}
                  required
                />
              </div>

              {/* Kontak Basecamp */}
              <div className="form-group">
                <label className="form-label">
                  Kontak Basecamp / Pengelola
                </label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="Contoh: 081234567890 / Basecamp Kledung"
                  value={formData.kontakBasecamp}
                  onChange={e => setFormData(p => ({ ...p, kontakBasecamp: e.target.value }))}
                  maxLength={50}
                />
              </div>

              {/* Koordinat GPS */}
              <div className="form-group">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 }}>
                  <label className="form-label" style={{ marginBottom: 0 }}>
                    Koordinat Geografis (Opsional)
                  </label>
                  <button
                    type="button"
                    onClick={handleGetGps}
                    disabled={fetchingGps}
                    style={{
                      fontSize: 11,
                      fontWeight: 600,
                      color: 'var(--color-primary)',
                      background: 'none',
                      border: 'none',
                      cursor: 'pointer',
                      display: 'flex',
                      alignItems: 'center',
                      gap: 4
                    }}
                  >
                    {fetchingGps ? '📡 Mengambil...' : '📡 Ambil GPS Saya'}
                  </button>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
                  <div>
                    <input
                      type="number"
                      step="any"
                      className="form-input"
                      placeholder="Latitude (-7.xxx)"
                      value={formData.latitude}
                      onChange={e => setFormData(p => ({ ...p, latitude: e.target.value }))}
                    />
                  </div>
                  <div>
                    <input
                      type="number"
                      step="any"
                      className="form-input"
                      placeholder="Longitude (110.xxx)"
                      value={formData.longitude}
                      onChange={e => setFormData(p => ({ ...p, longitude: e.target.value }))}
                    />
                  </div>
                </div>
              </div>

              {/* Submit Buttons */}
              <div style={{ display: 'flex', gap: 10, marginTop: 20 }}>
                {editingGunung && (
                  <button
                    type="button"
                    className="btn btn-secondary"
                    style={{ flex: 1 }}
                    onClick={() => {
                      resetForm();
                      setActiveTab('daftar');
                    }}
                  >
                    Batal
                  </button>
                )}
                <button
                  type="submit"
                  className="btn btn-primary"
                  style={{ flex: 2 }}
                  disabled={submitting}
                >
                  {submitting ? (
                    <>
                      <span className="spinner" style={{ width: 16, height: 16, borderWidth: 2 }} />
                      Menyimpan...
                    </>
                  ) : editingGunung ? (
                    'Simpan Perubahan ⛰️'
                  ) : (
                    'Tambah Gunung 🏔️'
                  )}
                </button>
              </div>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
