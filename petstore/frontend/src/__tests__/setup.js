/**
 * Jest setup file for React Testing Library and test utilities.
 * 
 * Runs before all tests to:
 * - Configure React Testing Library
 * - Set up global test utilities
 * - Configure window/document mocks
 */

import '@testing-library/jest-dom';

/**
 * Suppress console warnings/errors in tests (optional).
 * Comment out to see all console output during tests.
 */
global.console = {
  ...console,
  error: jest.fn(),
  warn: jest.fn(),
};

/**
 * Mock window.matchMedia for responsive component tests.
 */
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation((query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(),
    removeListener: jest.fn(),
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
});

/**
 * Mock localStorage.
 */
const localStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};
global.localStorage = localStorageMock;

/**
 * Mock sessionStorage.
 */
const sessionStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};
global.sessionStorage = sessionStorageMock;
