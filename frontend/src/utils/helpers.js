// src/utils/helpers.js

export function levelColor(level) {
  const map = { PEMULA: 'badge-green', MENENGAH: 'badge-amber', MAHIR: 'badge-red' };
  return map[level] || 'badge-muted';
}

export function levelLabel(level) {
  const map = { PEMULA: 'Pemula', MENENGAH: 'Menengah', MAHIR: 'Mahir' };
  return map[level] || level;
}

export function statusRencanaColor(status) {
  const map = { DIBUKA: 'badge-green', DITUTUP: 'badge-amber', SELESAI: 'badge-muted', DIBATALKAN: 'badge-red' };
  return map[status] || 'badge-muted';
}

export function statusRencanaLabel(status) {
  const map = { DIBUKA: 'Dibuka', DITUTUP: 'Ditutup', SELESAI: 'Selesai', DIBATALKAN: 'Dibatalkan' };
  return map[status] || status;
}

export function statusPesertaColor(status) {
  const map = { MENUNGGU: 'badge-amber', DITERIMA: 'badge-green', DITOLAK: 'badge-red' };
  return map[status] || 'badge-muted';
}

export function statusPesertaLabel(status) {
  const map = { MENUNGGU: 'Menunggu', DITERIMA: 'Diterima', DITOLAK: 'Ditolak' };
  return map[status] || status;
}

export function formatDate(dateStr) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleDateString('id-ID', { day: 'numeric', month: 'long', year: 'numeric' });
}

export function formatDateShort(dateStr) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleDateString('id-ID', { day: 'numeric', month: 'short' });
}

export function getInitials(name) {
  if (!name) return '?';
  return name.split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase();
}

export function getErrMsg(err) {
  return err?.response?.data?.message || err?.message || 'Terjadi kesalahan';
}

export const MOUNTAIN_EMOJIS = ['🏔️', '⛰️', '🗻', '🌋'];
export function getMtnEmoji(id) {
  return MOUNTAIN_EMOJIS[id % MOUNTAIN_EMOJIS.length];
}
