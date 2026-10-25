import { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

export default function JalurMap({ titik = [] }) {
  const elementRef = useRef(null);
  const mapRef = useRef(null);
  const userMarkerRef = useRef(null);
  const watchRef = useRef(null);
  const [tracking, setTracking] = useState(false);
  const [gpsError, setGpsError] = useState('');

  const points = [...titik]
    .filter(point => Number.isFinite(point.latitude) && Number.isFinite(point.longitude))
    .sort((a, b) => a.urutan - b.urutan);

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
    if (!map || !points.length) return;
    map.eachLayer(layer => {
      if (layer instanceof L.TileLayer || layer === userMarkerRef.current) return;
      map.removeLayer(layer);
    });
    const latLngs = points.map(point => [point.latitude, point.longitude]);
    L.polyline(latLngs, { color: '#1f7a4d', weight: 5, opacity: 0.85 }).addTo(map);
    points.forEach((point, index) => {
      L.circleMarker([point.latitude, point.longitude], {
        radius: index === 0 || index === points.length - 1 ? 8 : 6,
        color: '#ffffff', weight: 2, fillColor: index === 0 ? '#17603c' : '#1f7a4d', fillOpacity: 1
      }).bindPopup(`<strong>${point.urutan}. ${point.nama}</strong>${point.deskripsi ? `<br>${point.deskripsi}` : ''}`).addTo(map);
    });
    map.fitBounds(L.latLngBounds(latLngs), { padding: [24, 24] });
  }, [titik, points.length]);

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

  if (!points.length) return null;

  return (
    <section className="jalur-map-section" aria-label="Peta jalur pendakian">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div className="heading-sm">Peta Jalur</div>
        <button className="btn btn-ghost btn-sm" onClick={toggleGps}>
          {tracking ? 'Matikan GPS' : 'Gunakan GPS'}
        </button>
      </div>
      <div className="jalur-map" ref={elementRef} />
      {gpsError && <div className="form-error" style={{ marginTop: 6 }}>{gpsError}</div>}
      <div className="body-sm" style={{ marginTop: 8 }}>
        Rute berdasarkan {points.length} titik admin. GPS hanya digunakan di perangkat ini.
      </div>
    </section>
  );
}
