import React, { useState } from 'react';
import '@styles/index.css';

/**
 * PetFilterSidebar Component
 * 
 * Sidebar with filters for pet catalog (species, price range, breed).
 * Provides UI for filtering catalog results.
 */
export default function PetFilterSidebar({ onFiltersChange, loading = false }) {
  const [species, setSpecies] = useState('');
  const [breed, setBreed] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');

  const commonSpecies = ['Dog', 'Cat', 'Bird', 'Rabbit', 'Hamster', 'Guinea Pig'];
  const commonBreeds = {
    Dog: ['Labrador', 'Golden Retriever', 'German Shepherd', 'Bulldog', 'Poodle'],
    Cat: ['Persian', 'Siamese', 'Maine Coon', 'Bengal', 'British Shorthair'],
    Bird: ['Parrot', 'Cockatiel', 'Parakeet', 'Canary'],
    Rabbit: ['Lop', 'Holland', 'Angora', 'Flemish Giant'],
  };

  const handleFilterChange = () => {
    onFiltersChange({
      species: species || null,
      breed: breed || null,
      minPrice: minPrice ? parseFloat(minPrice) : null,
      maxPrice: maxPrice ? parseFloat(maxPrice) : null,
    });
  };

  const handleSpeciesChange = (e) => {
    setSpecies(e.target.value);
    setBreed(''); // Reset breed when species changes
  };

  const handleClearFilters = () => {
    setSpecies('');
    setBreed('');
    setMinPrice('');
    setMaxPrice('');
    onFiltersChange({
      species: null,
      breed: null,
      minPrice: null,
      maxPrice: null,
    });
  };

  const breedOptions = species && commonBreeds[species] ? commonBreeds[species] : [];

  return (
    <aside className="filter-sidebar">
      <div className="filter-header">
        <h3>Filters</h3>
        <button onClick={handleClearFilters} className="clear-filters-btn">
          Clear All
        </button>
      </div>

      <div className="filter-group">
        <label htmlFor="species-filter">Species</label>
        <select
          id="species-filter"
          value={species}
          onChange={handleSpeciesChange}
          disabled={loading}
        >
          <option value="">All Species</option>
          {commonSpecies.map((s) => (
            <option key={s} value={s}>
              {s}
            </option>
          ))}
        </select>
      </div>

      {breedOptions.length > 0 && (
        <div className="filter-group">
          <label htmlFor="breed-filter">Breed</label>
          <select
            id="breed-filter"
            value={breed}
            onChange={(e) => setBreed(e.target.value)}
            disabled={loading}
          >
            <option value="">All Breeds</option>
            {breedOptions.map((b) => (
              <option key={b} value={b}>
                {b}
              </option>
            ))}
          </select>
        </div>
      )}

      <div className="filter-group">
        <label htmlFor="min-price">Min Price ($)</label>
        <input
          id="min-price"
          type="number"
          value={minPrice}
          onChange={(e) => setMinPrice(e.target.value)}
          placeholder="0"
          min="0"
          disabled={loading}
        />
      </div>

      <div className="filter-group">
        <label htmlFor="max-price">Max Price ($)</label>
        <input
          id="max-price"
          type="number"
          value={maxPrice}
          onChange={(e) => setMaxPrice(e.target.value)}
          placeholder="10000"
          min="0"
          disabled={loading}
        />
      </div>

      <button
        onClick={handleFilterChange}
        className="apply-filters-btn"
        disabled={loading}
      >
        {loading ? 'Applying...' : 'Apply Filters'}
      </button>

      <style jsx>{`
        .filter-sidebar {
          width: 250px;
          background: #f8f9fa;
          padding: 20px;
          border-radius: 8px;
          height: fit-content;
          position: sticky;
          top: 100px;
        }

        .filter-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;
          padding-bottom: 10px;
          border-bottom: 2px solid #e0e0e0;
        }

        .filter-header h3 {
          margin: 0;
          font-size: 1.2rem;
          color: #333;
        }

        .clear-filters-btn {
          background: none;
          border: none;
          color: #667eea;
          font-size: 0.9rem;
          cursor: pointer;
          text-decoration: underline;
        }

        .clear-filters-btn:hover {
          color: #764ba2;
        }

        .filter-group {
          margin-bottom: 20px;
          display: flex;
          flex-direction: column;
        }

        .filter-group label {
          font-weight: 600;
          margin-bottom: 8px;
          font-size: 0.95rem;
          color: #333;
        }

        .filter-group select,
        .filter-group input {
          padding: 10px;
          border: 1px solid #ddd;
          border-radius: 4px;
          font-size: 0.95rem;
          font-family: inherit;
        }

        .filter-group select:focus,
        .filter-group input:focus {
          outline: none;
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .filter-group input:disabled,
        .filter-group select:disabled {
          background: #e8e8e8;
          color: #999;
        }

        .apply-filters-btn {
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

        .apply-filters-btn:hover:not(:disabled) {
          background: #764ba2;
        }

        .apply-filters-btn:disabled {
          background: #ccc;
          cursor: not-allowed;
        }

        @media (max-width: 768px) {
          .filter-sidebar {
            width: 100%;
            position: static;
            margin-bottom: 30px;
          }
        }
      `}</style>
    </aside>
  );
}
