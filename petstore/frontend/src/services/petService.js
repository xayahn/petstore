/**
 * Pet API Service
 * 
 * Handles all API calls related to pet listings and catalog.
 */

import api from './api';

const BASE_URL = '/api/pets';

export const petService = {
  /**
   * List all pets with optional filters and pagination
   */
  listPets: async (filters = {}) => {
    const params = new URLSearchParams();

    if (filters.species) params.append('species', filters.species);
    if (filters.breed) params.append('breed', filters.breed);
    if (filters.minPrice) params.append('minPrice', filters.minPrice);
    if (filters.maxPrice) params.append('maxPrice', filters.maxPrice);

    params.append('page', filters.page || 0);
    params.append('size', filters.size || 20);
    params.append('sortBy', filters.sortBy || 'created_at');

    try {
      const response = await api.get(`${BASE_URL}?${params.toString()}`);
      return response.data;
    } catch (error) {
      console.error('Error listing pets:', error);
      throw error;
    }
  },

  /**
   * Get pet detail by ID
   */
  getPetDetail: async (petId) => {
    try {
      const response = await api.get(`${BASE_URL}/${petId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching pet detail:', error);
      throw error;
    }
  },

  /**
   * Search pets by query string
   */
  searchPets: async (query, page = 0, size = 20) => {
    try {
      const response = await api.get(`${BASE_URL}/search?q=${query}&page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      console.error('Error searching pets:', error);
      throw error;
    }
  },

  /**
   * Get featured/trending pets
   */
  getFeaturedPets: async (limit = 6) => {
    try {
      const response = await api.get(`${BASE_URL}/featured?limit=${limit}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching featured pets:', error);
      throw error;
    }
  },
};

export default petService;
