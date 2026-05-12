import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import SellerRegistrationForm from '../components/Seller/SellerRegistrationForm';
import EmailVerificationStep from '../components/Seller/EmailVerificationStep';
import useAuth from '../hooks/useAuth';

/**
 * BecomeSeller Page Component
 * 
 * Multi-step seller onboarding workflow:
 * 1. Business information form (business name, bio, return policy)
 * 2. Email verification via token
 * 3. Success confirmation
 * 
 * Used by: Router path=/become-seller
 * Requires: User to be authenticated as a buyer first
 */
export default function BecomeSeller() {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  
  const [step, setStep] = useState('registration'); // 'registration', 'email-verification', 'success'
  const [registrationData, setRegistrationData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  /**
   * Handle registration form submission.
   * Sends seller profile data to backend and advances to email verification step.
   */
  const handleRegistrationSubmit = async (formData) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await fetch('/api/sellers/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to register as seller');
      }

      const sellerData = await response.json();
      setRegistrationData(sellerData);
      setStep('email-verification');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Handle email verification completion.
   * Updates registration status and navigates to seller dashboard.
   */
  const handleVerificationComplete = () => {
    setStep('success');
    // Redirect to seller dashboard after 2 seconds
    setTimeout(() => {
      navigate('/seller-dashboard');
    }, 2000);
  };

  // Redirect unauthenticated users to login
  if (!isAuthenticated) {
    return (
      <div className="become-seller-page">
        <div className="container">
          <div className="not-authenticated-message">
            <h1>Become a Seller</h1>
            <p>You must be logged in to become a seller.</p>
            <button onClick={() => navigate('/login')} className="btn btn-primary">
              Login First
            </button>
            <p>
              Don't have an account? <a href="/register">Register here</a>
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="become-seller-page">
      <div className="container">
        {/* Header */}
        <div className="page-header">
          <h1>Become a Seller</h1>
          <p>Join our marketplace and start selling quality pets</p>
        </div>

        {/* Progress Indicator */}
        <div className="progress-steps">
          <div className={`step ${step === 'registration' || step === 'email-verification' || step === 'success' ? 'active' : ''}`}>
            <div className="step-number">1</div>
            <div className="step-label">Registration</div>
          </div>
          <div className={`step ${step === 'email-verification' || step === 'success' ? 'active' : ''}`}>
            <div className="step-number">2</div>
            <div className="step-label">Verify Email</div>
          </div>
          <div className={`step ${step === 'success' ? 'active' : ''}`}>
            <div className="step-number">3</div>
            <div className="step-label">Complete</div>
          </div>
        </div>

        {/* Error Message */}
        {error && (
          <div className="alert alert-error">
            <strong>Error:</strong> {error}
            <button onClick={() => setError(null)} className="close-button">×</button>
          </div>
        )}

        {/* Step 1: Registration Form */}
        {step === 'registration' && (
          <div className="step-content">
            <SellerRegistrationForm 
              onSubmit={handleRegistrationSubmit}
              isLoading={loading}
            />
          </div>
        )}

        {/* Step 2: Email Verification */}
        {step === 'email-verification' && registrationData && (
          <div className="step-content">
            <EmailVerificationStep
              email={registrationData.email || user?.email}
              onVerificationComplete={handleVerificationComplete}
            />
          </div>
        )}

        {/* Step 3: Success */}
        {step === 'success' && (
          <div className="step-content success-message">
            <div className="success-icon">✓</div>
            <h2>Welcome to Petstore Seller Platform!</h2>
            <p>Your seller account has been successfully verified.</p>
            <p className="redirect-message">Redirecting to your dashboard...</p>
          </div>
        )}
      </div>

      <style jsx>{`
        .become-seller-page {
          min-height: calc(100vh - 200px);
          padding: 40px 20px;
          background: #f8f9fa;
        }

        .container {
          max-width: 800px;
          margin: 0 auto;
        }

        .page-header {
          text-align: center;
          margin-bottom: 50px;
        }

        .page-header h1 {
          font-size: 2.5rem;
          margin-bottom: 10px;
          color: #333;
        }

        .page-header p {
          font-size: 1.1rem;
          color: #666;
        }

        .progress-steps {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 50px;
          position: relative;
        }

        .progress-steps::before {
          content: '';
          position: absolute;
          top: 20px;
          left: 0;
          right: 0;
          height: 2px;
          background: #ddd;
          z-index: 0;
        }

        .step {
          display: flex;
          flex-direction: column;
          align-items: center;
          position: relative;
          z-index: 1;
          flex: 1;
        }

        .step-number {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background: white;
          border: 2px solid #ddd;
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: bold;
          color: #999;
          margin-bottom: 10px;
          transition: all 0.3s;
        }

        .step.active .step-number {
          background: #007bff;
          border-color: #007bff;
          color: white;
        }

        .step-label {
          font-size: 0.9rem;
          color: #999;
          text-align: center;
        }

        .step.active .step-label {
          color: #007bff;
          font-weight: bold;
        }

        .step-content {
          background: white;
          padding: 40px;
          border-radius: 10px;
          box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        .not-authenticated-message {
          text-align: center;
          background: white;
          padding: 60px 40px;
          border-radius: 10px;
          box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        .not-authenticated-message h1 {
          margin-bottom: 20px;
        }

        .not-authenticated-message p {
          margin-bottom: 20px;
          color: #666;
        }

        .not-authenticated-message a {
          color: #007bff;
          text-decoration: none;
        }

        .not-authenticated-message a:hover {
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

        .close-button {
          background: none;
          border: none;
          font-size: 1.5rem;
          cursor: pointer;
          color: inherit;
          padding: 0;
        }

        .success-message {
          text-align: center;
          padding: 60px 40px;
        }

        .success-icon {
          font-size: 4rem;
          color: #28a745;
          margin-bottom: 20px;
          display: block;
        }

        .success-message h2 {
          color: #333;
          margin-bottom: 15px;
        }

        .success-message p {
          color: #666;
          margin-bottom: 10px;
        }

        .redirect-message {
          font-style: italic;
          color: #999;
          margin-top: 20px;
        }

        @media (max-width: 768px) {
          .become-seller-page {
            padding: 20px 10px;
          }

          .page-header h1 {
            font-size: 1.8rem;
          }

          .step-content {
            padding: 30px 20px;
          }

          .progress-steps {
            flex-wrap: wrap;
            gap: 20px;
          }

          .progress-steps::before {
            display: none;
          }
        }
      `}</style>
    </div>
  );
}
