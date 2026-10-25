import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { gunungApi, jalurApi, titikJalurApi } from '../lib/api';
import { useToast } from '../hooks/useToast';
import { getErrMsg } from '../utils/helpers';

const JENIS_TITIK = [
  { val: 'BASECAMP', label: '🏠 Basecamp' },
  { val: 'POS', label: '⛺ Pos Pendakian' },
  { val: 'SUMBER_AIR', label: '💧 Sumber Air' },
  { val: 'TITIK_RAWAN', label: '⚠️ Titik Rawan' }
];

export default function AdminJalurPage() {
  const navigate = useNavigate();
  const { toast } = useToast();

  const [activeTab, setActiveTab] = useState('gpx'); // 'gpx' | 'jalur' | 'waypoint'
  const [gunung, setGunung] = useState([]);
  const [jalur, setJalur] = useState([]);
  const [titikList, setTitikList] = useState([]);
  const [gunungId, setGunungId] = useState('');
  const [jalurId, setJalurId] = useState('');

  // GPX State
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);

  // New Jalur Form
  const [newJalur, setNewJalur] = useState({
    namaJalur: '',
    jarakKm: '',
    tingkatKesulitan: 'SEDANG',
    estimasiJam: '',
    levelMinimum: 'PEMULA'
  });
  const [savingJalur, setSavingJalur] = useState(false);

  // New Titik Form
  const [newTitik, setNewTitik] = useState({
    nama: '',
    jenis: 'POS',
    urutan: 1,
    latitude: '',
    longitude: '',
    deskripsi: ''
  });
  const [savingTitik, setSavingTitik] = useState(false);
  const [fetchingGps, setFetchingGps] = useState(false);

  useEffect(() => {
    gunungApi.getAll().then(r => setGunung(r.data.data || []));
  }, []);

  const loadJalur = (gId) => {
    if (!gId) {
      setJalur([]);
      setJalurId('');
      setTitikList([]);
      return;
    }
    jalurApi.getByGunung(gId).then(r => {
      const list = r.data.data || [];
      setJalur(list);
      if (list.length > 0 && !jalurId) {
        setJalurId(String(list[0].id));
      }
    });
  };

  useEffect(() => {
    loadJalur(gunungId);
  }, [gunungId]);

  const loadTitik = (jId) => {
    if (!jId) {
      setTitikList([]);
      return;
    }
    titikJalurApi.getByJalur(jId).then(r => {
      const list = r.data.data || [];
      setTitikList(list);
      setNewTitik(prev => ({ ...prev, urutan: list.length + 1 }));
    });
  };

  useEffect(() => {
    loadTitik(jalurId);
  }, [jalurId]);

  // Handler: GPX Upload
  const handleUploadGpx = async (e) => {
    e.preventDefault();
    if (!jalurId || !file) return toast('Pilih jalur dan file GPX terlebih dahulu', 'error');
    setUploading(true);
    try {
      const data = new FormData();
      data.append('file', file);
      const res = await apiUploadGpx(jalurId, data);
      toast(`${res.data.data} titik waypoint berhasil diimport dari GPX! 🗺️`, 'success');
      setFile(null);
      e.target.reset();
      loadTitik(jalurId);
    } catch (err) {
      toast(getErrMsg(err), 'error');
    } finally {
      setUploading(false);
    }
  };

  // Handler: Tambah Jalur Baru
  const handleCreateJalur = async (e) => {
    e.preventDefault();
    if (!gunungId) return toast('Pilih gunung terlebih dahulu', 'error');
    if (!newJalur.namaJalur.trim()) return toast('Nama jalur wajib diisi', 'error');
    setSavingJalur(true);
    try {
      const payload = {
        gunungId: Number(gunungId),
        namaJalur: newJalur.namaJalur.trim(),
        jarakKm: newJalur.jarakKm ? parseFloat(newJalur.jarakKm) : null,
        tingkatKesulitan: newJalur.tingkatKesulitan,
        estimasiJam: newJalur.estimasiJam ? parseFloat(newJalur.estimasiJam) : 0,
        levelMinimum: newJalur.levelMinimum
      };
      await jalurApi.create(payload);
      toast('Jalur baru berhasil ditambahkan! 🏔️', 'success');
      setNewJalur({
        namaJalur: '',
        jarakKm: '',
        tingkatKesulitan: 'SEDANG',
        estimasiJam: '',
        levelMinimum: 'PEMULA'
      });
      loadJalur(gunungId);
      setActiveTab('gpx');
    } catch (err) {
      toast(getErrMsg(err), 'error');
    } finally {
      setSavingJalur(false);
    }
  };

  // Handler: Tambah Titik Pos
  const handleCreateTitik = async (e) => {
    e.preventDefault();
    if (!jalurId) return toast('Pilih jalur terlebih dahulu', 'error');
    if (!newTitik.nama.trim()) return toast('Nama titik/pos wajib diisi', 'error');
    setSavingTitik(true);
    try {
      const payload = {
        jalurId: Number(jalurId),
        jenis: newTitik.jenis,
        nama: newTitik.nama.trim(),
        urutan: Number(newTitik.urutan),
        latitude: newTitik.latitude ? parseFloat(newTitik.latitude) : null,
        longitude: newTitik.longitude ? parseFloat(newTitik.longitude) : null,
        deskripsi: newTitik.deskripsi.trim()
      };
      await titikJalurApi.create(payload);
      toast('Titik pos berhasil ditambahkan! 📍', 'success');
      setNewTitik(prev => ({
        nama: '',
        jenis: 'POS',
        urutan: prev.urutan + 1,
        latitude: '',
        longitude: '',
        deskripsi: ''
      }));
      loadTitik(jalurId);
    } catch (err) {
      toast(getErrMsg(err), 'error');
    } finally {
      setSavingTitik(false);
    }
  };

  // Handler: Hapus Titik
  const handleDeleteTitik = async (id, nama) => {
    if (!window.confirm(`Hapus titik "${nama}"?`)) return;
    try {
      await titikJalurApi.remove(id);
      toast('Titik pos berhasil dihapus', 'info');
      loadTitik(jalurId);
    } catch (err) {
      toast(getErrMsg(err), 'error');
    }
  };

  // Handler: Ambil GPS perangkat
  const handleGetGps = () => {
    if (!navigator.geolocation) {
      return toast('Perangkat tidak mendukung geolokasi GPS', 'error');
    }
    setFetchingGps(true);
    navigator.geolocation.getCurrentPosition(
      pos => {
        setNewTitik(prev => ({
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
        >
          ←
        </button>
        <div>
          <div className="app-bar-title">Kelola Jalur & Waypoint</div>
          <div className="app-bar-subtitle">Panel Admin KawanNaik</div>
        </div>
      </div>

      <div className="container" style={{ paddingTop: 16 }}>
        {/* Selector Gunung & Jalur */}
        <div className="card" style={{ marginBottom: 16 }}>
          <div className="card-body">
            <div className="form-group">
              <label className="form-label">Pilih Gunung</label>
              <select
                className="form-select"
                value={gunungId}
                onChange={e => setGunungId(e.target.value)}
              >
                <option value="">-- Pilih Gunung --</option>
                {gunung.map(g => (
                  <option key={g.id} value={g.id}>
                    {g.nama} ({g.lokasi})
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group" style={{ marginBottom: 0 }}>
              <label className="form-label">Pilih Jalur</label>
              <select
                className="form-select"
                value={jalurId}
                onChange={e => setJalurId(e.target.value)}
                disabled={!gunungId}
              >
                <option value="">-- Pilih Jalur --</option>
                {jalur.map(j => (
                  <option key={j.id} value={j.id}>
                    {j.namaJalur} ({j.tingkatKesulitan} &bull; {j.estimasiJam} jam)
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* Tab Buttons */}
        <div
          style={{
            display: 'flex',
            gap: 8,
            marginBottom: 16,
            background: 'var(--color-surface)',
            padding: 4,
            borderRadius: 'var(--radius-md)',
            border: '1px solid var(--color-border)'
          }}
        >
          {[
            { id: 'gpx', label: '📥 Import GPX' },
            { id: 'jalur', label: '➕ Tambah Jalur' },
            { id: 'waypoint', label: '📍 Titik Pos' }
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              style={{
                flex: 1,
                padding: '8px 4px',
                borderRadius: 'var(--radius-sm)',
                fontSize: 12,
                fontWeight: 600,
                background: activeTab === tab.id ? 'var(--color-primary)' : 'transparent',
                color: activeTab === tab.id ? '#ffffff' : 'var(--color-text-muted)',
                cursor: 'pointer',
                transition: 'all 0.2s',
                textAlign: 'center'
              }}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Tab Content 1: GPX Import */}
        {activeTab === 'gpx' && (
          <form onSubmit={handleUploadGpx} className="card">
            <div className="card-body">
              <div className="heading-sm" style={{ marginBottom: 6 }}>Import Track GPX</div>
              <p className="body-sm" style={{ marginBottom: 14 }}>
                Unggah file rute pendakian <code>.gpx</code> dari smartwatch/GPS untuk membuat polyline dan trackpoint otomatis di peta.
              </p>

              <div className="form-group">
                <label className="form-label">File GPX (.gpx)</label>
                <input
                  className="form-input"
                  type="file"
                  accept=".gpx,application/gpx+xml"
                  onChange={e => setFile(e.target.files[0] || null)}
                />
              </div>

              <button
                type="submit"
                className="btn btn-primary btn-block"
                disabled={uploading || !jalurId || !file}
              >
                {uploading ? (
                  <>
                    <span className="spinner" style={{ width: 16, height: 16, borderWidth: 2 }} />
                    Mengimport file GPX...
                  </>
                ) : (
                  'Mulai Import GPX'
                )}
              </button>
            </div>
          </form>
        )}

        {/* Tab Content 2: Tambah Jalur Baru */}
        {activeTab === 'jalur' && (
          <form onSubmit={handleCreateJalur} className="card">
            <div className="card-body">
              <div className="heading-sm" style={{ marginBottom: 6 }}>Tambah Master Jalur Baru</div>
              <p className="body-sm" style={{ marginBottom: 14 }}>
                Tambahkan jalur pendakian baru untuk gunung yang dipilih.
              </p>

              <div className="form-group">
                <label className="form-label">Nama Jalur</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="Contoh: Via Ranu Pane, Via Selo"
                  value={newJalur.namaJalur}
                  onChange={e => setNewJalur(p => ({ ...p, namaJalur: e.target.value }))}
                  required
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
                <div className="form-group">
                  <label className="form-label">Jarak (Km)</label>
                  <input
                    type="number"
                    step="0.1"
                    className="form-input"
                    placeholder="Contoh: 14.5"
                    value={newJalur.jarakKm}
                    onChange={e => setNewJalur(p => ({ ...p, jarakKm: e.target.value }))}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Estimasi (Jam)</label>
                  <input
                    type="number"
                    step="0.5"
                    className="form-input"
                    placeholder="Contoh: 7.5"
                    value={newJalur.estimasiJam}
                    onChange={e => setNewJalur(p => ({ ...p, estimasiJam: e.target.value }))}
                    required
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
                <div className="form-group">
                  <label className="form-label">Tingkat Kesulitan</label>
                  <select
                    className="form-select"
                    value={newJalur.tingkatKesulitan}
                    onChange={e => setNewJalur(p => ({ ...p, tingkatKesulitan: e.target.value }))}
                  >
                    <option value="MUDAH">Mudah</option>
                    <option value="SEDANG">Sedang</option>
                    <option value="SULIT">Sulit</option>
                    <option value="EKSTREM">Ekstrem</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Level Fisik Min</label>
                  <select
                    className="form-select"
                    value={newJalur.levelMinimum}
                    onChange={e => setNewJalur(p => ({ ...p, levelMinimum: e.target.value }))}
                  >
                    <option value="PEMULA">Pemula</option>
                    <option value="MENENGAH">Menengah</option>
                    <option value="MAHIR">Mahir</option>
                  </select>
                </div>
              </div>

              <button
                type="submit"
                className="btn btn-primary btn-block"
                disabled={savingJalur || !gunungId}
              >
                {savingJalur ? 'Menyimpan Jalur...' : 'Simpan Jalur Pendakian'}
              </button>
            </div>
          </form>
        )}

        {/* Tab Content 3: Tambah Titik Waypoint */}
        {activeTab === 'waypoint' && (
          <>
            <form onSubmit={handleCreateTitik} className="card" style={{ marginBottom: 16 }}>
              <div className="card-body">
                <div className="heading-sm" style={{ marginBottom: 6 }}>Tambah Titik Pos / Waypoint</div>
                <p className="body-sm" style={{ marginBottom: 14 }}>
                  Tambahkan pos istirahat, sumber air, atau titik rawan pada jalur ini.
                </p>

                <div style={{ display: 'grid', gridTemplateColumns: '80px 1fr', gap: 10 }}>
                  <div className="form-group">
                    <label className="form-label">Urutan</label>
                    <input
                      type="number"
                      className="form-input"
                      value={newTitik.urutan}
                      onChange={e => setNewTitik(p => ({ ...p, urutan: e.target.value }))}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Nama Titik / Pos</label>
                    <input
                      type="text"
                      className="form-input"
                      placeholder="Contoh: Pos 1 Watu Rejeng"
                      value={newTitik.nama}
                      onChange={e => setNewTitik(p => ({ ...p, nama: e.target.value }))}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Jenis Titik</label>
                  <select
                    className="form-select"
                    value={newTitik.jenis}
                    onChange={e => setNewTitik(p => ({ ...p, jenis: e.target.value }))}
                  >
                    {JENIS_TITIK.map(jt => (
                      <option key={jt.val} value={jt.val}>{jt.label}</option>
                    ))}
                  </select>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
                  <div className="form-group">
                    <label className="form-label">Latitude</label>
                    <input
                      type="number"
                      step="any"
                      className="form-input"
                      placeholder="-8.123456"
                      value={newTitik.latitude}
                      onChange={e => setNewTitik(p => ({ ...p, latitude: e.target.value }))}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Longitude</label>
                    <input
                      type="number"
                      step="any"
                      className="form-input"
                      placeholder="112.987654"
                      value={newTitik.longitude}
                      onChange={e => setNewTitik(p => ({ ...p, longitude: e.target.value }))}
                    />
                  </div>
                </div>

                <button
                  type="button"
                  className="btn btn-ghost btn-sm"
                  onClick={handleGetGps}
                  disabled={fetchingGps}
                  style={{ marginBottom: 14, width: '100%', justifyContent: 'center' }}
                >
                  {fetchingGps ? 'Mengambil GPS...' : '📍 Gunakan Koordinat Saat Ini (GPS)'}
                </button>

                <div className="form-group">
                  <label className="form-label">Keterangan / Deskripsi (Opsional)</label>
                  <input
                    type="text"
                    className="form-input"
                    placeholder="Contoh: Shelter luas, terdapat shelter air mengalir"
                    value={newTitik.deskripsi}
                    onChange={e => setNewTitik(p => ({ ...p, deskripsi: e.target.value }))}
                  />
                </div>

                <button
                  type="submit"
                  className="btn btn-primary btn-block"
                  disabled={savingTitik || !jalurId}
                >
                  {savingTitik ? 'Menyimpan Titik...' : '+ Tambahkan Titik Pos'}
                </button>
              </div>
            </form>

            {/* List existing waypoints */}
            <div className="heading-sm" style={{ marginBottom: 10 }}>
              Titik Waypoint Terdaftar ({titikList.length})
            </div>
            {titikList.length === 0 ? (
              <div className="card">
                <div className="card-body" style={{ textAlign: 'center', color: 'var(--color-text-muted)' }}>
                  Belum ada titik waypoint untuk jalur ini.
                </div>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginBottom: 20 }}>
                {titikList
                  .sort((a, b) => a.urutan - b.urutan)
                  .map(t => (
                    <div key={t.id} className="card">
                      <div
                        className="card-body"
                        style={{
                          padding: 12,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'space-between'
                        }}
                      >
                        <div>
                          <div style={{ fontWeight: 600, fontSize: 14 }}>
                            {t.urutan}. {t.nama}
                          </div>
                          <div className="body-sm" style={{ marginTop: 2 }}>
                            {t.jenis} &bull; {t.latitude || '-'}, {t.longitude || '-'}
                          </div>
                          {t.deskripsi && (
                            <div className="body-sm" style={{ marginTop: 2, color: 'var(--color-text-muted)' }}>
                              {t.deskripsi}
                            </div>
                          )}
                        </div>
                        <button
                          className="btn btn-ghost btn-sm"
                          onClick={() => handleDeleteTitik(t.id, t.nama)}
                          style={{ color: 'var(--color-red)' }}
                        >
                          🗑️
                        </button>
                      </div>
                    </div>
                  ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

async function apiUploadGpx(jalurId, data) {
  const token = localStorage.getItem('kn_token');
  const response = await fetch(`/api/v1/admin/jalur/${jalurId}/gpx`, {
    method: 'POST',
    body: data,
    headers: token ? { Authorization: `Bearer ${token}` } : {}
  });
  const body = await response.json();
  if (!response.ok) throw new Error(body.message || 'Import GPX gagal');
  return { data: body };
}
