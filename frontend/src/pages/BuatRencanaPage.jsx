// src/pages/BuatRencanaPage.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { rencanaApi, gunungApi, jalurApi } from '../lib/api';
import { useToast } from '../hooks/useToast';
import { getErrMsg } from '../utils/helpers';

export default function BuatRencanaPage() {
  const navigate = useNavigate();
  const { toast } = useToast();

  const [gunung, setGunung]     = useState([]);
  const [jalur, setJalur]       = useState([]);
  const [loading, setLoading]   = useState(false);
  const [form, setForm] = useState({
    jalurId: '', tanggalNaik: '', kuota: 4, levelMinimum: 'PEMULA', catatan: ''
  });
  const [selectedGunung, setSelectedGunung] = useState('');
  const [errors, setErrors] = useState({});

  useEffect(() => {
    gunungApi.getAll().then(r => setGunung(r.data.data || []));
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

  const validate = () => {
    const e = {};
    if (!form.jalurId)    e.jalurId    = 'Pilih jalur terlebih dahulu';
    if (!form.tanggalNaik) e.tanggalNaik = 'Tanggal naik wajib diisi';
    if (form.kuota < 2 || form.kuota > 20) e.kuota = 'Kuota antara 2-20 orang';
    setErrors(e);
    return !Object.keys(e).length;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    try {
      const payload = { ...form, jalurId: Number(form.jalurId), kuota: Number(form.kuota) };
      const res = await rencanaApi.create(payload);
      toast('Rencana pendakian berhasil dibuat! 🏔️', 'success');
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

  return (
    <div className="page page-enter">
      <div className="app-bar">
        <button onClick={() => navigate(-1)} style={{ background: 'none', border: 'none',
          color: 'var(--color-text-muted)', fontSize: 20, cursor: 'pointer', padding: '4px 8px 4px 0' }}>
          ←
        </button>
        <div className="app-bar-title">Buat Rencana Pendakian</div>
      </div>

      <div className="container" style={{ paddingTop: 20 }}>
        <form onSubmit={handleSubmit}>
          {/* Gunung selector */}
          <div className="form-group">
            <label className="form-label">Pilih Gunung</label>
            <select className="form-select" value={selectedGunung}
              onChange={e => setSelectedGunung(e.target.value)}>
              <option value="">-- Pilih Gunung --</option>
              {gunung.map(g => <option key={g.id} value={g.id}>{g.nama}</option>)}
            </select>
          </div>

          {/* Jalur selector */}
          <div className="form-group">
            <label className="form-label">Jalur Pendakian</label>
            {jalur.length === 0 ? (
              <div style={{ padding: 14, background: 'var(--color-surface)', border: '1.5px solid var(--color-border)',
                borderRadius: 'var(--radius-md)', fontSize: 14, color: 'var(--color-text-muted)' }}>
                {selectedGunung ? 'Tidak ada jalur untuk gunung ini' : 'Pilih gunung terlebih dahulu'}
              </div>
            ) : (
              <select className="form-select" value={form.jalurId}
                onChange={e => set('jalurId', e.target.value)}>
                <option value="">-- Pilih Jalur --</option>
                {jalur.map(j => <option key={j.id} value={j.id}>{j.namaJalur} ({j.tingkatKesulitan})</option>)}
              </select>
            )}
            {errors.jalurId && <span className="form-error">{errors.jalurId}</span>}
          </div>

          {/* Tanggal Naik */}
          <div className="form-group">
            <label className="form-label">Tanggal Naik</label>
            <input type="date" className="form-input" value={form.tanggalNaik}
              min={minDate}
              onChange={e => set('tanggalNaik', e.target.value)} />
            {errors.tanggalNaik && <span className="form-error">{errors.tanggalNaik}</span>}
          </div>

          {/* Kuota */}
          <div className="form-group">
            <label className="form-label">Kuota Peserta</label>
            <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
              <button type="button" onClick={() => set('kuota', Math.max(2, form.kuota - 1))}
                style={{ width: 44, height: 44, borderRadius: 'var(--radius-full)', background: 'var(--color-surface-2)',
                  border: '1.5px solid var(--color-border)', fontSize: 20, color: 'var(--color-text)', cursor: 'pointer' }}>
                -
              </button>
              <div style={{ flex: 1, textAlign: 'center', fontSize: 28, fontWeight: 800, color: 'var(--color-accent)' }}>
                {form.kuota}
              </div>
              <button type="button" onClick={() => set('kuota', Math.min(20, form.kuota + 1))}
                style={{ width: 44, height: 44, borderRadius: 'var(--radius-full)', background: 'var(--color-surface-2)',
                  border: '1.5px solid var(--color-border)', fontSize: 20, color: 'var(--color-text)', cursor: 'pointer' }}>
                +
              </button>
            </div>
            <div style={{ textAlign: 'center', fontSize: 12, color: 'var(--color-text-muted)', marginTop: 4 }}>orang (2–20)</div>
            {errors.kuota && <span className="form-error">{errors.kuota}</span>}
          </div>

          {/* Level Minimum */}
          <div className="form-group">
            <label className="form-label">Level Fisik Minimum</label>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
              {[
                { val: 'PEMULA', label: '🟢 Pemula' },
                { val: 'MENENGAH', label: '🟡 Menengah' },
                { val: 'MAHIR', label: '🔴 Mahir' },
              ].map(({ val, label }) => (
                <button type="button" key={val} onClick={() => set('levelMinimum', val)}
                  style={{
                    padding: '11px 4px', borderRadius: 'var(--radius-md)', fontSize: 12, fontWeight: 600,
                    border: `2px solid ${form.levelMinimum === val ? 'var(--color-primary)' : 'var(--color-border)'}`,
                    background: form.levelMinimum === val ? '#e7f5ee' : 'var(--color-surface)',
                    color: form.levelMinimum === val ? 'var(--color-primary)' : 'var(--color-text-muted)',
                    cursor: 'pointer', transition: 'all 0.2s', textAlign: 'center'
                  }}>
                  {label}
                </button>
              ))}
            </div>
          </div>

          {/* Catatan */}
          <div className="form-group">
            <label className="form-label">Catatan (Opsional)</label>
            <textarea className="form-input" rows={4}
              placeholder="Tuliskan info penting: titik kumpul, perlengkapan wajib, pengalaman yang diharapkan..."
              value={form.catatan}
              onChange={e => set('catatan', e.target.value)}
              style={{ resize: 'none', lineHeight: 1.6 }} />
          </div>

          <button id="create-rencana-submit" type="submit" className="btn btn-primary btn-block"
            disabled={loading || !form.jalurId}>
            {loading
              ? <><span className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }} /> Membuat Rencana...</>
              : 'Buat Rencana Pendakian'}
          </button>
        </form>
      </div>
    </div>
  );
}
