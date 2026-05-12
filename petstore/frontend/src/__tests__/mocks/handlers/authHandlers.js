/**
 * MSW handlers for authentication endpoints.
 */

import { http, HttpResponse } from 'msw';

const API_BASE = process.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const authHandlers = [
  /**
   * POST /api/auth/register
   */
  http.post(`${API_BASE}/api/auth/register`, async ({ request }) => {
    const body = await request.json();
    return HttpResponse.json(
      {
        id: '550e8400-e29b-41d4-a716-446655440001',
        email: body.email,
        firstName: body.firstName,
        message: 'Registration successful',
      },
      { status: 201 },
    );
  }),

  /**
   * POST /api/auth/login
   */
  http.post(`${API_BASE}/api/auth/login`, async ({ request }) => {
    const body = await request.json();
    return HttpResponse.json(
      {
        accessToken: 'mock-jwt-token',
        refreshToken: 'mock-refresh-token',
        user: {
          id: '550e8400-e29b-41d4-a716-446655440001',
          email: body.email,
          firstName: 'Test',
        },
      },
      { status: 200 },
    );
  }),

  /**
   * POST /api/auth/logout
   */
  http.post(`${API_BASE}/api/auth/logout`, () => {
    return HttpResponse.json({ message: 'Logged out successfully' }, { status: 200 });
  }),

  /**
   * GET /api/auth/me
   */
  http.get(`${API_BASE}/api/auth/me`, () => {
    return HttpResponse.json(
      {
        id: '550e8400-e29b-41d4-a716-446655440001',
        email: 'test@petstore.local',
        firstName: 'Test',
        lastName: 'User',
        isEmailVerified: true,
      },
      { status: 200 },
    );
  }),
];
