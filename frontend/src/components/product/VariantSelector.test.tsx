import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { VariantSelector } from './VariantSelector';
import type { ProductVariant } from '../../types/product';

const mockVariants: ProductVariant[] = [
  { id: 1, size: 'S', color: 'Black', colorHex: '#000000', priceAdjustment: 0, totalPrice: 29.99, stockQuantity: 5, sku: 'TS-BLK-S', imageUrl: null, active: true },
  { id: 2, size: 'M', color: 'Black', colorHex: '#000000', priceAdjustment: 0, totalPrice: 29.99, stockQuantity: 10, sku: 'TS-BLK-M', imageUrl: null, active: true },
  { id: 3, size: 'S', color: 'White', colorHex: '#FFFFFF', priceAdjustment: 5, totalPrice: 34.99, stockQuantity: 0, sku: 'TS-WHT-S', imageUrl: null, active: true },
  { id: 4, size: 'M', color: 'White', colorHex: '#FFFFFF', priceAdjustment: 5, totalPrice: 34.99, stockQuantity: 3, sku: 'TS-WHT-M', imageUrl: null, active: true },
  { id: 5, size: 'S', color: 'Gray', colorHex: '#808080', priceAdjustment: 0, totalPrice: 29.99, stockQuantity: 0, sku: 'TS-GRY-S', imageUrl: null, active: true },
  { id: 6, size: 'M', color: 'Gray', colorHex: '#808080', priceAdjustment: 0, totalPrice: 29.99, stockQuantity: 0, sku: 'TS-GRY-M', imageUrl: null, active: true },
];

describe('VariantSelector', () => {
  it('renders color swatches', () => {
    const onSelect = vi.fn();
    render(<VariantSelector variants={mockVariants} onSelect={onSelect} />);
    const colorButtons = document.querySelectorAll('button[title]');
    const colorTitles = Array.from(colorButtons).map((b) => b.getAttribute('title'));
    expect(colorTitles).toContain('Black');
    expect(colorTitles).toContain('White');
    expect(colorTitles).toContain('Gray');
  });

  it('renders size buttons', () => {
    const onSelect = vi.fn();
    render(<VariantSelector variants={mockVariants} onSelect={onSelect} />);
    expect(screen.getByText('S')).toBeInTheDocument();
    expect(screen.getByText('M')).toBeInTheDocument();
  });

  it('disables out-of-stock color swatches', () => {
    const onSelect = vi.fn();
    render(<VariantSelector variants={mockVariants} onSelect={onSelect} />);
    const colorButtons = document.querySelectorAll('button[title]');
    const grayButton = Array.from(colorButtons).find((b) => b.getAttribute('title') === 'Gray');
    expect(grayButton).toBeDisabled();
  });

  it('selects variant and calls onSelect when size chosen', () => {
    const onSelect = vi.fn();
    render(<VariantSelector variants={mockVariants} onSelect={onSelect} />);
    fireEvent.click(screen.getByText('M'));
    expect(onSelect).toHaveBeenCalledWith(expect.objectContaining({ id: 2 }));
  });

  it('shows selected variant details', () => {
    const onSelect = vi.fn();
    render(<VariantSelector variants={mockVariants} onSelect={onSelect} />);
    fireEvent.click(screen.getByText('M'));
    expect(screen.getByText(/Black \/ M/)).toBeInTheDocument();
  });
});
