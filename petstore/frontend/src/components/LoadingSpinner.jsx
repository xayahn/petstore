import React from 'react';
import './LoadingSpinner.css';

/**
 * LoadingSpinner component.
 * 
 * Displays a loading spinner animation while content is loading.
 * Can be used globally or for specific components.
 * 
 * @param {boolean} [isLoading=true] - Whether to show the spinner
 * @param {string} [message='Loading...'] - Message to display with spinner
 * @returns {JSX.Element} Loading spinner UI
 */
function LoadingSpinner({ isLoading = true, message = 'Loading...' }) {
  if (!isLoading) return null;

  return (
    <div className="loading-spinner-overlay">
      <div className="loading-spinner-container">
        <div className="spinner"></div>
        <p className="loading-message">{message}</p>
      </div>
    </div>
  );
}

export default LoadingSpinner;
