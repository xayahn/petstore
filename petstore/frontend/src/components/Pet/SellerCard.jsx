import React from 'react';
import '@styles/index.css';

/**
 * SellerCard Component
 * 
 * Display seller information with name, rating, and verified badge.
 */
export default function SellerCard({ seller }) {
  if (!seller) {
    return <div className="seller-card error">Seller information not available</div>;
  }

  return (
    <div className="seller-card">
      <div className="seller-header">
        <div className="seller-avatar">
          {seller.name ? seller.name.charAt(0).toUpperCase() : 'S'}
        </div>
        <div className="seller-info">
          <h3>{seller.name || 'Unknown Seller'}</h3>
          {seller.verified && <span className="verified-badge">✓ Verified</span>}
        </div>
      </div>

      {seller.rating !== null && seller.rating !== undefined && (
        <div className="seller-stats">
          <div className="stat">
            <span className="stars">
              {'⭐'.repeat(Math.floor(seller.rating))}
              {seller.rating % 1 !== 0 && '✨'}
            </span>
            <span className="rating-text">{seller.rating.toFixed(1)} Rating</span>
          </div>
          {seller.reviewCount && (
            <div className="stat">
              <span className="review-count">{seller.reviewCount} Reviews</span>
            </div>
          )}
        </div>
      )}

      {seller.description && <p className="seller-description">{seller.description}</p>}

      <button className="contact-seller-btn">Contact Seller</button>

      <style jsx>{`
        .seller-card {
          background: #f8f9fa;
          border: 2px solid #e0e0e0;
          border-radius: 8px;
          padding: 20px;
        }

        .seller-card.error {
          background: #ffe0e0;
          border-color: #ffb3b3;
          color: #c00;
          padding: 20px;
          text-align: center;
        }

        .seller-header {
          display: flex;
          gap: 15px;
          margin-bottom: 15px;
          align-items: flex-start;
        }

        .seller-avatar {
          width: 50px;
          height: 50px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 1.5rem;
          font-weight: bold;
          flex-shrink: 0;
        }

        .seller-info {
          flex: 1;
        }

        .seller-info h3 {
          margin: 0 0 5px 0;
          color: #333;
          font-size: 1.1rem;
        }

        .verified-badge {
          display: inline-block;
          background: #e8f5e9;
          color: #2e7d32;
          padding: 3px 8px;
          border-radius: 12px;
          font-size: 0.8rem;
          font-weight: 600;
        }

        .seller-stats {
          display: flex;
          gap: 20px;
          margin-bottom: 15px;
          padding: 15px 0;
          border-top: 1px solid #e0e0e0;
          border-bottom: 1px solid #e0e0e0;
        }

        .stat {
          display: flex;
          flex-direction: column;
          gap: 5px;
        }

        .stars {
          font-size: 1rem;
        }

        .rating-text {
          color: #666;
          font-size: 0.9rem;
          font-weight: 600;
        }

        .review-count {
          color: #999;
          font-size: 0.9rem;
        }

        .seller-description {
          color: #666;
          font-size: 0.95rem;
          line-height: 1.5;
          margin: 15px 0;
        }

        .contact-seller-btn {
          width: 100%;
          padding: 12px;
          background: #667eea;
          color: white;
          border: none;
          border-radius: 4px;
          font-weight: 600;
          cursor: pointer;
          transition: background 0.3s;
        }

        .contact-seller-btn:hover {
          background: #764ba2;
        }

        @media (max-width: 768px) {
          .seller-card {
            padding: 15px;
          }

          .seller-stats {
            gap: 15px;
          }
        }
      `}</style>
    </div>
  );
}
