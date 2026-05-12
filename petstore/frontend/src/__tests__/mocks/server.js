/**
 * Mock Service Worker (MSW) setup for API mocking.
 * 
 * MSW intercepts network requests at the browser level,
 * allowing tests to mock API responses consistently.
 */

import { setupServer } from 'msw/node';
import { authHandlers } from './handlers/authHandlers';
import { petHandlers } from './handlers/petHandlers';
import { cartHandlers } from './handlers/cartHandlers';

/**
 * Create MSW server with all handlers.
 */
export const server = setupServer(
  ...authHandlers,
  ...petHandlers,
  ...cartHandlers,
);

/**
 * Enable request interception before all tests.
 */
beforeAll(() => server.listen({ onUnhandledRequest: 'warn' }));

/**
 * Reset handlers between tests to ensure clean state.
 */
afterEach(() => server.resetHandlers());

/**
 * Clean up after all tests.
 */
afterAll(() => server.close());

export { server };
