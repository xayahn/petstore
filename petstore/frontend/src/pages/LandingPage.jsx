import React, { useState, useEffect } from 'react';
import HeroBanner from '../components/Pet/HeroBanner';
import FeaturedPetsSection from '../components/Pet/FeaturedPetsSection';
import '@styles/index.css';

/**
 * LandingPage Component
 * 
 * Main landing page with hero banner and featured pets section.
 * Public page - no authentication required.
 */
export default function LandingPage() {
  return (
    <div className="landing-page">
      <HeroBanner />
      <FeaturedPetsSection />
      
      <section className="features-section">
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">🛡️</div>
            <h3>Verified Sellers</h3>
            <p>All sellers are verified to ensure safe and reliable transactions.</p>
          </div>

          <div className="feature-card">
            <div className="feature-icon">💳</div>
            <h3>Secure Payments</h3>
            <p>Your payments are secured with industry-standard encryption.</p>
          </div>

          <div className="feature-card">
            <div className="feature-icon">🚚</div>
            <h3>Fast Delivery</h3>
            <p>Get your new pet companion delivered quickly and safely.</p>
          </div>

          <div className="feature-card">
            <div className="feature-icon">⭐</div>
            <h3>Quality Pets</h3>
            <p>All pets are health-checked and come with proper documentation.</p>
          </div>
        </div>
      </section>

      <section className="cta-section">
        <div className="cta-content">
          <h2>Ready to Find Your Perfect Pet?</h2>
          <p>Browse our extensive catalog of healthy, happy pets.</p>
          <a href="/browse" className="cta-button">
            Start Browsing Now
          </a>
        </div>
      </section>

      <style jsx>{`
        .landing-page {
          min-height: 100vh;
        }

        .features-section {
          padding: 80px 20px;
          background: white;
        }

        .features-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
          gap: 30px;
          max-width: 1200px;
          margin: 0 auto;
        }

        .feature-card {
          text-align: center;
          padding: 30px;
          border-radius: 10px;
          background: #f8f9fa;
          transition: transform 0.3s, box-shadow 0.3s;
        }

        .feature-card:hover {
          transform: translateY(-5px);
          box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }

        .feature-icon {
          font-size: 3rem;
          margin-bottom: 15px;
        }

        .feature-card h3 {
          margin-bottom: 10px;
          color: #333;
        }

        .feature-card p {
          color: #666;
          line-height: 1.6;
        }

        .cta-section {
          padding: 80px 20px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          text-align: center;
          color: white;
        }

        .cta-content h2 {
          font-size: 2.5rem;
          margin-bottom: 15px;
        }

        .cta-content p {
          font-size: 1.2rem;
          margin-bottom: 30px;
          opacity: 0.95;
        }

        .cta-button {
          display: inline-block;
          padding: 15px 50px;
          background: white;
          color: #667eea;
          text-decoration: none;
          border-radius: 8px;
          font-weight: 600;
          font-size: 1.1rem;
          transition: all 0.3s;
        }

        .cta-button:hover {
          transform: translateY(-3px);
          box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
        }

        @media (max-width: 768px) {
          .features-section {
            padding: 40px 15px;
          }

          .features-grid {
            gap: 20px;
          }

          .cta-section {
            padding: 40px 15px;
          }

          .cta-content h2 {
            font-size: 1.8rem;
          }
        }
      `}</style>
    </div>
  );
}
