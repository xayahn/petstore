import React, { useState } from 'react';
import './ErrorAlert.css';

/**
 * ErrorAlert component.
 * 
 * Displays an error message with dismiss functionality.
 * Can show different severity levels (error, warning, info).
 * 
 * @param {string} message - Error message to display
 * @param {string} [severity='error'] - Alert severity level (error, warning, info)
 * @param {function} [onDismiss] - Callback when alert is dismissed
 * @returns {JSX.Element} Error alert UI
 */
function ErrorAlert({ message, severity = 'error', onDismiss }) {
  const [visible, setVisible] = useState(true);

  const handleDismiss = () => {
    setVisible(false);
    if (onDismiss) {
      onDismiss();
    }
  };

  if (!visible || !message) return null;

  return (
    <div className={`error-alert error-alert-${severity}`}>
      <div className="error-alert-content">
        <span className="error-alert-icon">
          {severity === 'error' && '✕'}
          {severity === 'warning' && '⚠'}
          {severity === 'info' && 'ℹ'}
        </span>
        <span className="error-alert-message">{message}</span>
      </div>
      <button
        className="error-alert-close"
        onClick={handleDismiss}
        aria-label="Close alert"
      >
        ✕
      </button>
    </div>
  );
}

export default ErrorAlert;
