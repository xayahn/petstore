import { useState, useEffect, useCallback } from 'react';
import { useAuth } from './useAuth';
import api from '../services/api';

/**
 * Custom hook for fetching data from API with loading and error handling.
 * 
 * @param {string} url - API endpoint URL
 * @param {Object} options - Fetch options (method, body, etc.)
 * @returns {Object} Loading state, data, error, and refetch function
 */
export function useFetch(url, options = {}) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { accessToken } = useAuth();

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const headers = {
        ...options.headers,
      };

      if (accessToken) {
        headers.Authorization = `Bearer ${accessToken}`;
      }

      const config = {
        ...options,
        headers,
      };

      const response = await api.get(url, config);
      setData(response.data?.data || response.data);
      setError(null);
    } catch (err) {
      setError(
        err.response?.data?.error || err.message || 'Failed to fetch data',
      );
      setData(null);
    } finally {
      setLoading(false);
    }
  }, [url, options, accessToken]);

  useEffect(() => {
    if (url) {
      fetchData();
    }
  }, [url, fetchData]);

  return { data, loading, error, refetch: fetchData };
}
