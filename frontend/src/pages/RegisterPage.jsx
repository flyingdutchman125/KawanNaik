// src/pages/RegisterPage.jsx
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../hooks/useToast';
import { getErrMsg } from '../utils/helpers';

const LEVELS = ['PEMULA', 'MENENGAH', 'MAHIR'];
const LEVEL_LABELS = { PEMULA: '🟢 Pemula', MENENGAH: '🟡 Menengah', MAHIR: '🔴 Mahir' };

export default function RegisterPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { toast } = useToast();
  const [step, setStep] = useState(1);
  const [form, setForm] = useState({
    nama: '', email: '', password: '', confirmPassword: '',
    levelFisik: 'PEMULA', pengalaman: '', kontakDarurat: ''
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const set = (k, v) => setForm(p => ({ ...p, [k]: v }));
  const err = (k) => errors[k] && <span className="form-error">{errors[k]}</span>;

  const validateStep1 = () => {
    const e = {};
    if (!form.nama.trim())    e.nama = 'Nama wajib diisi';
    if (!form.email.trim())   e.email = 'Email wajib diisi';
    if (form.password.length < 6) e.password = 'Password minimal 6 karakter';
    if (form.password !== form.confirmPassword) e.confirmPassword = 'Password tidak sama';
    setErrors(e);
    return !Object.keys(e).length;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (step === 1) { if (validateStep1()) setStep(2); return; }
    setLoading(true);
    try {
      const { confirmPassword, ...payload } = form;
      const res = await authApi.register(payload);
      const { token, ...userData } = res.data.data;
      login(token, userData);
      toast('Akun berhasil dibuat.', 'success');
      navigate('/');
    } catch (err) {
      toast(getErrMsg(err), 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-no-nav" style={{ minHeight: '100dvh', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <div style={{ padding: '48px 24px 24px', background: 'var(--color-surface)', borderBottom: '1px solid var(--color-border)' }}>
        <button onClick={() => step === 1 ? navigate('/login') : setStep(1)}
          style={{ color: 'var(--color-text-muted)', fontSize: 13, marginBottom: 16, display: 'flex', alignItems: 'center', gap: 4 }}>
          ← Kembali
        </button>
        <div className="heading-lg">Buat Akun</div>
        <div className="body-md" style={{ marginTop: 4 }}>Langkah {step} dari 2</div>
        {/* Progress bar */}
        <div style={{ marginTop: 14, height: 4, background: 'var(--color-border)', borderRadius: 99, overflow: 'hidden' }}>
          <div style={{ height: '100%', width: step === 1 ? '50%' : '100%', background: 'var(--gradient-primary)',
            borderRadius: 99, transition: 'width 0.4s ease' }} />
        </div>
      </div>

      {/* Form */}
      <div style={{ flex: 1, padding: '28px 24px 24px', background: 'var(--color-bg)' }}>
        <form onSubmit={handleSubmit}>
          {step === 1 ? (
            <>
              <div className="form-group">
                <label className="form-label">Nama Lengkap</label>
                <input id="reg-nama" type="text" className="form-input" placeholder="Budi Santoso"
                  value={form.nama} onChange={e => set('nama', e.target.value)} />
                {err('nama')}
              </div>
              <div className="form-group">
                <label className="form-label">Email</label>
                <input id="reg-email" type="email" className="form-input" placeholder="email@contoh.com"
                  value={form.email} onChange={e => set('email', e.target.value)} />
                {err('email')}
              </div>
              <div className="form-group">
                <label className="form-label">Password</label>
                <input id="reg-password" type="password" className="form-input" placeholder="Minimal 6 karakter"
                  value={form.password} onChange={e => set('password', e.target.value)} />
                {err('password')}
              </div>
              <div className="form-group">
                <label className="form-label">Konfirmasi Password</label>
                <input id="reg-confirm-password" type="password" className="form-input" placeholder="Ulangi password"
                  value={form.confirmPassword} onChange={e => set('confirmPassword', e.target.value)} />
                {err('confirmPassword')}
              </div>
            </>
          ) : (
            <>
              <div className="form-group">
                <label className="form-label">Level Fisik</label>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
                  {LEVELS.map(lvl => (
                    <button type="button" key={lvl} onClick={() => set('levelFisik', lvl)}
                      style={{
                        padding: '12px 4px', borderRadius: 'var(--radius-md)', fontSize: 13, fontWeight: 600,
                        border: `2px solid ${form.levelFisik === lvl ? 'var(--color-primary)' : 'var(--color-border)'}`,
                        background: form.levelFisik === lvl ? '#e7f5ee' : 'var(--color-surface)',
                        color: form.levelFisik === lvl ? 'var(--color-primary)' : 'var(--color-text-muted)',
                        transition: 'all 0.2s', textAlign: 'center'
                      }}>
                      {LEVEL_LABELS[lvl]}
                    </button>
                  ))}
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Pengalaman Mendaki (Opsional)</label>
                <textarea id="reg-pengalaman" className="form-input" rows={3}
                  placeholder="Ceritakan pengalaman pendakianmu..."
                  value={form.pengalaman} onChange={e => set('pengalaman', e.target.value)}
                  style={{ resize: 'none', lineHeight: 1.6 }} />
              </div>
              <div className="form-group">
                <label className="form-label">Kontak Darurat (Opsional)</label>
                <input id="reg-kontak" type="tel" className="form-input" placeholder="08xxxxxxxxxx"
                  value={form.kontakDarurat} onChange={e => set('kontakDarurat', e.target.value)} />
              </div>
            </>
          )}

          <button id="reg-submit" type="submit" className="btn btn-primary btn-block" disabled={loading} style={{ marginTop: 8 }}>
            {loading ? <><span className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }} /> Mendaftar...</>
              : step === 1 ? 'Lanjut →' : 'Buat Akun'}
          </button>
        </form>

        {step === 1 && (
          <div style={{ textAlign: 'center', marginTop: 24, fontSize: 14, color: 'var(--color-text-muted)' }}>
            Sudah punya akun?{' '}
            <Link to="/login" style={{ color: 'var(--color-primary)', fontWeight: 600 }}>Masuk</Link>
          </div>
        )}
      </div>
    </div>
  );
}
