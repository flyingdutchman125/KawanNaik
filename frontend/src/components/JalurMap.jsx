import { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

const JENIS_COLOR = {
  BASECAMP: { fill: '#16a34a', border: '#14532d', radius: 8 },
  POS: { fill: '#22c55e', border: '#ffffff', radius: 6 },
  SUMBER_AIR: { fill: '#3b82f6', border: '#1e3a8a', radius: 7 },
  TITIK_RAWAN: { fill: '#ef4444', border: '#7f1d1d', radius: 7 },
  PUNCAK: { fill: '#f59e0b', border: '#78350f', radius: 9 }
};

export default function JalurMap({ titik = [], gpxTrack = [], jalurId = null, namaJalur = '' }) {
  const elementRef = useRef(null);
  const mapRef = useRef(null);
  const userMarkerRef = useRef(null);
  const watchRef = useRef(null);
  const [tracking, setTracking] = useState(false);
  const [gpsError, setGpsError] = useState('');

  const points = [...titik]
    .filter(point => Number.isFinite(point.latitude) && Number.isFinite(point.longitude))
    .sort((a, b) => a.urutan - b.urutan);

  const hasGpx = Array.isArray(gpxTrack) && gpxTrack.length > 1;

  useEffect(() => {
    if (!elementRef.current || mapRef.current) return undefined;
    const map = L.map(elementRef.current, { zoomControl: false, attributionControl: true });
    L.control.zoom({ position: 'topright' }).addTo(map);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);
    mapRef.current = map;
    return () => {
      if (watchRef.current !== null) navigator.geolocation?.clearWatch(watchRef.current);
      map.remove();
      mapRef.current = null;
    };
  }, []);

  useEffect(() => {
    const map = mapRef.current;
    if (!map) return;

    map.eachLayer(layer => {
      if (layer instanceof L.TileLayer || layer === userMarkerRef.current) return;
      map.removeLayer(layer);
    });

    const boundsPoints = [];

    // 1. Gambar Polyline: Prioritaskan GPX track asli yang presisi dan meliuk alami
    if (hasGpx) {
      const gpxLatLngs = gpxTrack.map(coord => [coord[0], coord[1]]);
      L.polyline(gpxLatLngs, {
        color: '#15803d',
        weight: 4.5,
        opacity: 0.9,
        lineJoin: 'round',
        lineCap: 'round'
      }).addTo(map);
      gpxLatLngs.forEach(c => boundsPoints.push(c));
    } else if (points.length > 1) {
      const waypointLatLngs = points.map(point => [point.latitude, point.longitude]);
      L.polyline(waypointLatLngs, {
        color: '#16a34a',
        weight: 4,
        opacity: 0.85,
        dashArray: '4, 4'
      }).addTo(map);
      waypointLatLngs.forEach(c => boundsPoints.push(c));
    }

    // 2. Beri Marker pada Titik Waypoint (Pos, Basecamp, Sumber Air, Titik Rawan, Puncak)
    points.forEach((point, index) => {
      const latLng = [point.latitude, point.longitude];
      boundsPoints.push(latLng);

      const style = JENIS_COLOR[point.jenis] || JENIS_COLOR.POS;
      const marker = L.circleMarker(latLng, {
        radius: style.radius,
        color: style.border,
        weight: 2,
        fillColor: style.fill,
        fillOpacity: 1
      });

      const popupHtml = `
        <div style="font-family: inherit; min-width: 140px;">
          <div style="font-weight: 700; font-size: 13px; color: #1e293b; margin-bottom: 2px;">
            ${point.urutan ? `${point.urutan}. ` : ''}${point.nama}
          </div>
          <div style="font-size: 11px; font-weight: 600; color: ${style.fill}; text-transform: uppercase;">
            ${point.jenis || 'POS'}
          </div>
          ${point.deskripsi ? `<div style="font-size: 11px; color: #475569; margin-top: 4px; border-top: 1px solid #e2e8f0; padding-top: 4px;">${point.deskripsi}</div>` : ''}
        </div>
      `;

      marker.bindPopup(popupHtml).addTo(map);
    });

    if (boundsPoints.length > 0) {
      map.fitBounds(L.latLngBounds(boundsPoints), { padding: [32, 32], maxZoom: 15 });
    }
  }, [titik, gpxTrack, hasGpx, points.length]);

  const toggleGps = () => {
    if (tracking) {
      navigator.geolocation?.clearWatch(watchRef.current);
      watchRef.current = null;
      setTracking(false);
      return;
    }
    if (!navigator.geolocation) {
      setGpsError('Perangkat tidak mendukung GPS.');
      return;
    }
    setGpsError('');
    watchRef.current = navigator.geolocation.watchPosition(({ coords }) => {
      const position = [coords.latitude, coords.longitude];
      const map = mapRef.current;
      if (!map) return;
      if (!userMarkerRef.current) {
        userMarkerRef.current = L.circleMarker(position, {
          radius: 8, color: '#fff', weight: 3, fillColor: '#175cd3', fillOpacity: 1
        }).bindTooltip('Lokasi saya').addTo(map);
      } else userMarkerRef.current.setLatLng(position);
      map.setView(position, Math.max(map.getZoom(), 15));
    }, () => setGpsError('Izin lokasi ditolak atau GPS tidak tersedia.'), {
      enableHighAccuracy: true, maximumAge: 5000, timeout: 10000
    });
    setTracking(true);
  };

  const handleDownloadGpx = () => {
    if (!jalurId) return;
    const url = `/api/v1/jalur/${jalurId}/gpx`;
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `jalur_${jalurId}.gpx`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (!points.length && !hasGpx) return null;

  return (
    <section className="jalur-map-section" aria-label="Peta jalur pendakian">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 8, marginBottom: 10 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <div className="heading-sm" style={{ margin: 0 }}>Peta Jalur</div>
          {hasGpx && (
            <span className="badge badge-success" style={{ fontSize: 10, padding: '2px 8px' }}>
              🛰️ Track GPX
            </span>
          )}
        </div>
        <div style={{ display: 'flex', gap: 6 }}>
          {jalurId && (
            <button
              className="btn btn-ghost btn-sm"
              onClick={handleDownloadGpx}
              title="Unduh file GPX resmi untuk GPS"
              style={{ padding: '4px 8px', fontSize: 12 }}>
              📥 Unduh GPX
            </button>
          )}
          <button
            className={`btn btn-sm ${tracking ? 'btn-primary' : 'btn-ghost'}`}
            onClick={toggleGps}
            style={{ padding: '4px 10px', fontSize: 12 }}>
            {tracking ? '🔴 Matikan GPS' : '📍 Gunakan GPS'}
          </button>
        </div>
      </div>

      <div className="jalur-map" ref={elementRef} style={{ position: 'relative', zIndex: 1, isolation: 'isolate' }} />
      {gpsError && <div className="form-error" style={{ marginTop: 6 }}>{gpsError}</div>}

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 8, flexWrap: 'wrap', gap: 6 }}>
        <div className="body-sm" style={{ color: 'var(--color-text-secondary)', fontSize: 12 }}>
          {hasGpx
            ? `Rute berbasis track GPX (${gpxTrack.length} titik koordinat) & ${points.length} pos.`
            : `Rute berdasarkan ${points.length} titik waypoint admin.`}
        </div>
        <div style={{ display: 'flex', gap: 10, fontSize: 11, color: 'var(--color-text-secondary)' }}>
          <span>⛺ Basecamp</span>
          <span>📍 Pos</span>
          <span>💧 Sumber Air</span>
          <span>🚩 Puncak</span>
        </div>
      </div>
    </section>
  );
}
