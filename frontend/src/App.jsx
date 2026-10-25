// src/App.jsx — Main router & layout
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import BottomNav from './components/BottomNav';
import Toast from './components/Toast';
import InstallBanner from './components/InstallBanner';
import OfflineBanner from './components/OfflineBanner';

// Pages
import LoginPage       from './pages/LoginPage';
import RegisterPage    from './pages/RegisterPage';
import HomePage        from './pages/HomePage';
import GunungPage      from './pages/GunungPage';
import GunungDetailPage from './pages/GunungDetailPage';
import RencanaPage     from './pages/RencanaPage';
import RencanaDetailPage from './pages/RencanaDetailPage';
import BuatRencanaPage from './pages/BuatRencanaPage';
import PanduanPage     from './pages/PanduanPage';
import ProfilPage      from './pages/ProfilPage';
import AdminJalurPage  from './pages/AdminJalurPage';

function ProtectedRoute({ children, admin = false }) {
  const { isAuthenticated, loading, user } = useAuth();
  if (loading) return (
    <div className="loading-screen">
      <div className="spinner" />
    </div>
  );
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return admin && user?.peran !== 'ADMIN' ? <Navigate to="/" replace /> : children;
}

function AppShell() {
  const { loading } = useAuth();

  if (loading) return (
    <div className="loading-screen">
      <div style={{ textAlign: 'center' }}>
        <img src="/icon-512.jpg" alt="KawanNaik" style={{ width: 72, height: 72, borderRadius: 18, marginBottom: 20 }} />
        <div className="spinner" style={{ margin: '0 auto' }} />
        <div className="body-sm" style={{ marginTop: 14 }}>Memuat KawanNaik...</div>
      </div>
    </div>
  );

  return (
    <div className="app-shell">
      <Routes>
        {/* Auth (no bottom nav) */}
        <Route path="/login"    element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Main app with bottom nav */}
        <Route path="/*" element={
          <>
            <Routes>
              <Route path="/"               element={<HomePage />} />
              <Route path="/gunung"         element={<GunungPage />} />
              <Route path="/gunung/:id"     element={<GunungDetailPage />} />
              <Route path="/rencana"        element={<RencanaPage />} />
              <Route path="/rencana/:id"    element={<RencanaDetailPage />} />
              <Route path="/rencana/buat"   element={
                <ProtectedRoute><BuatRencanaPage /></ProtectedRoute>
              } />
              <Route path="/panduan"        element={<PanduanPage />} />
              <Route path="/profil"         element={<ProfilPage />} />
              <Route path="/admin/jalur"    element={<ProtectedRoute admin><AdminJalurPage /></ProtectedRoute>} />
              <Route path="*"               element={<Navigate to="/" replace />} />
            </Routes>
            <BottomNav />
            <InstallBanner />
            <OfflineBanner />
          </>
        } />
      </Routes>
      <Toast />
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppShell />
      </AuthProvider>
    </BrowserRouter>
  );
}
