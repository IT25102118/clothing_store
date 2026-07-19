import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { ProductCard } from './ProductCard';
import type { Product } from '../../types/product';

const mockProduct: Product = {
  id: 1,
  name: 'Test Shirt',
  slug: 'test-shirt',
  description: 'A test shirt',
  basePrice: 29.99,
  imageUrl: null,
  categoryId: 1,
  categoryName: 'Tops',
  variants: [
    { id: 1, size: 'M', color: 'Black', colorHex: '#000000', priceAdjustment: 0, totalPrice: 29.99, stockQuantity: 10, sku: 'TS-BLK-M', imageUrl: null, active: true },
    { id: 2, size: 'L', color: 'White', colorHex: '#FFFFFF', priceAdjustment: 5, totalPrice: 34.99, stockQuantity: 5, sku: 'TS-WHT-L', imageUrl: null, active: true },
  ],
  active: true,
};

const mockProductSinglePrice: Product = {
  ...mockProduct,
  id: 2,
  name: 'Fixed Price Hat',
  slug: 'fixed-price-hat',
  variants: [
    { id: 3, size: 'One Size', color: 'Black', colorHex: '#000000', priceAdjustment: 0, totalPrice: 19.99, stockQuantity: 20, sku: 'FH-BLK-OS', imageUrl: null, active: true },
  ],
  basePrice: 19.99,
};

function renderWithRouter(ui: React.ReactElement) {
  return render(<BrowserRouter>{ui}</BrowserRouter>);
}

describe('ProductCard', () => {
  it('renders product name', () => {
    renderWithRouter(<ProductCard product={mockProduct} />);
    expect(screen.getByText('Test Shirt')).toBeInTheDocument();
  });

  it('renders category name', () => {
    renderWithRouter(<ProductCard product={mockProduct} />);
    expect(screen.getByText('Tops')).toBeInTheDocument();
  });

  it('renders price range for multi-variant products', () => {
    renderWithRouter(<ProductCard product={mockProduct} />);
    expect(screen.getByText('$29.99 – $34.99')).toBeInTheDocument();
  });

  it('renders single price for same-price variants', () => {
    renderWithRouter(<ProductCard product={mockProductSinglePrice} />);
    expect(screen.getByText('$19.99')).toBeInTheDocument();
  });

  it('links to product detail page', () => {
    renderWithRouter(<ProductCard product={mockProduct} />);
    const link = screen.getByRole('link');
    expect(link).toHaveAttribute('href', '/products/1');
  });

  it('shows placeholder icon when no image', () => {
    renderWithRouter(<ProductCard product={mockProduct} />);
    const svg = document.querySelector('svg');
    expect(svg).toBeInTheDocument();
  });
});
