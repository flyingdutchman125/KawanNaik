// src/main.jsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { registerSW } from 'virtual:pwa-register'

// Register service worker with auto-update
registerSW({
  onRegistered(r) {
    r && setInterval(() => { r.update() }, 60 * 60 * 1000) // Check update every hour
  },
  onOfflineReady() {
    console.log('KawanNaik siap digunakan secara offline! 🏔️')
  },
})

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
