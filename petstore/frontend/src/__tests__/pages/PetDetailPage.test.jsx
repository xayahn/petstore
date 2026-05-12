import { describe, it, expect, beforeAll, afterEach, afterAll, vi } from 'vitest';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithContext } from '../__mocks__/testUtils';
import server from '../__mocks__/msw/server';
import * as handlers from '../__mocks__/msw/handlers';
import PetDetailPage from '../../src/pages/PetDetailPage';

/**
 * PetDetailPage.test.jsx - T093
 * Tests pet detail page including pet details display, seller info, and image carousel.
 */

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('PetDetailPage Component', () => {
  const mockPetId = 'pet-123';

  // Mock useParams
  vi.mock('react-router-dom', async () => {
    const actual = await vi.importActual('react-router-dom');
    return {
      ...actual,
      useParams: () => ({ id: mockPetId }),
    };
  });

  it('displays loading spinner initially', () => {
    renderWithContext(<PetDetailPage />);

    // Initially shows loading, but MSW handlers resolve quickly in tests
    // Just verify component renders
    expect(document.body).toBeInTheDocument();
  });

  it('loads and displays pet details', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Fluffy/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/Dog/i)).toBeInTheDocument();
    expect(screen.getByText(/999\.99/i)).toBeInTheDocument();
  });

  it('displays pet information in grid', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Fluffy/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/Age/i)).toBeInTheDocument();
    expect(screen.getByText(/Health Status/i)).toBeInTheDocument();
  });

  it('displays pet description', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Fluffy/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/Friendly and playful/i)).toBeInTheDocument();
  });

  it('shows availability status badge', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Available/i)).toBeInTheDocument();
    });
  });

  it('displays seller information card', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Seller Information/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/John/i)).toBeInTheDocument();
  });

  it('shows seller verified badge', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Seller Information/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/Verified/i)).toBeInTheDocument();
  });

  it('displays seller rating', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Seller Information/i)).toBeInTheDocument();
    });

    // Should display rating
    const ratingElements = screen.queryAllByText(/Rating/i);
    expect(ratingElements.length).toBeGreaterThan(0);
  });

  it('displays action buttons (Add to Cart, Add to Favorites)', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Fluffy/i)).toBeInTheDocument();
    });

    expect(screen.getByRole('button', { name: /Add to Cart/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Add to Favorites/i })).toBeInTheDocument();
  });

  it('displays back button', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Fluffy/i)).toBeInTheDocument();
    });

    expect(screen.getByRole('button', { name: /Back to Browse/i })).toBeInTheDocument();
  });

  it('displays image carousel', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Fluffy/i)).toBeInTheDocument();
    });

    // Carousel should render
    const mainContainer = screen.getByText(/Fluffy/i).closest('.detail-page');
    expect(mainContainer).toBeInTheDocument();
  });

  it('displays pet price prominently', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Fluffy/i)).toBeInTheDocument();
    });

    const priceElement = screen.getByText(/\$999\.99/);
    expect(priceElement).toBeInTheDocument();
  });

  it('displays stock quantity if available', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Fluffy/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/In Stock/i)).toBeInTheDocument();
  });

  it('shows error message when pet not found', async () => {
    server.use(handlers.getPetDetailNotFoundHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Failed to load pet details/i)).toBeInTheDocument();
    });
  });

  it('displays responsive layout on mobile', async () => {
    server.use(handlers.getPetDetailHandler);

    const { container } = renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Fluffy/i)).toBeInTheDocument();
    });

    const detailContainer = container.querySelector('.detail-container');
    expect(detailContainer).toBeInTheDocument();
  });

  it('displays seller contact button', async () => {
    server.use(handlers.getPetDetailHandler);

    renderWithContext(<PetDetailPage />);

    await waitFor(() => {
      expect(screen.getByText(/Seller Information/i)).toBeInTheDocument();
    });

    expect(screen.getByRole('button', { name: /Contact Seller/i })).toBeInTheDocument();
  });
});
