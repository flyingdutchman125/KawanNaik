// src/components/InstallBanner.jsx — PWA install prompt
import { useState, useEffect } from 'react';

export default function InstallBanner() {
  const [deferredPrompt, setDeferredPrompt] = useState(null);
  const [shown, setShown] = useState(false);

  useEffect(() => {
    if (localStorage.getItem('kn_install_dismissed')) return;

    const handler = (e) => {
      e.preventDefault();
      setDeferredPrompt(e);
      setShown(true);
    };
    window.addEventListener('beforeinstallprompt', handler);
    return () => window.removeEventListener('beforeinstallprompt', handler);
  }, []);

  const handleInstall = async () => {
    if (!deferredPrompt) return;
    deferredPrompt.prompt();
    const { outcome } = await deferredPrompt.userChoice;
    if (outcome === 'accepted') setShown(false);
    setDeferredPrompt(null);
  };

  const handleDismiss = () => {
    setShown(false);
    localStorage.setItem('kn_install_dismissed', '1');
  };

  if (!shown) return null;

  return (
    <div className="install-banner">
      <img src="/icon-512.jpg" alt="KawanNaik" style={{ width: 44, height: 44, borderRadius: 10, flexShrink: 0 }} />
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 13, fontWeight: 700, color: 'var(--color-text)', marginBottom: 2 }}>Install KawanNaik</div>
        <div style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>Akses cepat dari layar utama</div>
      </div>
      <button className="btn btn-primary btn-sm" onClick={handleInstall}>Install</button>
      <button onClick={handleDismiss} style={{ color: 'var(--color-text-muted)', padding: '4px', fontSize: 18, lineHeight: 1 }}>✕</button>
    </div>
  );
}
