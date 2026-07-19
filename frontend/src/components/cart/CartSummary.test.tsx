import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { CartSummary } from './CartSummary';
import type { Cart } from '../../types/cart';

vi.mock('../../hooks/useCart', () => ({
  useClearCart: () => ({ mutate: vi.fn(), isPending: false }),
}));

const mockCart: Cart = {
  id: 1,
  items: [
    { id: 1, variantId: 1, productName: 'Test Shirt', productSlug: 'test-shirt', productImageUrl: null, size: 'M', color: 'Black', colorHex: '#000000', unitPrice: 29.99, quantity: 2, subtotal: 59.98 },
    { id: 2, variantId: 2, productName: 'Test Hat', productSlug: 'test-hat', productImageUrl: null, size: 'One Size', color: 'Red', colorHex: '#FF0000', unitPrice: 19.99, quantity: 1, subtotal: 19.99 },
  ],
  totalItems: 3,
  subtotal: 79.97,
};

function renderWithProviders(ui: React.ReactElement) {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>{ui}</BrowserRouter>
    </QueryClientProvider>
  );
}

describe('CartSummary', () => {
  it('displays item count', () => {
    renderWithProviders(<CartSummary cart={mockCart} />);
    expect(screen.getByText(/Items \(3\)/)).toBeInTheDocument();
  });

  it('displays subtotal in the summary', () => {
    renderWithProviders(<CartSummary cart={mockCart} />);
    const priceElements = screen.getAllByText('$79.97');
    expect(priceElements.length).toBeGreaterThanOrEqual(1);
  });

  it('displays free shipping', () => {
    renderWithProviders(<CartSummary cart={mockCart} />);
    expect(screen.getByText('Free')).toBeInTheDocument();
  });

  it('has proceed to checkout link', () => {
    renderWithProviders(<CartSummary cart={mockCart} />);
    const link = screen.getByRole('link', { name: /proceed to checkout/i });
    expect(link).toHaveAttribute('href', '/checkout');
  });

  it('shows total matching subtotal', () => {
    renderWithProviders(<CartSummary cart={mockCart} />);
    const totalElements = screen.getAllByText('$79.97');
    expect(totalElements.length).toBeGreaterThanOrEqual(2);
  });
});
