// src/lib/api.js — Axios instance with JWT interceptor
import axios from 'axios';

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('kn_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Handle 401 — clear token and redirect to login
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('kn_token');
      localStorage.removeItem('kn_user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export default api;

// ---- Auth ----
export const authApi = {
  login:    (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
};

// ---- Users ----
export const userApi = {
  getProfile:    ()     => api.get('/users/me'),
  updateProfile: (data) => api.put('/users/me', data),
};

// ---- Gunung ----
export const gunungApi = {
  getAll:  (search) => api.get('/gunung', { params: { search } }),
  getById: (id)     => api.get(`/gunung/${id}`),
};

// ---- Jalur ----
export const jalurApi = {
  getByGunung: (gunungId) => api.get(`/jalur/gunung/${gunungId}`),
  getById:     (id)       => api.get(`/jalur/${id}`),
  create:      (data)     => api.post('/admin/jalur', data),
};

export const titikJalurApi = {
  getByJalur: (jalurId) => api.get(`/titik-jalur/jalur/${jalurId}`),
  create: (data) => api.post('/admin/titik-jalur', data),
  remove: (id) => api.delete(`/admin/titik-jalur/${id}`),
};

// ---- Rencana Pendakian ----
export const rencanaApi = {
  getAll:   (params) => api.get('/rencana', { params }),
  getById:  (id)     => api.get(`/rencana/${id}`),
  getMy:    ()       => api.get('/rencana/my'),
  create:   (data)   => api.post('/rencana', data),
  updateStatus: (id, status) => api.put(`/rencana/${id}/status`, { status }),
  cancel:   (id)     => api.delete(`/rencana/${id}/cancel`),
};

// ---- Peserta Rencana ----
export const pesertaApi = {
  join:         (rencanaId)             => api.post(`/rencana/${rencanaId}/join`),
  leave:        (rencanaId)             => api.delete(`/rencana/${rencanaId}/leave`),
  getByRencana: (rencanaId)             => api.get(`/rencana/${rencanaId}/peserta`),
  updateStatus: (rencanaId, pesertaId, statusPermintaan) =>
    api.put(`/rencana/${rencanaId}/peserta/${pesertaId}/status`, { statusPermintaan }),
};

// ---- Panduan Keselamatan ----
export const panduanApi = {
  getAll:  () => api.get('/panduan'),
  getById: (id) => api.get(`/panduan/${id}`),
};
