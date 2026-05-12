import React from 'react';
import '@styles/index.css';

/**
 * HeroBanner Component
 * 
 * Large hero section at top of landing page with background image,
 * headline, subheading, and CTA buttons.
 */
export default function HeroBanner() {
  return (
    <section className="hero-banner">
      <div className="hero-overlay" />
      
      <div className="hero-content">
        <h1 className="hero-title">
          Find Your Perfect Pet Companion
        </h1>
        
        <p className="hero-subtitle">
          Browse thousands of pets from verified sellers in our marketplace.
          Start your journey today!
        </p>

        <div className="hero-cta">
          <a href="/browse" className="btn btn-primary">
            Browse Pets
          </a>
          
          <a href="/become-seller" className="btn btn-secondary">
            Start Selling
          </a>
        </div>
      </div>

      <style jsx>{`
        .hero-banner {
          position: relative;
          height: 600px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          background-size: cover;
          background-position: center;
          display: flex;
          align-items: center;
          justify-content: center;
          text-align: center;
          overflow: hidden;
        }

        .hero-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.3);
          z-index: 1;
        }

        .hero-content {
          position: relative;
          z-index: 2;
          color: white;
          max-width: 800px;
          padding: 0 20px;
        }

        .hero-title {
          font-size: 3.5rem;
          font-weight: 700;
          margin-bottom: 20px;
          line-height: 1.2;
        }

        .hero-subtitle {
          font-size: 1.5rem;
          margin-bottom: 40px;
          opacity: 0.95;
        }

        .hero-cta {
          display: flex;
          gap: 20px;
          justify-content: center;
          flex-wrap: wrap;
        }

        .btn {
          padding: 15px 40px;
          font-size: 1.1rem;
          border: none;
          border-radius: 8px;
          cursor: pointer;
          text-decoration: none;
          font-weight: 600;
          transition: all 0.3s ease;
        }

        .btn-primary {
          background: white;
          color: #667eea;
        }

        .btn-primary:hover {
          transform: translateY(-2px);
          box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
        }

        .btn-secondary {
          background: rgba(255, 255, 255, 0.2);
          color: white;
          border: 2px solid white;
        }

        .btn-secondary:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: translateY(-2px);
        }

        @media (max-width: 768px) {
          .hero-banner {
            height: 400px;
          }

          .hero-title {
            font-size: 2rem;
          }

          .hero-subtitle {
            font-size: 1.2rem;
          }

          .btn {
            padding: 12px 30px;
            font-size: 1rem;
          }
        }
      `}</style>
    </section>
  );
}
