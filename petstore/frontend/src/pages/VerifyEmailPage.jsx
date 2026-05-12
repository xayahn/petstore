import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

/**
 * VerifyEmailPage Component
 * 
 * Standalone page for verifying seller email via token from email link.
 * Can be accessed via URL: /verify-email?token=XXXX
 * 
 * Workflow:
 * 1. Auto-extracts token from URL query parameter
 * 2. User can manually enter token or click email link
 * 3. Shows status: pending, verifying, success, or error
 */
export default function VerifyEmailPage() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [searchParams] = useSearchParams();

  const [status, setStatus] = useState('ready'); // 'ready', 'verifying', 'success', 'error'
  const [verificationToken, setVerificationToken] = useState('');
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');

  // Extract token from URL if present
  useEffect(() => {
    const urlToken = searchParams.get('token');
    if (urlToken) {
      setVerificationToken(urlToken);
      // Auto-verify if token is in URL
      verifyEmail(urlToken);
    }
  }, [searchParams]);

  /**
   * Verify email with the provided token.
   */
  const verifyEmail = async (token) => {
    setStatus('verifying');
    setError(null);
    setSuccessMessage('');

    try {
      const response = await fetch('/api/sellers/verify-email', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(isAuthenticated && { 'Authorization': `Bearer ${localStorage.getItem('token')}` }),
        },
        body: JSON.stringify({
          token: token,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to verify email');
      }

      setStatus('success');
      setSuccessMessage('Email verified successfully! Redirecting...');
      
      // Redirect after 2 seconds
      setTimeout(() => {
        if (isAuthenticated) {
          navigate('/seller-dashboard');
        } else {
          navigate('/');
        }
      }, 2000);
    } catch (err) {
      setStatus('error');
      setError(err.message);
    }
  };

  /**
   * Handle form submission for manual token entry.
   */
  const handleSubmit = (e) => {
    e.preventDefault();

    if (!verificationToken.trim()) {
      setError('Verification code is required');
      return;
    }

    verifyEmail(verificationToken.trim());
  };

  return (
    <div className="verify-email-page">
      <div className="container">
        <div className="card">
          {/* Ready State */}
          {status === 'ready' && (
            <>
              <div className="card-header">
                <h1>Verify Your Email</h1>
              </div>
              <div className="card-body">
                <p className="description">
                  If you didn't receive a verification email, you can enter your code below.
                </p>

                <form onSubmit={handleSubmit}>
                  <div className="form-group">
                    <label htmlFor="tokenInput">
                      Verification Code
                      <span className="required">*</span>
                    </label>
                    <input
                      type="text"
                      id="tokenInput"
                      value={verificationToken}
                      onChange={(e) => setVerificationToken(e.target.value)}
                      placeholder="Enter verification code from your email"
                      className="form-control"
                      maxLength={50}
                      autoComplete="off"
                    />
                    <small className="form-text">
                      Check your email inbox (and spam folder) for the verification code
                    </small>
                  </div>

                  <button type="submit" className="btn btn-primary btn-block">
                    Verify Email
                  </button>
                </form>

                <div className="help-section">
                  <p className="help-text">
                    Need help? <a href="/contact-support">Contact support</a>
                  </p>
                </div>
              </div>
            </>
          )}

          {/* Verifying State */}
          {status === 'verifying' && (
            <>
              <div className="card-header">
                <h1>Verifying Your Email</h1>
              </div>
              <div className="card-body text-center">
                <div className="spinner"></div>
                <p>Please wait while we verify your email...</p>
              </div>
            </>
          )}

          {/* Success State */}
          {status === 'success' && (
            <>
              <div className="card-header success">
                <h1>✓ Email Verified</h1>
              </div>
              <div className="card-body text-center">
                <div className="success-icon">✓</div>
                <p className="success-message">{successMessage}</p>
                <p className="redirect-text">
                  If you are not redirected automatically, <a href="/seller-dashboard">click here</a>.
                </p>
              </div>
            </>
          )}

          {/* Error State */}
          {status === 'error' && (
            <>
              <div className="card-header error">
                <h1>✕ Verification Failed</h1>
              </div>
              <div className="card-body">
                <div className="alert alert-error">
                  <strong>Error:</strong> {error}
                </div>

                <p className="error-description">
                  The verification code may have expired or is invalid. Please try again or request a new code.
                </p>

                <form onSubmit={handleSubmit}>
                  <div className="form-group">
                    <label htmlFor="tokenInput">
                      Try Again
                      <span className="required">*</span>
                    </label>
                    <input
                      type="text"
                      id="tokenInput"
                      value={verificationToken}
                      onChange={(e) => setVerificationToken(e.target.value)}
                      placeholder="Enter verification code"
                      className="form-control"
                      maxLength={50}
                      autoComplete="off"
                    />
                  </div>

                  <button type="submit" className="btn btn-primary btn-block">
                    Verify Email
                  </button>
                </form>

                <div className="help-section">
                  <p className="help-text">
                    <a href="/become-seller">Back to seller registration</a> | 
                    <a href="/"> Go home</a>
                  </p>
                </div>
              </div>
            </>
          )}
        </div>
      </div>

      <style jsx>{`
        .verify-email-page {
          min-height: calc(100vh - 200px);
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 20px;
          background: #f8f9fa;
        }

        .container {
          max-width: 500px;
          width: 100%;
        }

        .card {
          background: white;
          border-radius: 10px;
          box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
          overflow: hidden;
        }

        .card-header {
          background: #f8f9fa;
          padding: 30px 20px;
          border-bottom: 1px solid #eee;
        }

        .card-header.success {
          background: #d4edda;
          border-bottom: 1px solid #c3e6cb;
        }

        .card-header.error {
          background: #f8d7da;
          border-bottom: 1px solid #f5c6cb;
        }

        .card-header h1 {
          margin: 0;
          font-size: 1.5rem;
          color: #333;
        }

        .card-header.success h1 {
          color: #155724;
        }

        .card-header.error h1 {
          color: #721c24;
        }

        .card-body {
          padding: 30px 20px;
        }

        .card-body.text-center {
          text-align: center;
        }

        .description {
          color: #666;
          margin-bottom: 25px;
          line-height: 1.6;
        }

        .form-group {
          margin-bottom: 20px;
        }

        .form-group label {
          display: block;
          margin-bottom: 8px;
          font-weight: 600;
          color: #333;
        }

        .required {
          color: #dc3545;
          margin-left: 3px;
        }

        .form-control {
          width: 100%;
          padding: 10px 12px;
          border: 1px solid #ddd;
          border-radius: 5px;
          font-size: 1rem;
          transition: border-color 0.3s, box-shadow 0.3s;
          box-sizing: border-box;
        }

        .form-control:focus {
          outline: none;
          border-color: #007bff;
          box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
        }

        .form-text {
          font-size: 0.85rem;
          color: #999;
          display: block;
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
        }

        .btn-primary:hover {
          background: #0056b3;
          box-shadow: 0 2px 8px rgba(0, 86, 179, 0.3);
        }

        .btn-block {
          width: 100%;
        }

        .spinner {
          display: inline-block;
          width: 40px;
          height: 40px;
          border: 4px solid #f3f3f3;
          border-top: 4px solid #007bff;
          border-radius: 50%;
          animation: spin 1s linear infinite;
          margin-bottom: 20px;
        }

        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }

        .success-icon {
          font-size: 3rem;
          color: #28a745;
          margin-bottom: 15px;
          display: block;
        }

        .success-message {
          color: #155724;
          font-weight: 600;
          margin-bottom: 10px;
        }

        .redirect-text {
          color: #666;
          margin-top: 10px;
        }

        .redirect-text a {
          color: #007bff;
          text-decoration: none;
        }

        .redirect-text a:hover {
          text-decoration: underline;
        }

        .alert {
          padding: 15px 20px;
          margin-bottom: 20px;
          border-radius: 5px;
          background: #f8d7da;
          color: #721c24;
          border: 1px solid #f5c6cb;
        }

        .alert strong {
          display: block;
          margin-bottom: 5px;
        }

        .error-description {
          color: #666;
          margin-bottom: 20px;
          line-height: 1.6;
        }

        .help-section {
          margin-top: 20px;
          padding-top: 20px;
          border-top: 1px solid #eee;
          text-align: center;
        }

        .help-text {
          color: #999;
          font-size: 0.9rem;
          margin: 0;
        }

        .help-text a {
          color: #007bff;
          text-decoration: none;
          margin: 0 5px;
        }

        .help-text a:hover {
          text-decoration: underline;
        }

        @media (max-width: 768px) {
          .verify-email-page {
            padding: 10px;
          }

          .card-header {
            padding: 20px;
          }

          .card-body {
            padding: 20px;
          }

          .card-header h1 {
            font-size: 1.25rem;
          }
        }
      `}</style>
    </div>
  );
}
