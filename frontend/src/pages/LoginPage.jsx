// src/pages/LoginPage.jsx
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../lib/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../hooks/useToast';
import { getErrMsg } from '../utils/helpers';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { toast } = useToast();
  const [form, setForm] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const e = {};
    if (!form.email)    e.email    = 'Email wajib diisi';
    if (!form.password) e.password = 'Password wajib diisi';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    try {
      const res = await authApi.login(form);
      const { token, ...userData } = res.data.data;
      login(token, userData);
      toast('Selamat datang kembali.', 'success');
      navigate('/');
    } catch (err) {
      toast(getErrMsg(err), 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-no-nav login-page">
      <div className="login-layout">
      {/* Header visual */}
      <div className="login-intro">
        {/* Decorative circles */}
        <div style={{ position: 'absolute', top: -40, right: -40, width: 180, height: 180,
          background: 'radial-gradient(circle, rgba(31,122,77,0.06) 0%, transparent 70%)', borderRadius: '50%' }} />
        <div style={{ position: 'absolute', bottom: -20, left: -20, width: 120, height: 120,
          background: 'radial-gradient(circle, rgba(31,122,77,0.04) 0%, transparent 70%)', borderRadius: '50%' }} />

        <div style={{ display: 'flex', alignItems: 'center', gap: 14, position: 'relative' }}>
          <img src="/icon-512.jpg" alt="KawanNaik" style={{ width: 52, height: 52, borderRadius: 14, border: '1px solid var(--color-border)' }} />
          <div>
            <div className="heading-lg">KawanNaik</div>
            <div className="body-sm">Teman mendaki terpercaya</div>
          </div>
        </div>
        <div className="heading-xl" style={{ marginTop: 28 }}>Masuk ke Akun</div>
        <div className="body-md" style={{ marginTop: 6 }}>Temukan teman pendakian impianmu</div>
      </div>

      {/* Form */}
      <div className="login-form">
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email</label>
            <input
              id="login-email"
              type="email"
              className="form-input"
              placeholder="nama@email.com"
              value={form.email}
              onChange={e => setForm(p => ({ ...p, email: e.target.value }))}
              autoComplete="email"
            />
            {errors.email && <span className="form-error">{errors.email}</span>}
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              id="login-password"
              type="password"
              className="form-input"
              placeholder="Minimal 6 karakter"
              value={form.password}
              onChange={e => setForm(p => ({ ...p, password: e.target.value }))}
              autoComplete="current-password"
            />
            {errors.password && <span className="form-error">{errors.password}</span>}
          </div>

          <button id="login-submit" type="submit" className="btn btn-primary btn-block" disabled={loading} style={{ marginTop: 8 }}>
            {loading ? (
              <><span className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }} /> Masuk...</>
            ) : 'Masuk'}
          </button>
        </form>

        {/* Demo credentials */}
        <div className="demo-panel" style={{
          marginTop: 20,
          padding: '14px 16px'
        }}>
          <div style={{ fontSize: 12, fontWeight: 700, color: 'var(--color-primary)', marginBottom: 8 }}>🔑 Akun Demo</div>
          {[
            { label: 'Pendaki (Mahir)', email: 'budi@example.com' },
            { label: 'Pendaki (Menengah)', email: 'sari@example.com' },
            { label: 'Admin', email: 'admin@kawannaik.com' },
          ].map(({ label, email }) => (
            <button key={email} onClick={() => setForm({ email, password: 'password123' })}
              style={{ display: 'block', textAlign: 'left', fontSize: 12, color: 'var(--color-text-2)',
                padding: '4px 0', background: 'none', border: 'none', cursor: 'pointer', width: '100%' }}>
              <span style={{ color: 'var(--color-text-3)', fontWeight: 600 }}>{label}:</span> {email}
            </button>
          ))}
        </div>

        <div style={{ textAlign: 'center', marginTop: 24, fontSize: 14, color: 'var(--color-text-muted)' }}>
          Belum punya akun?{' '}
          <Link to="/register" style={{ color: 'var(--color-primary)', fontWeight: 600 }}>Daftar sekarang</Link>
        </div>
      </div>
      </div>
    </div>
  );
}
