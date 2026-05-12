import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import './Header.css';

/**
 * Header component with navigation and authentication status.
 * 
 * Features:
 * - Logo/branding
 * - Navigation links
 * - Cart icon with item count
 * - Authentication status (Login/Logout)
 * - Responsive mobile menu (future)
 */
export default function Header() {
  const { isAuthenticated, user, logout } = useAuth();

  const handleLogout = () => {
    logout();
  };

  return (
    <header className="header">
      <nav className="navbar">
        <div className="nav-container">
          <Link to="/" className="logo">
            🐾 Petstore
          </Link>

          <ul className="nav-menu">
            <li>
              <Link to="/">Home</Link>
            </li>
            <li>
              <Link to="/pets">Browse Pets</Link>
            </li>
            <li>
              <Link to="/cart">🛒 Cart</Link>
            </li>
            {isAuthenticated && user?.is_seller && (
              <li>
                <Link to="/seller">Seller Dashboard</Link>
              </li>
            )}
            {!isAuthenticated ? (
              <>
                <li>
                  <Link to="/login" className="nav-link-btn">
                    Login
                  </Link>
                </li>
                <li>
                  <Link to="/register" className="nav-link-btn primary">
                    Register
                  </Link>
                </li>
              </>
            ) : (
              <>
                <li className="user-info">
                  Welcome, {user?.first_name}!
                </li>
                <li>
                  <Link to="/orders">Orders</Link>
                </li>
                <li>
                  <button onClick={handleLogout} className="nav-link-btn logout">
                    Logout
                  </button>
                </li>
              </>
            )}
          </ul>
        </div>
      </nav>
    </header>
  );
}
