/**
 * MSW handlers for pet-related endpoints.
 */

import { http, HttpResponse } from 'msw';

const API_BASE = process.env.VITE_API_BASE_URL || 'http://localhost:8080';

const mockPets = [
  {
    id: '850e8400-e29b-41d4-a716-446655440001',
    name: 'Buddy',
    species: 'Dog',
    breed: 'Golden Retriever',
    price: 1200.00,
    description: 'Friendly and energetic puppy',
    images: [],
    seller: {
      id: '650e8400-e29b-41d4-a716-446655440001',
      businessName: 'Happy Pets Farm',
      verified: true,
      rating: 4.8,
    },
  },
  {
    id: '850e8400-e29b-41d4-a716-446655440002',
    name: 'Luna',
    species: 'Cat',
    breed: 'Persian',
    price: 800.00,
    description: 'Adorable kitten with beautiful white fur',
    images: [],
    seller: {
      id: '650e8400-e29b-41d4-a716-446655440001',
      businessName: 'Happy Pets Farm',
      verified: true,
      rating: 4.8,
    },
  },
];

export const petHandlers = [
  /**
   * GET /api/pets
   */
  http.get(`${API_BASE}/api/pets`, ({ request }) => {
    const url = new URL(request.url);
    const page = url.searchParams.get('page') || 0;
    const size = url.searchParams.get('size') || 20;
    const species = url.searchParams.get('species');

    let filtered = mockPets;
    if (species) {
      filtered = mockPets.filter((pet) => pet.species.toLowerCase() === species.toLowerCase());
    }

    return HttpResponse.json(
      {
        content: filtered.slice(page * size, (page + 1) * size),
        totalElements: filtered.length,
        totalPages: Math.ceil(filtered.length / size),
        currentPage: page,
        pageSize: size,
      },
      { status: 200 },
    );
  }),

  /**
   * GET /api/pets/:id
   */
  http.get(`${API_BASE}/api/pets/:id`, ({ params }) => {
    const pet = mockPets.find((p) => p.id === params.id);
    if (!pet) {
      return HttpResponse.json({ error: 'Pet not found' }, { status: 404 });
    }
    return HttpResponse.json(pet, { status: 200 });
  }),

  /**
   * GET /api/pets/featured
   */
  http.get(`${API_BASE}/api/pets/featured`, () => {
    return HttpResponse.json(
      {
        content: mockPets.slice(0, 5),
      },
      { status: 200 },
    );
  }),

  /**
   * POST /api/pets (create listing - seller only)
   */
  http.post(`${API_BASE}/api/pets`, async ({ request }) => {
    const body = await request.json();
    return HttpResponse.json(
      {
        id: '850e8400-e29b-41d4-a716-446655440099',
        ...body,
      },
      { status: 201 },
    );
  }),

  /**
   * PUT /api/pets/:id
   */
  http.put(`${API_BASE}/api/pets/:id`, async ({ request, params }) => {
    const body = await request.json();
    return HttpResponse.json(
      {
        id: params.id,
        ...body,
      },
      { status: 200 },
    );
  }),
];
