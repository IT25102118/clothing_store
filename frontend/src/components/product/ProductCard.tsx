import { Link } from 'react-router-dom';
import { ShoppingBag } from 'lucide-react';
import type { Product } from '../../types/product';
import { formatCurrency } from '../../utils/formatCurrency';

interface ProductCardProps {
  product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
  const basePrice = product.basePrice;
  const minVariantPrice = product.variants.length > 0
    ? Math.min(...product.variants.map((v) => v.totalPrice))
    : basePrice;
  const maxVariantPrice = product.variants.length > 0
    ? Math.max(...product.variants.map((v) => v.totalPrice))
    : basePrice;
  const priceDisplay = minVariantPrice === maxVariantPrice
    ? formatCurrency(minVariantPrice)
    : `${formatCurrency(minVariantPrice)} – ${formatCurrency(maxVariantPrice)}`;

  return (
    <Link
      to={`/products/${product.id}`}
      className="group bg-white rounded-xl border border-gray-200 overflow-hidden shadow-sm hover:shadow-md transition-all hover:-translate-y-0.5"
    >
      <div className="aspect-square bg-gray-100 flex items-center justify-center overflow-hidden">
        {product.imageUrl ? (
          <img
            src={product.imageUrl}
            alt={product.name}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            onError={(e) => {
              (e.target as HTMLImageElement).src = '';
              (e.target as HTMLImageElement).style.display = 'none';
              (e.target as HTMLImageElement).parentElement!.classList.add('flex', 'items-center', 'justify-center');
            }}
          />
        ) : (
          <ShoppingBag className="h-12 w-12 text-gray-300" />
        )}
      </div>
      <div className="p-4">
        <span className="text-xs font-medium text-blue-600 bg-blue-50 px-2 py-0.5 rounded-full">
          {product.categoryName}
        </span>
        <h3 className="mt-2 text-sm font-medium text-gray-900 line-clamp-2 group-hover:text-blue-600 transition-colors">
          {product.name}
        </h3>
        <p className="mt-1 text-sm font-semibold text-gray-900">{priceDisplay}</p>
      </div>
    </Link>
  );
}
