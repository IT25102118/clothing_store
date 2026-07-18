import type { ProductVariant } from '../../types/product';
import { formatCurrency } from '../../utils/formatCurrency';
import { useState, useMemo } from 'react';

interface VariantSelectorProps {
  variants: ProductVariant[];
  onSelect: (variant: ProductVariant) => void;
}

export function VariantSelector({ variants, onSelect }: VariantSelectorProps) {
  const [selectedSize, setSelectedSize] = useState<string | null>(null);
  const [selectedColor, setSelectedColor] = useState<string | null>(null);

  const sizes = useMemo(() => [...new Set(variants.map((v) => v.size))], [variants]);
  const colors = useMemo(() => {
    const colorMap = new Map<string, { name: string; hex: string | null }>();
    variants.forEach((v) => {
      if (!colorMap.has(v.color)) {
        colorMap.set(v.color, { name: v.color, hex: v.colorHex });
      }
    });
    return Array.from(colorMap.entries()).map(([colorName, info]) => ({ name: colorName, hex: info.hex }));
  }, [variants]);

  const selectedVariant = useMemo(() => {
    if (!selectedSize && !selectedColor) return null;
    return variants.find((v) =>
      (!selectedSize || v.size === selectedSize) &&
      (!selectedColor || v.color === selectedColor)
    ) || null;
  }, [variants, selectedSize, selectedColor]);

  const isSizeAvailable = (size: string) =>
    variants.some((v) => v.size === size && v.stockQuantity > 0);

  const isColorAvailable = (color: string) =>
    variants.some((v) => v.color === color && v.stockQuantity > 0);

  const handleSizeSelect = (size: string) => {
    setSelectedSize(size);
    const match = variants.find((v) => v.size === size && (!selectedColor || v.color === selectedColor));
    if (match) onSelect(match);
  };

  const handleColorSelect = (color: string) => {
    setSelectedColor(color);
    const match = variants.find((v) => v.color === color && (!selectedSize || v.size === selectedSize));
    if (match) onSelect(match);
  };

  return (
    <div className="space-y-6">
      <div>
        <h3 className="text-sm font-medium text-gray-900 mb-3">
          Color
          {selectedColor && <span className="text-gray-500 font-normal ml-1">— {selectedColor}</span>}
        </h3>
        <div className="flex flex-wrap gap-2">
          {colors.map((color) => {
            const available = isColorAvailable(color.name);
            return (
              <button
                key={color.name}
                onClick={() => available && handleColorSelect(color.name)}
                disabled={!available}
                title={color.name}
                className={`
                  w-10 h-10 rounded-full border-2 transition-all
                  ${selectedColor === color.name ? 'border-blue-600 ring-2 ring-blue-200' : 'border-gray-200'}
                  ${!available ? 'opacity-30 cursor-not-allowed' : 'hover:border-gray-400 cursor-pointer'}
                `}
                style={{ backgroundColor: color.hex || '#e5e7eb' }}
              />
            );
          })}
        </div>
      </div>

      <div>
        <h3 className="text-sm font-medium text-gray-900 mb-3">
          Size
          {selectedSize && <span className="text-gray-500 font-normal ml-1">— {selectedSize}</span>}
        </h3>
        <div className="flex flex-wrap gap-2">
          {sizes.map((size) => {
            const available = isSizeAvailable(size);
            return (
              <button
                key={size}
                onClick={() => available && handleSizeSelect(size)}
                disabled={!available}
                className={`
                  px-4 py-2 text-sm font-medium rounded-lg border transition-all
                  ${selectedSize === size
                    ? 'border-blue-600 bg-blue-50 text-blue-700'
                    : 'border-gray-200 text-gray-700 hover:border-gray-400'
                  }
                  ${!available ? 'opacity-30 cursor-not-allowed line-through' : 'cursor-pointer'}
                `}
              >
                {size}
              </button>
            );
          })}
        </div>
      </div>

      {selectedVariant && (
        <div className="bg-gray-50 rounded-lg p-4 border border-gray-200">
          <div className="text-sm text-gray-600 space-y-1">
            <p>
              Selected: <span className="font-medium text-gray-900">{selectedVariant.color} / {selectedVariant.size}</span>
            </p>
            <p>
              SKU: <span className="font-medium text-gray-900">{selectedVariant.sku}</span>
            </p>
            <p>
              Price: <span className="font-semibold text-gray-900 text-lg">{formatCurrency(selectedVariant.totalPrice)}</span>
            </p>
            <p>
              Stock: <span className={`font-medium ${selectedVariant.stockQuantity > 5 ? 'text-green-600' : selectedVariant.stockQuantity > 0 ? 'text-amber-600' : 'text-red-600'}`}>
                {selectedVariant.stockQuantity > 0 ? `${selectedVariant.stockQuantity} available` : 'Out of stock'}
              </span>
            </p>
          </div>
        </div>
      )}

      {!selectedSize && !selectedColor && variants.length === 1 && (
        <div className="text-sm text-gray-500 italic">Only one variant available.</div>
      )}
    </div>
  );
}
