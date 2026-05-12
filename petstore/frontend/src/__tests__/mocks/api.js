/**
 * Mock API client for testing.
 * 
 * Jest mocks the real API service and provides test implementations.
 * Use jest.mock() to replace API calls in tests.
 */

jest.mock('../../services/api', () => ({
  default: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
    patch: jest.fn(),
  },
  __esModule: true,
}));

export const mockApi = jest.mock;
