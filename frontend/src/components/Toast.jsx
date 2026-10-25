import { useToastState } from '../hooks/useToast';

const labels = { success: 'Berhasil', error: 'Perlu diperiksa', info: 'Informasi' };

export default function Toast() {
  const toasts = useToastState();
  if (!toasts.length) return null;

  return (
    <div className="toast-container" aria-live="polite">
      {toasts.map(t => (
        <div key={t.id} className={`toast ${t.type}`} role="status">
          <span className="toast-mark" aria-hidden="true" />
          <div>
            <strong>{labels[t.type] || labels.info}</strong>
            <div>{t.message}</div>
          </div>
        </div>
      ))}
    </div>
  );
}
