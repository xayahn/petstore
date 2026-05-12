import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import petService from '@services/petService';
import PetImageCarousel from './PetImageCarousel';
import SellerCard from './SellerCard';
import LoadingSpinner from '../LoadingSpinner';
import ErrorAlert from '../ErrorAlert';
import '@styles/index.css';

/**
 * PetDetailPage Component
 * 
 * Full pet detail page showing complete pet information, images, and seller details.
 */
export default function PetDetailPage() {
  const { id } = useParams();
  const [pet, setPet] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchPetDetail();
  }, [id]);

  const fetchPetDetail = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await petService.getPetDetail(id);
      setPet(data);
    } catch (err) {
      setError('Failed to load pet details. Please try again.');
      console.error('Error fetching pet:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner message="Loading pet details..." />;
  }

  if (error || !pet) {
    return (
      <div className="detail-page">
        {error && <ErrorAlert message={error} severity="error" />}
        {!error && <ErrorAlert message="Pet not found." severity="error" />}
      </div>
    );
  }

  const images = pet.images || [];
  const seller = pet.seller || {};

  return (
    <div className="detail-page">
      <button className="back-btn" onClick={() => window.history.back()}>
        ← Back to Browse
      </button>

      <div className="detail-container">
        <div className="detail-gallery">
          <PetImageCarousel images={images} />
        </div>

        <div className="detail-content">
          <div className="detail-header">
            <div>
              <h1>{pet.name}</h1>
              <p className="pet-category">
                {pet.species}
                {pet.breed && ` • ${pet.breed}`}
              </p>
            </div>
            {pet.availabilityStatus === 'AVAILABLE' && (
              <span className="availability-badge available">Available</span>
            )}
            {pet.availabilityStatus === 'UNAVAILABLE' && (
              <span className="availability-badge unavailable">Unavailable</span>
            )}
          </div>

          <div className="detail-price">
            <span className="price">${pet.price?.toFixed(2) || 'Contact for price'}</span>
            {pet.stockQuantity && (
              <span className="stock-info">{pet.stockQuantity} available</span>
            )}
          </div>

          <div className="detail-info-grid">
            <div className="info-item">
              <span className="label">Age</span>
              <span className="value">{pet.age} months</span>
            </div>
            <div className="info-item">
              <span className="label">Health Status</span>
              <span className="value">{pet.healthStatus || 'Not specified'}</span>
            </div>
            {pet.stockQuantity && (
              <div className="info-item">
                <span className="label">In Stock</span>
                <span className="value">{pet.stockQuantity}</span>
              </div>
            )}
          </div>

          {pet.description && (
            <div className="detail-section">
              <h3>Description</h3>
              <p className="description">{pet.description}</p>
            </div>
          )}

          <div className="detail-actions">
            <button className="action-btn primary">Add to Cart</button>
            <button className="action-btn secondary">Add to Favorites</button>
          </div>

          <div className="detail-seller">
            <h3>Seller Information</h3>
            <SellerCard seller={seller} />
          </div>
        </div>
      </div>

      <style jsx>{`
        .detail-page {
          min-height: 100vh;
          padding: 40px 20px;
          background: white;
        }

        .back-btn {
          background: none;
          border: none;
          color: #667eea;
          font-size: 1rem;
          cursor: pointer;
          margin-bottom: 20px;
          font-weight: 600;
          transition: color 0.3s;
        }

        .back-btn:hover {
          color: #764ba2;
        }

        .detail-container {
          max-width: 1200px;
          margin: 0 auto;
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 50px;
        }

        .detail-gallery {
          position: sticky;
          top: 100px;
          height: fit-content;
        }

        .detail-content {
          padding-top: 0;
        }

        .detail-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 20px;
          gap: 20px;
        }

        .detail-header h1 {
          margin: 0 0 10px 0;
          font-size: 2rem;
          color: #333;
        }

        .pet-category {
          color: #666;
          margin: 0;
          font-size: 1rem;
        }

        .availability-badge {
          padding: 6px 12px;
          border-radius: 20px;
          font-weight: 600;
          font-size: 0.9rem;
        }

        .availability-badge.available {
          background: #e8f5e9;
          color: #2e7d32;
        }

        .availability-badge.unavailable {
          background: #ffebee;
          color: #c62828;
        }

        .detail-price {
          display: flex;
          align-items: center;
          gap: 20px;
          margin-bottom: 30px;
          padding-bottom: 20px;
          border-bottom: 2px solid #e0e0e0;
        }

        .price {
          font-size: 2rem;
          font-weight: bold;
          color: #667eea;
        }

        .stock-info {
          color: #666;
          font-size: 0.95rem;
        }

        .detail-info-grid {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 20px;
          margin-bottom: 30px;
          padding: 20px;
          background: #f8f9fa;
          border-radius: 8px;
        }

        .info-item {
          display: flex;
          flex-direction: column;
          gap: 5px;
        }

        .info-item .label {
          color: #999;
          font-size: 0.85rem;
          font-weight: 600;
          text-transform: uppercase;
        }

        .info-item .value {
          color: #333;
          font-size: 1rem;
          font-weight: 600;
        }

        .detail-section {
          margin-bottom: 30px;
        }

        .detail-section h3 {
          margin: 0 0 15px 0;
          color: #333;
          font-size: 1.2rem;
        }

        .description {
          color: #666;
          line-height: 1.6;
          margin: 0;
        }

        .detail-actions {
          display: flex;
          gap: 15px;
          margin-bottom: 40px;
        }

        .action-btn {
          flex: 1;
          padding: 15px 20px;
          border: none;
          border-radius: 4px;
          font-weight: 600;
          font-size: 1rem;
          cursor: pointer;
          transition: all 0.3s;
        }

        .action-btn.primary {
          background: #667eea;
          color: white;
        }

        .action-btn.primary:hover {
          background: #764ba2;
        }

        .action-btn.secondary {
          background: white;
          border: 2px solid #667eea;
          color: #667eea;
        }

        .action-btn.secondary:hover {
          background: #f8f9fa;
        }

        .detail-seller {
          margin-top: 40px;
        }

        .detail-seller h3 {
          margin-bottom: 20px;
          color: #333;
          font-size: 1.2rem;
        }

        @media (max-width: 1024px) {
          .detail-container {
            grid-template-columns: 1fr;
            gap: 30px;
          }

          .detail-gallery {
            position: static;
          }
        }

        @media (max-width: 768px) {
          .detail-page {
            padding: 20px 10px;
          }

          .detail-header h1 {
            font-size: 1.5rem;
          }

          .detail-info-grid {
            grid-template-columns: 1fr;
          }

          .detail-actions {
            flex-direction: column;
          }
        }
      `}</style>
    </div>
  );
}
