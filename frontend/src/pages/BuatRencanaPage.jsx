// src/pages/BuatRencanaPage.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { rencanaApi, gunungApi, jalurApi } from '../lib/api';
import { useToast } from '../hooks/useToast';
import { getErrMsg, formatRupiah, levelLabel, levelColor, formatDate } from '../utils/helpers';

const GEAR_GROUPS = [
  {
    category: '⛺ Shelter & Istirahat',
    items: ['Tenda', 'Sleeping Bag', 'Matras Foil', 'Bantal Tiup']
  },
  {
    category: '🧥 Pakaian & Proteksi',
    items: ['Jaket Gunung', 'Jas Hujan', 'Sepatu Trekking', 'Sarung Tangan', 'Pakaian Ganti']
  },
  {
    category: '🔦 Esensial & Navigasi',
    items: ['Headlamp + Baterai', 'P3K & Obat Pribadi', 'Trash Bag', 'Powerbank']
  },
  {
    category: '🍳 Masak & Konsumsi',
    items: ['Nesting & Kompor', 'Air Minum min 2L', 'Logistik Pribadi', 'Alat Makan']
  }
];

const PRESET_WAKTU = [
  { label: '🌅 05:00 Subuh', val: '05:00 WIB' },
  { label: '☀️ 06:00 Pagi',  val: '06:00 WIB' },
  { label: '🌄 07:30 Pagi',  val: '07:30 WIB' },
  { label: '⛺ 20:00 Malam', val: '20:00 WIB' },
];

const PRESET_BIAYA = [
  { label: 'Gratis / Mandiri', val: 0 },
  { label: 'Rp 50.000',        val: 50000 },
  { label: 'Rp 100.000',       val: 100000 },
  { label: 'Rp 200.000',       val: 200000 },
  { label: 'Rp 250.000',       val: 250000 },
];

