import { useEffect, useState } from 'react';

function useClock() {
  const [now, setNow] = useState(new Date());
  useEffect(() => {
    const timer = setInterval(() => setNow(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);
  return now;
}

function useCompass() {
  const [heading, setHeading] = useState(null);
  const [enabled, setEnabled] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!enabled) return undefined;
    const onOrientation = event => {
      const value = event.webkitCompassHeading ?? (event.alpha == null ? null : 360 - event.alpha);
      if (value != null) setHeading(Math.round((value + 360) % 360));
    };
    window.addEventListener('deviceorientationabsolute', onOrientation, true);
    window.addEventListener('deviceorientation', onOrientation, true);
    return () => {
      window.removeEventListener('deviceorientationabsolute', onOrientation, true);
      window.removeEventListener('deviceorientation', onOrientation, true);
    };
  }, [enabled]);

  const enable = async () => {
    try {
      if (typeof DeviceOrientationEvent?.requestPermission === 'function') {
        const permission = await DeviceOrientationEvent.requestPermission();
        if (permission !== 'granted') throw new Error('Izin kompas ditolak');
      }
      setError('');
      setEnabled(true);
    } catch (err) {
      setError(err.message || 'Kompas tidak tersedia');
    }
  };

  return { heading, enabled, error, enable };
}

export default function DashboardTools() {
  const now = useClock();
  const compass = useCompass();
  const time = now.toLocaleTimeString('id-ID', { hour: '2-digit', minute: '2-digit' });
  const date = now.toLocaleDateString('id-ID', { weekday: 'long', day: 'numeric', month: 'long' });
  const direction = compass.heading == null ? '—' : compass.heading < 23 || compass.heading >= 338 ? 'U' : compass.heading < 68 ? 'TL' : compass.heading < 113 ? 'T' : compass.heading < 158 ? 'Tenggara' : compass.heading < 203 ? 'S' : compass.heading < 248 ? 'Barat Daya' : compass.heading < 293 ? 'B' : 'Barat Laut';

  return (
    <section className="dashboard-tools" aria-label="Peralatan pendakian">
      <div className="tool-card clock-card">
        <div className="tool-label">Waktu lokal</div>
        <div className="tool-time">{time}</div>
        <div className="tool-meta">{date}</div>
      </div>
      <div className="tool-card compass-card">
        <div className="tool-label">Kompas</div>
        <div className="compass-readout">
          <div className="compass-dial" style={{ transform: `rotate(${compass.heading == null ? 0 : -compass.heading}deg)` }}>
            <span>N</span><i />
          </div>
          <div><strong>{direction}</strong><small>{compass.heading == null ? 'Belum aktif' : `${compass.heading}°`}</small></div>
        </div>
        {!compass.enabled && <button className="btn btn-secondary btn-sm" onClick={compass.enable}>Aktifkan kompas</button>}
        {compass.error && <div className="form-error">{compass.error}</div>}
      </div>
    </section>
  );
}
