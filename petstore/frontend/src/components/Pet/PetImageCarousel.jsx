import React, { useState } from 'react';
import '@styles/index.css';

/**
 * PetImageCarousel Component
 * 
 * Image carousel/slider for multiple pet images.
 * Shows one image at a time with prev/next navigation.
 */
export default function PetImageCarousel({ images }) {
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  if (!images || images.length === 0) {
    return (
      <div className="carousel-placeholder">
        <span>🖼️</span>
        <p>No images available</p>
      </div>
    );
  }

  const handlePrevImage = () => {
    setCurrentImageIndex((prev) => (prev === 0 ? images.length - 1 : prev - 1));
  };

  const handleNextImage = () => {
    setCurrentImageIndex((prev) => (prev === images.length - 1 ? 0 : prev + 1));
  };

  return (
    <div className="pet-carousel">
      <div className="carousel-main">
        <img
          src={images[currentImageIndex]}
          alt={`Pet image ${currentImageIndex + 1}`}
          className="carousel-image"
        />

        {images.length > 1 && (
          <>
            <button className="carousel-btn prev-btn" onClick={handlePrevImage}>
              &#10094;
            </button>
            <button className="carousel-btn next-btn" onClick={handleNextImage}>
              &#10095;
            </button>
          </>
        )}
      </div>

      {images.length > 1 && (
        <div className="carousel-thumbnails">
          {images.map((image, index) => (
            <button
              key={index}
              className={`thumbnail ${index === currentImageIndex ? 'active' : ''}`}
              onClick={() => setCurrentImageIndex(index)}
            >
              <img src={image} alt={`Thumbnail ${index + 1}`} />
            </button>
          ))}
        </div>
      )}

      {images.length > 1 && (
        <div className="carousel-indicator">
          {currentImageIndex + 1} / {images.length}
        </div>
      )}

      <style jsx>{`
        .pet-carousel {
          display: flex;
          flex-direction: column;
          gap: 15px;
        }

        .carousel-main {
          position: relative;
          aspect-ratio: 16 / 12;
          background: #f0f0f0;
          border-radius: 8px;
          overflow: hidden;
        }

        .carousel-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .carousel-btn {
          position: absolute;
          top: 50%;
          transform: translateY(-50%);
          background: rgba(0, 0, 0, 0.5);
          color: white;
          border: none;
          font-size: 2rem;
          padding: 10px 15px;
          cursor: pointer;
          border-radius: 4px;
          transition: background 0.3s;
          z-index: 2;
        }

        .carousel-btn:hover {
          background: rgba(0, 0, 0, 0.8);
        }

        .prev-btn {
          left: 10px;
        }

        .next-btn {
          right: 10px;
        }

        .carousel-thumbnails {
          display: flex;
          gap: 10px;
          overflow-x: auto;
          padding: 10px 0;
        }

        .thumbnail {
          flex-shrink: 0;
          width: 80px;
          height: 80px;
          border: 3px solid transparent;
          border-radius: 4px;
          overflow: hidden;
          cursor: pointer;
          transition: border-color 0.3s;
          padding: 0;
          background: none;
        }

        .thumbnail img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .thumbnail.active {
          border-color: #667eea;
        }

        .thumbnail:hover {
          border-color: #ddd;
        }

        .carousel-indicator {
          text-align: center;
          color: #666;
          font-size: 0.9rem;
        }

        .carousel-placeholder {
          aspect-ratio: 16 / 12;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          background: #f0f0f0;
          border-radius: 8px;
          color: #999;
          gap: 10px;
        }

        .carousel-placeholder span {
          font-size: 3rem;
        }

        @media (max-width: 768px) {
          .carousel-thumbnails {
            gap: 8px;
          }

          .thumbnail {
            width: 60px;
            height: 60px;
          }
        }
      `}</style>
    </div>
  );
}
