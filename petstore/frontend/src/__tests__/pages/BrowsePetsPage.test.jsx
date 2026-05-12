import { describe, it, expect, beforeAll, afterEach, afterAll, vi } from 'vitest';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithContext } from '../__mocks__/testUtils';
import server from '../__mocks__/msw/server';
import * as handlers from '../__mocks__/msw/handlers';
import BrowsePetsPage from '../../src/pages/BrowsePetsPage';

/**
 * BrowsePetsPage.test.jsx - T092
 * Tests browse page including catalog load, filter functionality, and pagination.
 */

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('BrowsePetsPage Component', () => {
  it('renders page title and description', () => {
    renderWithContext(<BrowsePetsPage />);

    expect(screen.getByText(/Browse Pets/i)).toBeInTheDocument();
    expect(screen.getByText(/Discover your perfect pet companion/i)).toBeInTheDocument();
  });

  it('renders search bar with input and search button', () => {
    renderWithContext(<BrowsePetsPage />);

    expect(screen.getByPlaceholderText(/Search by name/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Search/i })).toBeInTheDocument();
  });

  it('renders filter sidebar with species, breed, and price filters', () => {
    renderWithContext(<BrowsePetsPage />);

    expect(screen.getByText(/Filters/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Species/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Min Price/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Max Price/i)).toBeInTheDocument();
  });

  it('loads and displays pet catalog on mount', async () => {
    server.use(handlers.listPetsHandler);

    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/Showing [0-9]+ results/i)).toBeInTheDocument();
    });
  });

  it('applies species filter and reloads catalog', async () => {
    server.use(handlers.listPetsHandler);

    const user = userEvent.setup();
    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/Showing [0-9]+ results/i)).toBeInTheDocument();
    });

    const speciesSelect = screen.getByLabelText(/Species/i);
    await user.selectOptions(speciesSelect, 'Dog');

    const applyBtn = screen.getByRole('button', { name: /Apply Filters/i });
    await user.click(applyBtn);

    await waitFor(() => {
      expect(screen.getByText(/Page 1 of/i)).toBeInTheDocument();
    });
  });

  it('applies price range filter', async () => {
    server.use(handlers.listPetsHandler);

    const user = userEvent.setup();
    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/Showing [0-9]+ results/i)).toBeInTheDocument();
    });

    const minPriceInput = screen.getByLabelText(/Min Price/i);
    const maxPriceInput = screen.getByLabelText(/Max Price/i);

    await user.clear(minPriceInput);
    await user.type(minPriceInput, '100');

    await user.clear(maxPriceInput);
    await user.type(maxPriceInput, '500');

    const applyBtn = screen.getByRole('button', { name: /Apply Filters/i });
    await user.click(applyBtn);

    await waitFor(() => {
      expect(screen.getByText(/Page 1 of/i)).toBeInTheDocument();
    });
  });

  it('performs search when search button is clicked', async () => {
    server.use(handlers.searchPetsHandler);

    const user = userEvent.setup();
    renderWithContext(<BrowsePetsPage />);

    const searchInput = screen.getByPlaceholderText(/Search by name/i);
    await user.type(searchInput, 'fluffy');

    const searchBtn = screen.getByRole('button', { name: /Search/i });
    await user.click(searchBtn);

    await waitFor(() => {
      expect(screen.getByText(/Page 1 of/i)).toBeInTheDocument();
    });
  });

  it('displays pagination controls', async () => {
    server.use(handlers.listPetsHandler);

    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/Page 1 of/i)).toBeInTheDocument();
    });

    expect(screen.getByRole('button', { name: /← Previous/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Next →/i })).toBeInTheDocument();
  });

  it('navigates to next page when next button is clicked', async () => {
    server.use(handlers.listPetsHandler);

    const user = userEvent.setup();
    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/Page 1 of/i)).toBeInTheDocument();
    });

    const nextBtn = screen.getByRole('button', { name: /Next →/i });
    await user.click(nextBtn);

    await waitFor(() => {
      expect(screen.getByText(/Page 2 of/i)).toBeInTheDocument();
    });
  });

  it('disables previous button on first page', async () => {
    server.use(handlers.listPetsHandler);

    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/Page 1 of/i)).toBeInTheDocument();
    });

    const prevBtn = screen.getByRole('button', { name: /← Previous/i });
    expect(prevBtn).toBeDisabled();
  });

  it('displays sort options', async () => {
    server.use(handlers.listPetsHandler);

    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/Showing [0-9]+ results/i)).toBeInTheDocument();
    });

    expect(screen.getByLabelText(/Sort by/i)).toBeInTheDocument();
  });

  it('changes sort and reloads results', async () => {
    server.use(handlers.listPetsHandler);

    const user = userEvent.setup();
    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/Showing [0-9]+ results/i)).toBeInTheDocument();
    });

    const sortSelect = screen.getByLabelText(/Sort by/i);
    await user.selectOptions(sortSelect, 'price');

    await waitFor(() => {
      expect(screen.getByText(/Page 1 of/i)).toBeInTheDocument();
    });
  });

  it('displays empty state when no pets found', async () => {
    server.use(handlers.emptyListPetsHandler);

    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/No pets found/i)).toBeInTheDocument();
    });
  });

  it('clear filters button resets all filter values', async () => {
    server.use(handlers.listPetsHandler);

    const user = userEvent.setup();
    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/Showing [0-9]+ results/i)).toBeInTheDocument();
    });

    const speciesSelect = screen.getByLabelText(/Species/i);
    await user.selectOptions(speciesSelect, 'Dog');

    const clearBtn = screen.getByRole('button', { name: /Clear All/i });
    await user.click(clearBtn);

    expect(speciesSelect).toHaveValue('');
  });

  it('displays error alert on API failure', async () => {
    server.use(handlers.listPetsErrorHandler);

    renderWithContext(<BrowsePetsPage />);

    await waitFor(() => {
      expect(screen.getByText(/Failed to load pets/i)).toBeInTheDocument();
    });
  });
});
