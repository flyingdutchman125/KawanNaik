// src/hooks/useToast.js
import { useState, useCallback } from 'react';

let _setToasts = null;

export function useToastState() {
  const [toasts, setToasts] = useState([]);
  _setToasts = setToasts;
  return toasts;
}

export function toast(message, type = 'info') {
  if (!_setToasts) return;
  const id = Date.now();
  _setToasts(prev => [...prev, { id, message, type }]);
  setTimeout(() => _setToasts(prev => prev.filter(t => t.id !== id)), 3500);
}

export function useToast() {
  const addToast = useCallback((message, type = 'info') => {
    toast(message, type);
  }, []);
  return { toast: addToast };
}
