import { useEffect, useState } from 'react';

export default function InstallButton() {
  const [prompt, setPrompt] = useState(null);
  const [message, setMessage] = useState('');

  useEffect(() => {
    const handler = event => { event.preventDefault(); setPrompt(event); };
    window.addEventListener('beforeinstallprompt', handler);
    return () => window.removeEventListener('beforeinstallprompt', handler);
  }, []);

  const install = async () => {
    if (!prompt) {
      setMessage(/iphone|ipad|ipod/i.test(navigator.userAgent)
        ? 'Di Safari: tekan Bagikan, lalu pilih Tambahkan ke Layar Utama.'
        : 'Buka aplikasi melalui HTTPS atau localhost untuk mengaktifkan instalasi.');
      return;
    }
    prompt.prompt();
    await prompt.userChoice;
    setPrompt(null);
  };

  return (
    <div className="install-action">
      <button type="button" className="btn btn-secondary btn-block" onClick={install}>
        Install aplikasi
      </button>
      {message && <div className="body-sm install-help">{message}</div>}
    </div>
  );
}
