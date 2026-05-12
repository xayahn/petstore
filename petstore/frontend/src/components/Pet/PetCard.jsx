import React from 'react';
import { Link } from 'react-router-dom';
import '@styles/index.css';

/**
 * PetCard Component
 * 
 * Displays a pet listing in a card format with image, name, breed, price, and seller info.
 * Used in catalog listings and featured pets sections.
 */
export default function PetCard({ pet, className = '' }) {
  if (!pet) return null;

  const placeholderImage = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200"%3E%3Crect fill="%23ddd" width="200" height="200"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999" font-size="14"%3ENo Image%3C/text%3E%3C/svg%3E';

  return (
    <Link to={`/pets/${pet.id}`} className="pet-card-link">
      <div className={`pet-card ${className}`}>
        <div className="pet-image-container">
          <img
            src={pet.primaryImage || placeholderImage}
            alt={pet.name}
            className="pet-image"
            onError={(e) => {
              e.target.src = placeholderImage;
            }}
          />
          {pet.availabilityStatus !== 'available' && (
            <div className="pet-badge unavailable">Not Available</div>
          )}
        </div>

        <div className="pet-info">
          <h3 className="pet-name">{pet.name}</h3>

          <p className="pet-breed">
            {pet.species}
            {pet.breed ? ` · ${pet.breed}` : ''}
          </p>

          <div className="pet-price-container">
            <span className="pet-price">${pet.price?.toFixed(2) || 'N/A'}</span>
          </div>

          {pet.sellerName && (
            <div className="pet-seller">
              <p className="seller-name">{pet.sellerName}</p>
              {pet.sellerRating && (
                <span className="seller-rating">
                  ★ {pet.sellerRating.toFixed(1)}
                </span>
              )}
            </div>
          )}
        </div>
      </div>
    </Link>
  );
}
