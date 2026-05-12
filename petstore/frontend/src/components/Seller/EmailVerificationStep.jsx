import React, { useState, useEffect } from 'react';

/**
 * EmailVerificationStep Component
 * 
 * Component for verifying seller email during registration.
 * Displays the email to verify and prompts user to enter verification token.
 * 
 * Props:
 *  - email: string - Email address to verify
 *  - onVerificationComplete: () => void - Called when verification succeeds
 */
export default function EmailVerificationStep({ email, onVerificationComplete }) {
  const [verificationToken, setVerificationToken] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [resendCountdown, setResendCountdown] = useState(0);
  const [successMessage, setSuccessMessage] = useState('');

  // Countdown timer for resend button
  useEffect(() => {
    if (resendCountdown > 0) {
      const timer = setTimeout(() => setResendCountdown(resendCountdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [resendCountdown]);

  /**
   * Handle verification token submission.
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage('');

    if (!verificationToken.trim()) {
      setError('Verification code is required');
      return;
    }

    setLoading(true);

    try {
      const response = await fetch('/api/sellers/verify-email', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          token: verificationToken.trim(),
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to verify email');
      }

      setSuccessMessage('Email verified successfully!');
      setVerificationToken('');
      
      // Call completion callback after showing success message
      setTimeout(() => {
        onVerificationComplete();
      }, 1500);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Handle resend verification email.
   */
  const handleResendEmail = async () => {
    setError(null);
    setSuccessMessage('');
    setResendCountdown(60); // 60 second cooldown

    try {
      const response = await fetch('/api/sellers/resend-verification-email', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          email: email,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to resend verification email');
      }

      setSuccessMessage('Verification code sent to ' + email);
    } catch (err) {
      setError(err.message);
      setResendCountdown(0);
    }
  };

  return (
    <div className="email-verification-step">
      <h2>Verify Your Email</h2>
      <p className="verification-description">
        We've sent a verification code to <strong>{email}</strong>. 
        Enter the code below to verify your email address.
      </p>

      {/* Error Message */}
      {error && (
        <div className="alert alert-error">
          <strong>Error:</strong> {error}
        </div>
      )}

      {/* Success Message */}
      {successMessage && (
        <div className="alert alert-success">
          <strong>Success:</strong> {successMessage}
        </div>
      )}

      {/* Verification Form */}
      <form onSubmit={handleSubmit} className="verification-form">
        <div className="form-group">
          <label htmlFor="verificationToken">
            Verification Code
            <span className="required">*</span>
          </label>
          <input
            type="text"
            id="verificationToken"
            value={verificationToken}
            onChange={(e) => setVerificationToken(e.target.value.toUpperCase())}
            placeholder="Enter 6-8 character code from your email"
            disabled={loading}
            className={`form-control verification-input ${error ? 'is-invalid' : ''}`}
            maxLength={20}
            autoComplete="off"
          />
          <div className="form-text">
            Check your email inbox and spam folder for the verification code
          </div>
        </div>

        <button
          type="submit"
          className="btn btn-primary btn-block"
          disabled={loading || !verificationToken.trim()}
        >
          {loading ? 'Verifying...' : 'Verify Email'}
        </button>
      </form>

      {/* Resend Email Link */}
      <div className="resend-section">
        <p className="resend-text">Didn't receive the code?</p>
        <button
          type="button"
          onClick={handleResendEmail}
          className="btn btn-link"
          disabled={resendCountdown > 0 || loading}
        >
          {resendCountdown > 0
            ? `Resend code in ${resendCountdown}s`
            : 'Resend Verification Code'}
        </button>
      </div>

      <style jsx>{`
        .email-verification-step {
          width: 100%;
        }

        .email-verification-step h2 {
          margin-bottom: 20px;
          color: #333;
          font-size: 1.5rem;
        }

        .verification-description {
          color: #666;
          margin-bottom: 30px;
          line-height: 1.6;
          font-size: 1rem;
        }

        .verification-description strong {
          color: #333;
        }

        .alert {
          padding: 15px 20px;
          margin-bottom: 25px;
          border-radius: 5px;
          display: flex;
          align-items: flex-start;
          gap: 10px;
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

        .alert strong {
          display: block;
          margin-bottom: 5px;
        }

        .verification-form {
          margin-bottom: 30px;
        }

        .form-group {
          margin-bottom: 20px;
        }

        .form-group label {
          display: block;
          margin-bottom: 8px;
          font-weight: 600;
          color: #333;
          font-size: 1rem;
        }

        .required {
          color: #dc3545;
          margin-left: 3px;
        }

        .form-control {
          width: 100%;
          padding: 12px 15px;
          border: 2px solid #ddd;
          border-radius: 5px;
          font-size: 1.1rem;
          letter-spacing: 2px;
          text-transform: uppercase;
          font-family: monospace;
          transition: border-color 0.3s, box-shadow 0.3s;
          box-sizing: border-box;
        }

        .form-control:focus {
          outline: none;
          border-color: #007bff;
          box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
        }

        .form-control:disabled {
          background-color: #e9ecef;
          cursor: not-allowed;
        }

        .form-control.is-invalid {
          border-color: #dc3545;
        }

        .form-control.is-invalid:focus {
          box-shadow: 0 0 0 3px rgba(220, 53, 69, 0.25);
        }

        .form-text {
          font-size: 0.85rem;
          color: #999;
          margin-top: 5px;
        }

        .btn {
          padding: 12px 20px;
          border: none;
          border-radius: 5px;
          font-size: 1rem;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.3s;
        }

        .btn-primary {
          background: #007bff;
          color: white;
          width: 100%;
        }

        .btn-primary:hover:not(:disabled) {
          background: #0056b3;
          box-shadow: 0 2px 8px rgba(0, 86, 179, 0.3);
        }

        .btn-primary:disabled {
          background: #6c757d;
          cursor: not-allowed;
          opacity: 0.65;
        }

        .btn-block {
          width: 100%;
        }

        .resend-section {
          text-align: center;
          padding-top: 20px;
          border-top: 1px solid #eee;
        }

        .resend-text {
          color: #666;
          margin-bottom: 10px;
          font-size: 0.95rem;
        }

        .btn-link {
          background: none;
          border: none;
          color: #007bff;
          padding: 0;
          font-weight: 600;
          cursor: pointer;
          text-decoration: underline;
          transition: color 0.3s;
        }

        .btn-link:hover:not(:disabled) {
          color: #0056b3;
        }

        .btn-link:disabled {
          color: #999;
          cursor: not-allowed;
        }

        @media (max-width: 768px) {
          .email-verification-step h2 {
            font-size: 1.25rem;
          }

          .verification-description {
            font-size: 0.95rem;
          }

          .form-control {
            font-size: 1rem;
            padding: 10px 12px;
          }
        }
      `}</style>
    </div>
  );
}
