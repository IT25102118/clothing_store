import type { Product } from '../../types/product';
import { ProductCard } from './ProductCard';
import { Spinner } from '../ui/Spinner';
import { ErrorMessage } from '../ui/ErrorMessage';
import { EmptyState } from '../ui/EmptyState';

interface ProductGridProps {
  products: Product[] | undefined;
  isLoading: boolean;
  isError: boolean;
  error?: Error | null;
  onRetry?: () => void;
}

export function ProductGrid({ products, isLoading, isError, error, onRetry }: ProductGridProps) {
  if (isLoading) return <Spinner size="lg" />;

  if (isError) {
    return (
      <ErrorMessage
        message={error?.message || 'Failed to load products. Please make sure the backend server is running.'}
        onRetry={onRetry}
      />
    );
  }

  if (!products || products.length === 0) return <EmptyState />;

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      {products.map((product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  );
}
