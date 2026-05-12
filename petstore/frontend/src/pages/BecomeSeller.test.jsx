import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter as Router } from 'react-router-dom';
import BecomeSeller from '../pages/BecomeSeller';
import * as authHooks from '../hooks/useAuth';

// Mock the auth hook
jest.mock('../hooks/useAuth');

// Mock the components that are used
jest.mock('../components/Seller/SellerRegistrationForm', () => {
  return function MockSellerRegistrationForm({ onSubmit, isLoading }) {
    return (
      <form
        onSubmit={(e) => {
          e.preventDefault();
          onSubmit({
            businessName: 'Test Business',
            bio: 'Test bio',
            returnPolicy: 'Test return policy',
          });
        }}
      >
        <button type="submit" disabled={isLoading}>
          Submit Registration
        </button>
      </form>
    );
  };
});

jest.mock('../components/Seller/EmailVerificationStep', () => {
  return function MockEmailVerificationStep({ email, onVerificationComplete }) {
    return (
      <div>
        <p>Email: {email}</p>
        <button onClick={onVerificationComplete}>Verify Email</button>
      </div>
    );
  };
});

// Mock fetch
global.fetch = jest.fn();

describe('BecomeSeller', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.setItem('token', 'test-token');
    authHooks.default.mockReturnValue({
      user: { id: '123', email: 'test@example.com' },
      isAuthenticated: true,
    });
  });

  afterEach(() => {
    jest.restoreAllMocks();
    localStorage.clear();
  });

  test('renders BecomeSeller page with progress steps', () => {
    render(
      <Router>
        <BecomeSeller />
      </Router>
    );

    expect(screen.getByText('Become a Seller')).toBeInTheDocument();
    expect(screen.getByText('Join our marketplace and start selling quality pets')).toBeInTheDocument();
    expect(screen.getByText('Registration')).toBeInTheDocument();
    expect(screen.getByText('Verify Email')).toBeInTheDocument();
    expect(screen.getByText('Complete')).toBeInTheDocument();
  });

  test('shows registration form on initial render', () => {
    render(
      <Router>
        <BecomeSeller />
      </Router>
    );

    expect(screen.getByText('Submit Registration')).toBeInTheDocument();
  });

  test('redirects unauthenticated users', () => {
    authHooks.default.mockReturnValue({
      user: null,
      isAuthenticated: false,
    });

    render(
      <Router>
        <BecomeSeller />
      </Router>
    );

    expect(screen.getByText('You must be logged in to become a seller.')).toBeInTheDocument();
    expect(screen.getByText('Login First')).toBeInTheDocument();
  });

  test('handles registration form submission', async () => {
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        id: 'seller-123',
        email: 'test@example.com',
      }),
    });

    render(
      <Router>
        <BecomeSeller />
      </Router>
    );

    const submitButton = screen.getByText('Submit Registration');
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith(
        '/api/sellers/register',
        expect.objectContaining({
          method: 'POST',
          headers: expect.objectContaining({
            'Content-Type': 'application/json',
            'Authorization': 'Bearer test-token',
          }),
        })
      );
    });

    // Should advance to email verification step
    await waitFor(() => {
      expect(screen.getByText(/Email:/)).toBeInTheDocument();
    });
  });

  test('displays error message on registration failure', async () => {
    global.fetch.mockResolvedValueOnce({
      ok: false,
      json: async () => ({
        message: 'Business name already exists',
      }),
    });

    render(
      <Router>
        <BecomeSeller />
      </Router>
    );

    const submitButton = screen.getByText('Submit Registration');
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Business name already exists')).toBeInTheDocument();
    });
  });

  test('advances through steps correctly', async () => {
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        id: 'seller-123',
        email: 'test@example.com',
      }),
    });

    render(
      <Router>
        <BecomeSeller />
      </Router>
    );

    // Initial step: registration
    expect(screen.getByText('Submit Registration')).toBeInTheDocument();

    // Submit registration
    fireEvent.click(screen.getByText('Submit Registration'));

    // Wait for email verification step
    await waitFor(() => {
      expect(screen.getByText(/Email:/)).toBeInTheDocument();
    });

    // Complete email verification
    fireEvent.click(screen.getByText('Verify Email'));

    // Should show success message
    await waitFor(() => {
      expect(screen.getByText('Welcome to Petstore Seller Platform!')).toBeInTheDocument();
    });
  });

  test('handles registration error properly', async () => {
    const errorMessage = 'Server error';
    global.fetch.mockRejectedValueOnce(new Error(errorMessage));

    render(
      <Router>
        <BecomeSeller />
      </Router>
    );

    fireEvent.click(screen.getByText('Submit Registration'));

    await waitFor(() => {
      expect(screen.getByText(errorMessage)).toBeInTheDocument();
    });
  });

  test('allows user to close error message', async () => {
    global.fetch.mockResolvedValueOnce({
      ok: false,
      json: async () => ({
        message: 'Test error',
      }),
    });

    render(
      <Router>
        <BecomeSeller />
      </Router>
    );

    fireEvent.click(screen.getByText('Submit Registration'));

    await waitFor(() => {
      expect(screen.getByText('Test error')).toBeInTheDocument();
    });

    // Find and click the close button
    const closeButtons = screen.getAllByText('×');
    fireEvent.click(closeButtons[0]);

    await waitFor(() => {
      expect(screen.queryByText('Test error')).not.toBeInTheDocument();
    });
  });
});
