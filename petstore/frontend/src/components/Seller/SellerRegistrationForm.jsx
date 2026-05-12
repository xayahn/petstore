import React, { useState } from 'react';

/**
 * SellerRegistrationForm Component
 * 
 * Form for capturing seller business information during registration:
 * - business_name: Name of the seller business
 * - bio: Description of the business
 * - return_policy: Returns and refunds policy
 * 
 * Props:
 *  - onSubmit: (formData) => void - Called with form data on successful validation
 *  - isLoading: boolean - Shows loading state while submitting
 */
export default function SellerRegistrationForm({ onSubmit, isLoading = false }) {
  const [formData, setFormData] = useState({
    businessName: '',
    bio: '',
    returnPolicy: '',
  });

  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});

  /**
   * Validate form data.
   * Returns object with field errors or null if valid.
   */
  const validate = () => {
    const newErrors = {};

    // Business Name validation
    if (!formData.businessName?.trim()) {
      newErrors.businessName = 'Business name is required';
    } else if (formData.businessName.length < 3) {
      newErrors.businessName = 'Business name must be at least 3 characters';
    } else if (formData.businessName.length > 100) {
      newErrors.businessName = 'Business name must not exceed 100 characters';
    }

    // Bio validation
    if (!formData.bio?.trim()) {
      newErrors.bio = 'Business description is required';
    } else if (formData.bio.length < 10) {
      newErrors.bio = 'Description must be at least 10 characters';
    } else if (formData.bio.length > 1000) {
      newErrors.bio = 'Description must not exceed 1000 characters';
    }

    // Return Policy validation
    if (!formData.returnPolicy?.trim()) {
      newErrors.returnPolicy = 'Return policy is required';
    } else if (formData.returnPolicy.length < 20) {
      newErrors.returnPolicy = 'Return policy must be at least 20 characters';
    } else if (formData.returnPolicy.length > 2000) {
      newErrors.returnPolicy = 'Return policy must not exceed 2000 characters';
    }

    return Object.keys(newErrors).length > 0 ? newErrors : null;
  };

  /**
   * Handle form input changes.
   */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));

    // Clear error for this field when user starts typing
    if (touched[name]) {
      const validationErrors = validate();
      setErrors(validationErrors || {});
    }
  };

  /**
   * Handle field blur event to mark as touched and validate.
   */
  const handleBlur = (e) => {
    const { name } = e.target;
    setTouched(prev => ({
      ...prev,
      [name]: true,
    }));

    const validationErrors = validate();
    setErrors(validationErrors || {});
  };

  /**
   * Handle form submission.
   */
  const handleSubmit = (e) => {
    e.preventDefault();

    // Mark all fields as touched
    setTouched({
      businessName: true,
      bio: true,
      returnPolicy: true,
    });

    // Validate
    const validationErrors = validate();
    if (validationErrors) {
      setErrors(validationErrors);
      return;
    }

    // Submit
    onSubmit({
      businessName: formData.businessName.trim(),
      bio: formData.bio.trim(),
      returnPolicy: formData.returnPolicy.trim(),
    });
  };

  return (
    <form onSubmit={handleSubmit} className="seller-registration-form">
      <h2>Tell Us About Your Business</h2>

      {/* Business Name Field */}
      <div className="form-group">
        <label htmlFor="businessName">
          Business Name
          <span className="required">*</span>
        </label>
        <input
          type="text"
          id="businessName"
          name="businessName"
          value={formData.businessName}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="Enter your business name"
          maxLength={100}
          disabled={isLoading}
          className={`form-control ${errors.businessName && touched.businessName ? 'is-invalid' : ''}`}
        />
        <div className="form-text">
          {formData.businessName.length}/100 characters
        </div>
        {errors.businessName && touched.businessName && (
          <div className="form-error">
            <small>{errors.businessName}</small>
          </div>
        )}
      </div>

      {/* Bio Field */}
      <div className="form-group">
        <label htmlFor="bio">
          Business Description
          <span className="required">*</span>
        </label>
        <textarea
          id="bio"
          name="bio"
          value={formData.bio}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="Describe your business, the pets you sell, and what makes you unique"
          maxLength={1000}
          rows={5}
          disabled={isLoading}
          className={`form-control ${errors.bio && touched.bio ? 'is-invalid' : ''}`}
        />
        <div className="form-text">
          {formData.bio.length}/1000 characters
        </div>
        {errors.bio && touched.bio && (
          <div className="form-error">
            <small>{errors.bio}</small>
          </div>
        )}
      </div>

      {/* Return Policy Field */}
      <div className="form-group">
        <label htmlFor="returnPolicy">
          Return & Refund Policy
          <span className="required">*</span>
        </label>
        <textarea
          id="returnPolicy"
          name="returnPolicy"
          value={formData.returnPolicy}
          onChange={handleChange}
          onBlur={handleBlur}
          placeholder="Outline your return, refund, and exchange policies for customers"
          maxLength={2000}
          rows={6}
          disabled={isLoading}
          className={`form-control ${errors.returnPolicy && touched.returnPolicy ? 'is-invalid' : ''}`}
        />
        <div className="form-text">
          {formData.returnPolicy.length}/2000 characters
        </div>
        {errors.returnPolicy && touched.returnPolicy && (
          <div className="form-error">
            <small>{errors.returnPolicy}</small>
          </div>
        )}
      </div>

      {/* Submit Button */}
      <button
        type="submit"
        className="btn btn-primary btn-block"
        disabled={isLoading}
      >
        {isLoading ? 'Creating Seller Account...' : 'Continue to Email Verification'}
      </button>

      <style jsx>{`
        .seller-registration-form {
          width: 100%;
        }

        .seller-registration-form h2 {
          margin-bottom: 30px;
          color: #333;
          font-size: 1.5rem;
        }

        .form-group {
          margin-bottom: 25px;
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
          padding: 10px 12px;
          border: 1px solid #ddd;
          border-radius: 5px;
          font-size: 1rem;
          font-family: inherit;
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

        textarea.form-control {
          resize: vertical;
          min-height: 100px;
        }

        .form-text {
          font-size: 0.85rem;
          color: #999;
          margin-top: 5px;
        }

        .form-error {
          margin-top: 5px;
        }

        .form-error small {
          color: #dc3545;
          display: block;
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
          margin-top: 10px;
        }

        @media (max-width: 768px) {
          .seller-registration-form {
            padding: 0;
          }

          .seller-registration-form h2 {
            font-size: 1.25rem;
          }

          .form-group {
            margin-bottom: 20px;
          }

          .form-control {
            padding: 10px;
            font-size: 16px; /* Prevents zoom on mobile */
          }

          textarea.form-control {
            resize: none;
          }
        }
      `}</style>
    </form>
  );
}
