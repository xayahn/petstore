import React, { useState, useEffect } from 'react';
import petService from '@services/petService';
import PetFilterSidebar from './PetFilterSidebar';
import SearchBar from './SearchBar';
import PetListView from './PetListView';
import LoadingSpinner from '../LoadingSpinner';
import ErrorAlert from '../ErrorAlert';
import '@styles/index.css';

/**
 * BrowsePetsPage Component
 * 
 * Full pet catalog page with filters, search, and pagination.
 * Combines all catalog features into one page.
 */
export default function BrowsePetsPage() {
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filters, setFilters] = useState({
    species: null,
    breed: null,
    minPrice: null,
    maxPrice: null,
  });
  const [sortBy, setSortBy] = useState('created_at');
  const [searchQuery, setSearchQuery] = useState('');

  const pageSize = 20;

  useEffect(() => {
    fetchPets();
  }, [currentPage, filters, sortBy, searchQuery]);

  const fetchPets = async () => {
    try {
      setLoading(true);
      setError(null);

      let data;
      if (searchQuery) {
        data = await petService.searchPets(searchQuery, currentPage, pageSize);
      } else {
        data = await petService.listPets({
          ...filters,
          page: currentPage,
          size: pageSize,
          sortBy,
        });
      }

      setPets(data.content || []);
      setTotalPages(data.totalPages || 1);
    } catch (err) {
      setError('Failed to load pets. Please try again.');
      console.error('Error fetching pets:', err);
      setPets([]);
    } finally {
      setLoading(false);
    }
  };

  const handleFiltersChange = (newFilters) => {
    setFilters(newFilters);
    setCurrentPage(0); // Reset to first page
  };

  const handleSearch = (query) => {
    setSearchQuery(query);
    setCurrentPage(0); // Reset to first page
  };

  const handleSortChange = (sort) => {
    setSortBy(sort);
    setCurrentPage(0);
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="browse-pets-page">
      <div className="page-header">
        <h1>Browse Pets</h1>
        <p>Discover your perfect pet companion</p>
      </div>

      <SearchBar onSearch={handleSearch} loading={loading} />

      {error && <ErrorAlert message={error} severity="error" />}

      <div className="browse-container">
        <PetFilterSidebar onFiltersChange={handleFiltersChange} loading={loading} />

        <div className="browse-main">
          {loading && <LoadingSpinner message="Loading pets..." />}

          {!loading && (
            <PetListView
              pets={pets}
              totalPages={totalPages}
              currentPage={currentPage}
              loading={loading}
              onPageChange={handlePageChange}
              onSortChange={handleSortChange}
            />
          )}
        </div>
      </div>

      <style jsx>{`
        .browse-pets-page {
          min-height: 100vh;
          padding: 40px 20px;
          background: white;
        }

        .page-header {
          text-align: center;
          margin-bottom: 40px;
        }

        .page-header h1 {
          font-size: 2.5rem;
          margin-bottom: 10px;
          color: #333;
        }

        .page-header p {
          font-size: 1.1rem;
          color: #666;
        }

        .browse-container {
          display: flex;
          gap: 30px;
          max-width: 1400px;
          margin: 0 auto;
        }

        .browse-main {
          flex: 1;
          min-width: 0;
        }

        @media (max-width: 768px) {
          .browse-pets-page {
            padding: 20px 10px;
          }

          .page-header h1 {
            font-size: 1.8rem;
          }

          .browse-container {
            flex-direction: column;
            gap: 20px;
          }
        }
      `}</style>
    </div>
  );
}
