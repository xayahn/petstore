/**
 * Custom render utility for React Testing Library.
 * 
 * Wraps components with required providers (Context, Router).
 * Use this instead of render() to properly test components.
 * 
 * Usage:
 * const { getByText } = renderWithContext(<MyComponent />);
 */

import React from 'react';
import { render } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../../context/AuthContext';
import { CartProvider } from '../../context/CartContext';
import { UserProvider } from '../../context/UserContext';

/**
 * Custom render function that wraps components with all necessary providers.
 * 
 * @param {JSX.Element} component - Component to render
 * @param {object} options - Additional render options
 * @returns {object} React Testing Library render result
 */
export function renderWithContext(component, options = {}) {
  function Wrapper({ children }) {
    return (
      <BrowserRouter>
        <AuthProvider>
          <CartProvider>
            <UserProvider>
              {children}
            </UserProvider>
          </CartProvider>
        </AuthProvider>
      </BrowserRouter>
    );
  }

  return render(component, { wrapper: Wrapper, ...options });
}

/**
 * Helper to render only with AuthContext.
 */
export function renderWithAuth(component, options = {}) {
  function Wrapper({ children }) {
    return (
      <BrowserRouter>
        <AuthProvider>
          {children}
        </AuthProvider>
      </BrowserRouter>
    );
  }

  return render(component, { wrapper: Wrapper, ...options });
}

/**
 * Helper to render only with CartContext.
 */
export function renderWithCart(component, options = {}) {
  function Wrapper({ children }) {
    return (
      <BrowserRouter>
        <CartProvider>
          {children}
        </CartProvider>
      </BrowserRouter>
    );
  }

  return render(component, { wrapper: Wrapper, ...options });
}

export * from '@testing-library/react';
