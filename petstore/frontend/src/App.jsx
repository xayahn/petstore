import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import { UserProvider } from './context/UserContext';
import Header from './components/Header';
import Footer from './components/Footer';
import LandingPage from './pages/LandingPage';
import BrowsePetsPage from './pages/BrowsePetsPage';
import PetDetailPage from './pages/PetDetailPage';
import './App.css';
import BecomeSeller from './pages/BecomeSeller';
import VerifyEmailPage from './pages/VerifyEmailPage';

/**
 * Root application component.
 * 
 * Sets up:
 * - Context providers (Auth, Cart, User)
 * - Router with routes
 * - Header and Footer layout
 * - Global error boundary (future)
 */
function App() {
  return (
    <Router>
      <AuthProvider>
        <CartProvider>
          <UserProvider>
            <div className="app-container">
              <Header />
              <main className="main-content">
                <Routes>
                  <Route path="/" element={<LandingPage />} />
                  <Route path="/browse" element={<BrowsePetsPage />} />
                  <Route path="/pets/:id" element={<PetDetailPage />} />
                  <Route path="/cart" element={<div>Shopping Cart - Coming Soon</div>} />
                  <Route path="/checkout" element={<div>Checkout - Coming Soon</div>} />
                  <Route path="/login" element={<div>Login - Coming Soon</div>} />
                  <Route path="/register" element={<div>Register - Coming Soon</div>} />
                  <Route path="/become-seller" element={<BecomeSeller />} />
                  <Route path="/verify-email" element={<VerifyEmailPage />} />
                  <Route path="/seller-dashboard" element={<div>Seller Dashboard - Coming Soon</div>} />
                  <Route path="/seller" element={<div>Seller Dashboard - Coming Soon</div>} />
                  <Route path="/orders" element={<div>My Orders - Coming Soon</div>} />
                  <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
              </main>
              <Footer />
            </div>
          </UserProvider>
        </CartProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;