export default function BuatRencanaPage() {
  const navigate = useNavigate();
  const { toast } = useToast();

  const [gunung, setGunung]     = useState([]);
  const [jalur, setJalur]       = useState([]);
  const [loading, setLoading]   = useState(false);
  const [form, setForm] = useState({
    jalurId: '',
    tanggalNaik: '',
    waktuKumpul: '06:00 WIB',
    titikKumpul: '',
    perlengkapan: 'Tenda, Sleeping Bag, Matras Foil, Jaket Gunung, Headlamp + Baterai, Jas Hujan',
    estimasiBiaya: '',
    kontak: '',
    kuota: 6,
    levelMinimum: 'PEMULA',
    catatan: ''
  });
  const [selectedGunung, setSelectedGunung] = useState('');
  const [errors, setErrors] = useState({});

  useEffect(() => {
    gunungApi.getAll().then(r => {
      const list = r.data.data || [];
      setGunung(list);
      if (list.length > 0) {
        setSelectedGunung(String(list[0].id));
      }
    });
  }, []);

  useEffect(() => {
    if (!selectedGunung) { setJalur([]); setForm(p => ({ ...p, jalurId: '' })); return; }
    jalurApi.getByGunung(selectedGunung).then(r => {
      const list = r.data.data || [];
      setJalur(list);
      if (list.length > 0) setForm(p => ({ ...p, jalurId: String(list[0].id) }));
    });
  }, [selectedGunung]);

  const set = (k, v) => setForm(p => ({ ...p, [k]: v }));

  const toggleAlat = (alat) => {
    const currentList = form.perlengkapan
      ? form.perlengkapan.split(',').map(s => s.trim()).filter(Boolean)
      : [];
    let updated;
    if (currentList.some(item => item.toLowerCase() === alat.toLowerCase())) {
      updated = currentList.filter(item => item.toLowerCase() !== alat.toLowerCase());
    } else {
      updated = [...currentList, alat];
    }
    set('perlengkapan', updated.join(', '));
  };

  const isAlatSelected = (alat) => {
    if (!form.perlengkapan) return false;
    const currentList = form.perlengkapan.split(',').map(s => s.trim().toLowerCase());
    return currentList.includes(alat.toLowerCase());
  };

  const selectedGearCount = form.perlengkapan
    ? form.perlengkapan.split(',').map(s => s.trim()).filter(Boolean).length
    : 0;

  const validate = () => {
    const e = {};
    if (!form.jalurId)     e.jalurId = 'Pilih jalur terlebih dahulu';
    if (!form.tanggalNaik) e.tanggalNaik = 'Tanggal naik wajib ditentukan';
    if (form.kuota < 2 || form.kuota > 20) e.kuota = 'Kuota peserta antara 2–20 orang';
    setErrors(e);
    return !Object.keys(e).length;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) {
      toast('Harap lengkapi formulir yang diperlukan', 'error');
      return;
    }
    setLoading(true);
    try {
      const payload = {
        ...form,
        jalurId: Number(form.jalurId),
        kuota: Number(form.kuota),
        estimasiBiaya: form.estimasiBiaya !== '' ? Number(form.estimasiBiaya) : null,
        waktuKumpul: form.waktuKumpul.trim() || null,
        titikKumpul: form.titikKumpul.trim() || null,
        perlengkapan: form.perlengkapan.trim() || null,
        kontak: form.kontak.trim() || null,
        catatan: form.catatan.trim() || null,
      };
      const res = await rencanaApi.create(payload);
      toast('Jadwal open trip berhasil diterbitkan! 🏔️', 'success');
      navigate(`/rencana/${res.data.data.id}`);
    } catch (err) {
      toast(getErrMsg(err), 'error');
    } finally {
      setLoading(false);
    }
  };

  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const minDate = tomorrow.toISOString().split('T')[0];

  const currentGunungObj = gunung.find(g => String(g.id) === String(selectedGunung));
  const currentJalurObj = jalur.find(j => String(j.id) === String(form.jalurId));

  return (
    <div className="page page-enter" style={{ background: 'var(--color-bg)' }}>
      {/* Visual Header Banner */}
      <div className="trip-header-banner">
        <button
          type="button"
          onClick={() => navigate(-1)}
          style={{
            display: 'inline-flex', alignItems: 'center', gap: 6,
            background: 'rgba(255,255,255,0.15)', border: '1px solid rgba(255,255,255,0.25)',
            borderRadius: 'var(--radius-full)', color: '#ffffff',
            padding: '6px 14px', fontSize: 13, fontWeight: 600, cursor: 'pointer', marginBottom: 14
          }}
        >
          ← Kembali
        </button>

        <div className="trip-header-badge">🏔️ EXPEDITION PLANNER</div>
        <h1 style={{ fontSize: 24, fontWeight: 800, color: '#ffffff', margin: 0, letterSpacing: '-0.02em' }}>
          Buka Jadwal Open Trip
        </h1>
        <p style={{ fontSize: 13, color: 'rgba(255,255,255,0.85)', marginTop: 6, lineHeight: 1.5 }}>
          Susun detail pendakian dengan jelas: waktu, meeting point, perlengkapan, dan ajak kawan mendaki bersama.
        </p>
      </div>

      <div className="container" style={{ paddingTop: 20, paddingBottom: 48 }}>
        <form onSubmit={handleSubmit}>
          {/* Step 1: Lokasi & Jalur */}
          <div className="card" style={{ marginBottom: 16 }}>
            <div className="card-body">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 14 }}>
                <span style={{ fontSize: 20 }}>📍</span>
                <div>
                  <div className="heading-sm">Destinasi Gunung & Jalur</div>
                  <div className="body-sm" style={{ color: 'var(--color-text-muted)' }}>Pilih target puncak dan rute pendakian</div>
                </div>
              </div>

              {/* Gunung selector */}
              <div className="form-group">
                <label className="form-label">Gunung Tujuan</label>
                <select className="form-select" value={selectedGunung}
                  onChange={e => setSelectedGunung(e.target.value)}>
                  {gunung.map(g => (
                    <option key={g.id} value={g.id}>
                      {g.nama} {g.ketinggianMdpl ? `(${g.ketinggianMdpl.toLocaleString('id-ID')} mdpl)` : ''}
                    </option>
                  ))}
                </select>
                {currentGunungObj && (
                  <div style={{ marginTop: 6, fontSize: 12, color: 'var(--color-text-muted)', display: 'flex', gap: 8 }}>
                    <span>📍 {currentGunungObj.lokasi}</span>
                    <span>•</span>
                    <span>⛰️ {currentGunungObj.ketinggianMdpl?.toLocaleString('id-ID')} mdpl</span>
                  </div>
                )}
              </div>

              {/* Jalur selector */}
              <div className="form-group" style={{ marginBottom: 0 }}>
                <label className="form-label">Jalur Pendakian</label>
                {jalur.length === 0 ? (
                  <div style={{ padding: 14, background: 'var(--color-surface-2)', border: '1px solid var(--color-border)',
                    borderRadius: 'var(--radius-md)', fontSize: 13, color: 'var(--color-text-muted)' }}>
                    Memuat jalur pendakian...
                  </div>
                ) : (
                  <select className="form-select" value={form.jalurId}
                    onChange={e => set('jalurId', e.target.value)}>
                    {jalur.map(j => (
                      <option key={j.id} value={j.id}>
                        {j.namaJalur} — {j.tingkatKesulitan} ({j.estimasiJam} jam)
                      </option>
                    ))}
                  </select>
                )}
                {errors.jalurId && <span className="form-error">{errors.jalurId}</span>}

                {currentJalurObj && (
                  <div style={{
                    marginTop: 10, padding: 10, background: 'var(--color-surface-2)',
                    border: '1px solid var(--color-border)', borderRadius: 'var(--radius-md)',
                    display: 'flex', alignItems: 'center', justifyContent: 'space-between'
                  }}>
                    <span className="body-sm" style={{ fontWeight: 600 }}>Tingkat Kesulitan:</span>
                    <span className={`badge ${levelColor(currentJalurObj.levelMinimum)}`}>
                      {currentJalurObj.tingkatKesulitan} • Min: {levelLabel(currentJalurObj.levelMinimum)}
                    </span>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Step 2: Waktu & Titik Kumpul */}
          <div className="card" style={{ marginBottom: 16 }}>
            <div className="card-body">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 14 }}>
                <span style={{ fontSize: 20 }}>⏰</span>
                <div>
                  <div className="heading-sm">Jadwal & Meeting Point</div>
                  <div className="body-sm" style={{ color: 'var(--color-text-muted)' }}>Kapan dan di mana anggota tim akan berkumpul</div>
                </div>
              </div>

              {/* Tanggal Naik */}
              <div className="form-group">
                <label className="form-label">Tanggal Mulai Pendakian</label>
                <input type="date" className="form-input" value={form.tanggalNaik}
                  min={minDate}
                  onChange={e => set('tanggalNaik', e.target.value)} />
                {errors.tanggalNaik && <span className="form-error">{errors.tanggalNaik}</span>}
              </div>

              {/* Jam Kumpul */}
              <div className="form-group">
                <label className="form-label">Jam / Waktu Kumpul</label>
                <input type="text" className="form-input"
                  placeholder="Misal: 06:00 WIB di Basecamp"
                  value={form.waktuKumpul}
                  onChange={e => set('waktuKumpul', e.target.value)} />
                <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginTop: 8 }}>
                  {PRESET_WAKTU.map(p => (
                    <button type="button" key={p.val}
                      onClick={() => set('waktuKumpul', p.val)}
                      style={{
                        padding: '5px 12px', borderRadius: 'var(--radius-full)', fontSize: 11, fontWeight: 600,
                        background: form.waktuKumpul === p.val ? '#e7f5ee' : 'var(--color-surface-2)',
                        border: `1px solid ${form.waktuKumpul === p.val ? 'var(--color-primary)' : 'var(--color-border)'}`,
                        color: form.waktuKumpul === p.val ? 'var(--color-primary)' : 'var(--color-text-2)',
                        cursor: 'pointer'
                      }}>
                      {p.label}
                    </button>
                  ))}
                </div>
              </div>

              {/* Titik Kumpul */}
              <div className="form-group" style={{ marginBottom: 0 }}>
                <label className="form-label">Titik Kumpul (Meeting Point)</label>
                <input type="text" className="form-input"
                  placeholder="Contoh: Basecamp Selo / Stasiun Malang Kota Baru (Pintu Timur)"
                  value={form.titikKumpul}
                  onChange={e => set('titikKumpul', e.target.value)} />
                <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginTop: 8 }}>
                  {['Basecamp Utama Jalur', 'Stasiun Kereta Terdekat', 'Terminal Bus', 'Rest Area SPBU'].map(loc => (
                    <button type="button" key={loc}
                      onClick={() => set('titikKumpul', loc)}
                      style={{
                        padding: '4px 10px', borderRadius: 'var(--radius-full)', fontSize: 11,
                        background: 'var(--color-surface-2)', border: '1px solid var(--color-border)',
                        color: 'var(--color-text-muted)', cursor: 'pointer'
                      }}>
                      + {loc}
                    </button>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Step 3: Perlengkapan */}
          <div className="card" style={{ marginBottom: 16 }}>
            <div className="card-body">
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <span style={{ fontSize: 20 }}>🎒</span>
                  <div>
                    <div className="heading-sm">Daftar Perlengkapan</div>
                    <div className="body-sm" style={{ color: 'var(--color-text-muted)' }}>Pilih alat wajib atau disarankan</div>
                  </div>
                </div>
                <span className="badge badge-green" style={{ fontSize: 11 }}>
                  {selectedGearCount} terpilih
                </span>
              </div>

              {GEAR_GROUPS.map(grp => (
                <div key={grp.category} style={{ marginBottom: 12 }}>
                  <div className="gear-category-title">{grp.category}</div>
                  <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                    {grp.items.map(item => {
                      const active = isAlatSelected(item);
                      return (
                        <div
                          key={item}
                          onClick={() => toggleAlat(item)}
                          className={`gear-interactive-chip${active ? ' active' : ''}`}
                        >
                          <span className="chip-check">{active ? '✓' : '+'}</span>
                          <span>{item}</span>
                        </div>
                      );
                    })}
                  </div>
                </div>
              ))}

              <div className="form-group" style={{ marginTop: 14, marginBottom: 0 }}>
                <label className="form-label" style={{ fontSize: 12 }}>Tulis / Edit Rincian Perlengkapan:</label>
                <textarea className="form-input" rows={3}
                  value={form.perlengkapan}
                  onChange={e => set('perlengkapan', e.target.value)}
                  placeholder="Pisahkan dengan koma (misal: Tenda dome, Sleeping bag, Headlamp...)"
                  style={{ resize: 'none', lineHeight: 1.6, fontSize: 13 }} />
              </div>
            </div>
          </div>

          {/* Step 4: Biaya & Narahubung */}
          <div className="card" style={{ marginBottom: 16 }}>
            <div className="card-body">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 14 }}>
                <span style={{ fontSize: 20 }}>💰</span>
                <div>
                  <div className="heading-sm">Estimasi Biaya & WhatsApp Host</div>
                  <div className="body-sm" style={{ color: 'var(--color-text-muted)' }}>Transparansi patungan dan kanal komunikasi tim</div>
                </div>
              </div>

              {/* Estimasi Biaya */}
              <div className="form-group">
                <label className="form-label">Estimasi Biaya Patungan (Rp / Orang)</label>
                <div style={{ position: 'relative' }}>
                  <span style={{
                    position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)',
                    fontWeight: 700, color: 'var(--color-text-muted)', fontSize: 14
                  }}>Rp</span>
                  <input
                    type="number"
                    className="form-input"
                    style={{ paddingLeft: 42 }}
                    placeholder="0 jika gratis atau patungan mandiri di lokasi"
                    min="0"
                    step="5000"
                    value={form.estimasiBiaya}
                    onChange={e => set('estimasiBiaya', e.target.value)}
                  />
                </div>
                <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginTop: 8 }}>
                  {PRESET_BIAYA.map(b => (
                    <button type="button" key={b.label}
                      onClick={() => set('estimasiBiaya', b.val)}
                      style={{
                        padding: '4px 10px', borderRadius: 'var(--radius-full)', fontSize: 11,
                        background: Number(form.estimasiBiaya) === b.val ? '#e7f5ee' : 'var(--color-surface-2)',
                        border: `1px solid ${Number(form.estimasiBiaya) === b.val ? 'var(--color-primary)' : 'var(--color-border)'}`,
                        color: Number(form.estimasiBiaya) === b.val ? 'var(--color-primary)' : 'var(--color-text-2)',
                        cursor: 'pointer'
                      }}>
                      {b.label}
                    </button>
                  ))}
                </div>
              </div>

              {/* Kontak WhatsApp */}
              <div className="form-group" style={{ marginBottom: 0 }}>
                <label className="form-label">Nomor WhatsApp Narahubung</label>
                <div style={{ position: 'relative' }}>
                  <span style={{
                    position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)',
                    fontSize: 16
                  }}>📱</span>
                  <input
                    type="text"
                    className="form-input"
                    style={{ paddingLeft: 42 }}
                    placeholder="Contoh: 081234567890 (peserta dapat langsung chat WhatsApp)"
                    value={form.kontak}
                    onChange={e => set('kontak', e.target.value)}
                  />
                </div>
                <div style={{ fontSize: 11, color: 'var(--color-text-muted)', marginTop: 4 }}>
                  Nomor ini akan menyediakan tombol satu-klik bagi peserta untuk menghubungi kamu.
                </div>
              </div>
            </div>
          </div>

          {/* Step 5: Kuota & Ketentuan */}
          <div className="card" style={{ marginBottom: 20 }}>
            <div className="card-body">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 14 }}>
                <span style={{ fontSize: 20 }}>👥</span>
                <div>
                  <div className="heading-sm">Ketentuan Peserta</div>
                  <div className="body-sm" style={{ color: 'var(--color-text-muted)' }}>Jumlah anggota dan kualifikasi fisik yang dibutuhkan</div>
                </div>
              </div>

              {/* Kuota Stepper */}
              <div className="form-group">
                <label className="form-label">Target Kuota Tim</label>
                <div style={{
                  display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                  background: 'var(--color-surface-2)', padding: '8px 16px', borderRadius: 'var(--radius-lg)',
                  border: '1px solid var(--color-border)'
                }}>
                  <button type="button" onClick={() => set('kuota', Math.max(2, form.kuota - 1))}
                    style={{
                      width: 40, height: 40, borderRadius: 'var(--radius-md)',
                      background: 'var(--color-surface)', border: '1px solid var(--color-border)',
                      fontSize: 18, fontWeight: 700, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center'
                    }}>-</button>
                  <div style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: 24, fontWeight: 800, color: 'var(--color-primary)' }}>{form.kuota} Orang</div>
                    <div style={{ fontSize: 11, color: 'var(--color-text-muted)' }}>Batas rombongan 2–20 pendaki</div>
                  </div>
                  <button type="button" onClick={() => set('kuota', Math.min(20, form.kuota + 1))}
                    style={{
                      width: 40, height: 40, borderRadius: 'var(--radius-md)',
                      background: 'var(--color-surface)', border: '1px solid var(--color-border)',
                      fontSize: 18, fontWeight: 700, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center'
                    }}>+</button>
                </div>
                {errors.kuota && <span className="form-error">{errors.kuota}</span>}
              </div>

              {/* Level Minimum */}
              <div className="form-group">
                <label className="form-label">Level Fisik Minimum</label>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
                  {[
                    { val: 'PEMULA', label: '🟢 Pemula', desc: 'Jalur landai / santai' },
                    { val: 'MENENGAH', label: '🟡 Menengah', desc: 'Tanjakan terjal' },
                    { val: 'MAHIR', label: '🔴 Mahir', desc: 'Teknis / ekstrem' },
                  ].map(({ val, label, desc }) => (
                    <button type="button" key={val} onClick={() => set('levelMinimum', val)}
                      style={{
                        padding: '10px 6px', borderRadius: 'var(--radius-md)', fontSize: 12, fontWeight: 600,
                        border: `2px solid ${form.levelMinimum === val ? 'var(--color-primary)' : 'var(--color-border)'}`,
                        background: form.levelMinimum === val ? '#e7f5ee' : 'var(--color-surface)',
                        color: form.levelMinimum === val ? 'var(--color-primary)' : 'var(--color-text)',
                        cursor: 'pointer', transition: 'all 0.15s', textAlign: 'center',
                        display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2
                      }}>
                      <span>{label}</span>
                      <span style={{ fontSize: 10, color: 'var(--color-text-muted)', fontWeight: 400 }}>{desc}</span>
                    </button>
                  ))}
                </div>
              </div>

              {/* Catatan Tambahan */}
              <div className="form-group" style={{ marginBottom: 0 }}>
                <label className="form-label">Catatan & Rencana Perjalanan (Opsional)</label>
                <textarea className="form-input" rows={3}
                  placeholder="Tuliskan estimasi jam istirahat, aturan kebersihan (Leave No Trace), atau kesepakatan rombongan..."
                  value={form.catatan}
                  onChange={e => set('catatan', e.target.value)}
                  style={{ resize: 'none', lineHeight: 1.6 }} />
              </div>
            </div>
          </div>

          {/* Live Preview Ticket */}
          <div style={{ marginBottom: 20 }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
              <span className="body-sm" style={{ fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.04em', color: 'var(--color-text-muted)' }}>
                🎫 Pratinjau Tiket Open Trip
              </span>
              <span className="badge badge-green">Live Preview</span>
            </div>

            <div className="trip-preview-ticket">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <div style={{ fontSize: 18, fontWeight: 800, color: 'var(--color-text)' }}>
                    🏔️ {currentGunungObj?.nama || 'Pilih Gunung'}
                  </div>
                  <div className="body-sm" style={{ color: 'var(--color-primary)', fontWeight: 600, marginTop: 2 }}>
                    Via {currentJalurObj?.namaJalur || 'Pilih Jalur'}
                  </div>
                </div>
                <span className={`badge ${levelColor(form.levelMinimum)}`}>
                  {levelLabel(form.levelMinimum)}
                </span>
              </div>

              <div style={{
                display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 8,
                margin: '14px 0', padding: '10px 0',
                borderTop: '1px dashed var(--color-border)', borderBottom: '1px dashed var(--color-border)'
              }}>
                <div className="body-sm">
                  <div style={{ color: 'var(--color-text-muted)', fontSize: 11 }}>📅 Tanggal & Waktu</div>
                  <div style={{ fontWeight: 700, marginTop: 2 }}>
                    {form.tanggalNaik ? formatDate(form.tanggalNaik) : 'Belum diatur'} • {form.waktuKumpul || '-'}
                  </div>
                </div>
                <div className="body-sm">
                  <div style={{ color: 'var(--color-text-muted)', fontSize: 11 }}>📍 Meeting Point</div>
                  <div style={{ fontWeight: 700, marginTop: 2 }} className="truncate">
                    {form.titikKumpul || 'Basecamp'}
                  </div>
                </div>
                <div className="body-sm">
                  <div style={{ color: 'var(--color-text-muted)', fontSize: 11 }}>💰 Estimasi Patungan</div>
                  <div style={{ fontWeight: 700, color: 'var(--color-primary)', marginTop: 2 }}>
                    {formatRupiah(form.estimasiBiaya)}
                  </div>
                </div>
                <div className="body-sm">
                  <div style={{ color: 'var(--color-text-muted)', fontSize: 11 }}>👥 Kuota Tim</div>
                  <div style={{ fontWeight: 700, marginTop: 2 }}>
                    {form.kuota} Pendaki
                  </div>
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: 12 }}>
                <span style={{ color: 'var(--color-text-muted)' }}>🎒 {selectedGearCount} perlengkapan siap</span>
                {form.kontak ? (
                  <span style={{ color: '#128C7E', fontWeight: 600 }}>💬 WhatsApp aktif</span>
                ) : (
                  <span style={{ color: 'var(--color-text-muted)' }}>Nomor WA opsional</span>
                )}
              </div>
            </div>
          </div>

          {/* Submit CTA */}
          <button
            id="create-rencana-submit"
            type="submit"
            className="btn btn-primary btn-block"
            disabled={loading || !form.jalurId}
            style={{
              padding: '16px 20px', fontSize: 16, fontWeight: 800, letterSpacing: '-0.01em',
              boxShadow: '0 4px 14px rgba(31, 122, 77, 0.35)'
            }}
          >
            {loading ? (
              <><span className="spinner" style={{ width: 20, height: 20, borderWidth: 2 }} /> Mempublikasikan...</>
            ) : (
              '🚀 Terbitkan Jadwal Open Trip'
            )}
          </button>
        </form>
      </div>
    </div>
  );
}
