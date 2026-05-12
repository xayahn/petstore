import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PetListingForm from '../components/Seller/PetListingForm';
import useAuth from '../hooks/useAuth';

/**
 * CreatePetListing Page Component
 * 
 * Page for sellers to create new pet listings.
 * Handles:
 * - Pet information form (name, species, breed, price, etc.)
 * - Multiple image uploads
 * - Validation and submission to backend
 * 
 * Used by: Router path=/create-listing
 * Requires: User must be authenticated as a seller
 */
export default function CreatePetListing() {
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');

  /**
   * Handle form submission.
   * Sends pet data to backend and navigates to seller dashboard.
   */
  const handleSubmit = async (formData) => {
    setLoading(true);
    setError(null);
    setSuccessMessage('');

    try {
      const response = await fetch('/api/pets', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to create pet listing');
      }

      const petData = await response.json();
      setSuccessMessage('Pet listing created successfully!');
      
      // Redirect to seller dashboard after success message
      setTimeout(() => {
        navigate('/seller-dashboard');
      }, 2000);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Redirect unauthenticated users
  if (!isAuthenticated) {
    return (
      <div className="create-listing-page">
        <div className="container">
          <div className="auth-required-message">
            <h1>Create Pet Listing</h1>
            <p>You must be logged in as a seller to create pet listings.</p>
            <button onClick={() => navigate('/login')} className="btn btn-primary">
              Login
            </button>
            <p>
              Not a seller yet? <a href="/become-seller">Become a seller</a>
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="create-listing-page">
      <div className="container">
        {/* Page Header */}
        <div className="page-header">
          <h1>Create New Pet Listing</h1>
          <p>Fill in the details about your pet and upload photos</p>
        </div>

        {/* Error Message */}
        {error && (
          <div className="alert alert-error">
            <strong>Error:</strong> {error}
            <button onClick={() => setError(null)} className="close-button">×</button>
          </div>
        )}

        {/* Success Message */}
        {successMessage && (
          <div className="alert alert-success">
            <strong>Success:</strong> {successMessage}
            <button onClick={() => setSuccessMessage('')} className="close-button">×</button>
          </div>
        )}

        {/* Form */}
        {!successMessage && (
          <div className="form-container">
            <PetListingForm 
              onSubmit={handleSubmit}
              isLoading={loading}
              mode="create"
            />
          </div>
        )}

        {/* Success View */}
        {successMessage && (
          <div className="success-section">
            <div className="success-icon">✓</div>
            <h2>Listing Created!</h2>
            <p>Your pet listing has been successfully created and is now visible to buyers.</p>
            <p className="redirect-message">Redirecting to your dashboard...</p>
          </div>
        )}
      </div>

      <style jsx>{`
        .create-listing-page {
          min-height: calc(100vh - 200px);
          padding: 40px 20px;
          background: #f8f9fa;
        }

        .container {
          max-width: 1000px;
          margin: 0 auto;
        }

        .page-header {
          text-align: center;
          margin-bottom: 40px;
        }

        .page-header h1 {
          font-size: 2rem;
          margin-bottom: 10px;
          color: #333;
        }

        .page-header p {
          font-size: 1rem;
          color: #666;
        }

        .auth-required-message {
          text-align: center;
          background: white;
          padding: 60px 40px;
          border-radius: 10px;
          box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        .auth-required-message h1 {
          margin-bottom: 20px;
        }

        .auth-required-message p {
          color: #666;
          margin-bottom: 20px;
        }

        .auth-required-message a {
          color: #007bff;
          text-decoration: none;
        }

        .auth-required-message a:hover {
          text-decoration: underline;
        }

        .btn {
          padding: 10px 20px;
          border: none;
          border-radius: 5px;
          font-size: 1rem;
          cursor: pointer;
          transition: all 0.3s;
        }

        .btn-primary {
          background: #007bff;
          color: white;
          margin-bottom: 20px;
        }

        .btn-primary:hover {
          background: #0056b3;
        }

        .alert {
          padding: 15px 20px;
          margin-bottom: 30px;
          border-radius: 5px;
          display: flex;
          justify-content: space-between;
          align-items: center;
        }

        .alert-error {
          background: #f8d7da;
          color: #721c24;
          border: 1px solid #f5c6cb;
        }

        .alert-success {
          background: #d4edda;
          color: #155724;
          border: 1px solid #c3e6cb;
        }

        .close-button {
          background: none;
          border: none;
          font-size: 1.5rem;
          cursor: pointer;
          color: inherit;
          padding: 0;
        }

        .form-container {
          background: white;
          padding: 40px;
          border-radius: 10px;
          box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        .success-section {
          text-align: center;
          background: white;
          padding: 60px 40px;
          border-radius: 10px;
          box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        .success-icon {
          font-size: 4rem;
          color: #28a745;
          margin-bottom: 20px;
          display: block;
        }

        .success-section h2 {
          color: #333;
          margin-bottom: 15px;
        }

        .success-section p {
          color: #666;
          margin-bottom: 10px;
        }

        .redirect-message {
          font-style: italic;
          color: #999;
          margin-top: 20px;
        }

        @media (max-width: 768px) {
          .create-listing-page {
            padding: 20px 10px;
          }

          .page-header h1 {
            font-size: 1.5rem;
          }

          .form-container {
            padding: 20px;
          }
        }
      `}</style>
    </div>
  );
}
