// src/context/AuthContext.jsx
import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { userApi } from '../lib/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser]       = useState(() => {
    try { return JSON.parse(localStorage.getItem('kn_user')) || null; } catch { return null; }
  });
  const [token, setToken]     = useState(() => localStorage.getItem('kn_token') || null);
  const [loading, setLoading] = useState(true);

  // Verify token on mount
  useEffect(() => {
    if (token) {
      userApi.getProfile()
        .then(res => { setUser(res.data.data); localStorage.setItem('kn_user', JSON.stringify(res.data.data)); })
        .catch(() => { logout(); })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, []);

  const login = useCallback((tokenValue, userData) => {
    localStorage.setItem('kn_token', tokenValue);
    localStorage.setItem('kn_user', JSON.stringify(userData));
    setToken(tokenValue);
    setUser(userData);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('kn_token');
    localStorage.removeItem('kn_user');
    setToken(null);
    setUser(null);
  }, []);

  const refreshUser = useCallback(async () => {
    if (!token) return;
    const res = await userApi.getProfile();
    setUser(res.data.data);
    localStorage.setItem('kn_user', JSON.stringify(res.data.data));
  }, [token]);

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout, refreshUser, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
};
