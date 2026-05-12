import React, { useState } from 'react';
import '@styles/index.css';

/**
 * SearchBar Component
 * 
 * Search input for pet catalog with icon and debouncing.
 */
export default function SearchBar({ onSearch, loading = false }) {
  const [query, setQuery] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    if (query.trim()) {
      onSearch(query);
    }
  };

  const handleClear = () => {
    setQuery('');
    onSearch('');
  };

  return (
    <form className="search-bar" onSubmit={handleSearch}>
      <div className="search-input-wrapper">
        <span className="search-icon">🔍</span>
        <input
          type="text"
          placeholder="Search by name, species, or breed..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="search-input"
          disabled={loading}
        />
        {query && (
          <button
            type="button"
            onClick={handleClear}
            className="clear-search-btn"
            disabled={loading}
          >
            ✕
          </button>
        )}
      </div>
      <button type="submit" className="search-btn" disabled={loading || !query.trim()}>
        {loading ? 'Searching...' : 'Search'}
      </button>

      <style jsx>{`
        .search-bar {
          display: flex;
          gap: 10px;
          margin-bottom: 30px;
        }

        .search-input-wrapper {
          position: relative;
          flex: 1;
        }

        .search-icon {
          position: absolute;
          left: 15px;
          top: 50%;
          transform: translateY(-50%);
          font-size: 1.2rem;
          pointer-events: none;
        }

        .search-input {
          width: 100%;
          padding: 12px 40px 12px 45px;
          border: 2px solid #e0e0e0;
          border-radius: 8px;
          font-size: 1rem;
          font-family: inherit;
          transition: border-color 0.3s;
        }

        .search-input:focus {
          outline: none;
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .search-input:disabled {
          background: #f0f0f0;
          color: #999;
        }

        .clear-search-btn {
          position: absolute;
          right: 12px;
          top: 50%;
          transform: translateY(-50%);
          background: none;
          border: none;
          font-size: 1.2rem;
          cursor: pointer;
          color: #999;
          padding: 5px;
          transition: color 0.2s;
        }

        .clear-search-btn:hover {
          color: #333;
        }

        .search-btn {
          padding: 12px 30px;
          background: #667eea;
          color: white;
          border: none;
          border-radius: 8px;
          font-weight: 600;
          cursor: pointer;
          transition: background 0.3s;
          white-space: nowrap;
        }

        .search-btn:hover:not(:disabled) {
          background: #764ba2;
        }

        .search-btn:disabled {
          background: #ccc;
          cursor: not-allowed;
        }

        @media (max-width: 768px) {
          .search-bar {
            flex-direction: column;
          }

          .search-btn {
            width: 100%;
          }
        }
      `}</style>
    </form>
  );
}
