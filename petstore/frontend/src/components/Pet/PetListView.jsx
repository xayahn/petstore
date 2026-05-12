import React from 'react';
import PetCard from './PetCard';
import '@styles/index.css';

/**
 * PetListView Component
 * 
 * Displays paginated list of pets with sorting options.
 */
export default function PetListView({ pets, totalPages, currentPage, loading, onPageChange, onSortChange }) {
  const handlePreviousPage = () => {
    if (currentPage > 0) {
      onPageChange(currentPage - 1);
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      onPageChange(currentPage + 1);
    }
  };

  if (loading) {
    return <div className="loading-state">Loading pets...</div>;
  }

  if (!pets || pets.length === 0) {
    return <div className="empty-state">No pets found. Try adjusting your filters.</div>;
  }

  return (
    <div className="pet-list-view">
      <div className="list-controls">
        <div className="sort-control">
          <label htmlFor="sort-by">Sort by:</label>
          <select id="sort-by" onChange={(e) => onSortChange(e.target.value)}>
            <option value="created_at">Newest First</option>
            <option value="price">Price: Low to High</option>
            <option value="name">Pet Name A-Z</option>
          </select>
        </div>
        <div className="results-info">
          Showing {pets.length} results (Page {currentPage + 1} of {totalPages})
        </div>
      </div>

      <div className="pets-grid">
        {pets.map((pet) => (
          <PetCard key={pet.id} pet={pet} />
        ))}
      </div>

      <div className="pagination">
        <button
          onClick={handlePreviousPage}
          disabled={currentPage === 0}
          className="pagination-btn"
        >
          ← Previous
        </button>

        <div className="page-info">
          Page {currentPage + 1} of {totalPages}
        </div>

        <button
          onClick={handleNextPage}
          disabled={currentPage >= totalPages - 1}
          className="pagination-btn"
        >
          Next →
        </button>
      </div>

      <style jsx>{`
        .list-controls {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;
          padding: 15px;
          background: #f8f9fa;
          border-radius: 8px;
        }

        .sort-control {
          display: flex;
          align-items: center;
          gap: 10px;
        }

        .sort-control label {
          font-weight: 600;
          color: #333;
        }

        .sort-control select {
          padding: 8px 12px;
          border: 1px solid #ddd;
          border-radius: 4px;
          font-family: inherit;
        }

        .results-info {
          color: #666;
          font-size: 0.95rem;
        }

        .pets-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
          gap: 30px;
          margin-bottom: 40px;
        }

        .loading-state,
        .empty-state {
          text-align: center;
          padding: 60px 20px;
          color: #999;
          font-size: 1.1rem;
        }

        .pagination {
          display: flex;
          justify-content: center;
          align-items: center;
          gap: 20px;
          margin-top: 30px;
          padding: 20px;
          background: #f8f9fa;
          border-radius: 8px;
        }

        .pagination-btn {
          padding: 10px 20px;
          background: white;
          border: 2px solid #667eea;
          color: #667eea;
          border-radius: 4px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.3s;
        }

        .pagination-btn:hover:not(:disabled) {
          background: #667eea;
          color: white;
        }

        .pagination-btn:disabled {
          border-color: #ccc;
          color: #ccc;
          cursor: not-allowed;
        }

        .page-info {
          font-weight: 600;
          color: #333;
        }

        @media (max-width: 768px) {
          .list-controls {
            flex-direction: column;
            gap: 15px;
            align-items: flex-start;
          }

          .pets-grid {
            grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
            gap: 20px;
          }

          .pagination {
            flex-direction: column;
            gap: 10px;
          }
        }
      `}</style>
    </div>
  );
}
