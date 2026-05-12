import React, { useEffect, useState } from 'react';
import petService from '@services/petService';
import PetCard from './PetCard';
import LoadingSpinner from '../LoadingSpinner';
import ErrorAlert from '../ErrorAlert';
import '@styles/index.css';

/**
 * FeaturedPetsSection Component
 * 
 * Displays a curated list of featured/trending pets on the landing page.
 */
export default function FeaturedPetsSection() {
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchFeaturedPets();
  }, []);

  const fetchFeaturedPets = async () => {
    try {
      setLoading(true);
      const data = await petService.getFeaturedPets(8);
      setPets(data);
      setError(null);
    } catch (err) {
      setError('Failed to load featured pets. Please try again.');
      console.error('Error loading featured pets:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="featured-section">
      <div className="section-header">
        <h2>Featured Pets</h2>
        <p>Popular pets from our verified sellers</p>
        <a href="/browse" className="view-all-link">
          View All Pets →
        </a>
      </div>

      {error && <ErrorAlert message={error} severity="error" />}

      {loading ? (
        <LoadingSpinner message="Loading featured pets..." />
      ) : (
        <div className="pets-grid">
          {pets && pets.length > 0 ? (
            pets.map((pet) => (
              <PetCard key={pet.id} pet={pet} />
            ))
          ) : (
            <p className="no-pets">No featured pets available at this time.</p>
          )}
        </div>
      )}

      <style jsx>{`
        .featured-section {
          padding: 80px 20px;
          background: #f8f9fa;
        }

        .section-header {
          text-align: center;
          margin-bottom: 60px;
        }

        .section-header h2 {
          font-size: 2.5rem;
          margin-bottom: 10px;
          color: #333;
        }

        .section-header p {
          font-size: 1.1rem;
          color: #666;
          margin-bottom: 15px;
        }

        .view-all-link {
          color: #667eea;
          text-decoration: none;
          font-weight: 600;
          transition: color 0.3s;
        }

        .view-all-link:hover {
          color: #764ba2;
        }

        .pets-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
          gap: 30px;
          max-width: 1200px;
          margin: 0 auto;
        }

        .no-pets {
          grid-column: 1 / -1;
          text-align: center;
          color: #999;
          padding: 40px;
        }

        @media (max-width: 768px) {
          .featured-section {
            padding: 40px 15px;
          }

          .section-header h2 {
            font-size: 1.8rem;
          }

          .pets-grid {
            grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
            gap: 20px;
          }
        }
      `}</style>
    </section>
  );
}
