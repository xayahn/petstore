/**
 * MSW handlers for cart-related endpoints.
 */

import { http, HttpResponse } from 'msw';

const API_BASE = process.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const cartHandlers = [
  /**
   * GET /api/carts/me
   */
  http.get(`${API_BASE}/api/carts/me`, () => {
    return HttpResponse.json(
      {
        id: 'a50e8400-e29b-41d4-a716-446655440001',
        userId: '550e8400-e29b-41d4-a716-446655440003',
        items: [],
        totalItems: 0,
        totalPrice: 0.00,
      },
      { status: 200 },
    );
  }),

  /**
   * POST /api/carts/items
   */
  http.post(`${API_BASE}/api/carts/items`, async ({ request }) => {
    const body = await request.json();
    return HttpResponse.json(
      {
        id: 'cart-item-id',
        petId: body.petId,
        quantity: body.quantity,
        price: 1200.00,
      },
      { status: 201 },
    );
  }),

  /**
   * PUT /api/carts/items/:itemId
   */
  http.put(`${API_BASE}/api/carts/items/:itemId`, async ({ request, params }) => {
    const body = await request.json();
    return HttpResponse.json(
      {
        id: params.itemId,
        quantity: body.quantity,
        price: 1200.00,
      },
      { status: 200 },
    );
  }),

  /**
   * DELETE /api/carts/items/:itemId
   */
  http.delete(`${API_BASE}/api/carts/items/:itemId`, ({ params }) => {
    return HttpResponse.json({ message: 'Item removed' }, { status: 200 });
  }),
];
