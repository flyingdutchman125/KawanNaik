// src/components/OfflineBanner.jsx — PWA offline state
import { useEffect, useState } from 'react';

export default function OfflineBanner() {
  const [offline, setOffline] = useState(!navigator.onLine);

  useEffect(() => {
    const on = () => setOffline(false);
    const off = () => setOffline(true);
    window.addEventListener('online', on);
    window.addEventListener('offline', off);
    return () => {
      window.removeEventListener('online', on);
      window.removeEventListener('offline', off);
    };
  }, []);

  if (!offline) return null;

  return (
    <div style={{
      position: 'fixed', top: 0, left: 0, right: 0, zIndex: 1000,
      padding: '8px 16px', textAlign: 'center', fontSize: 13, fontWeight: 600,
      background: 'var(--color-amber, #b45309)', color: '#fff'
    }}>
      Mode offline — data terakhir yang tersimpan
    </div>
  );
}
