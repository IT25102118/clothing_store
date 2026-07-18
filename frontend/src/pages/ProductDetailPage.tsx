import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, ShoppingBag, PackageOpen } from 'lucide-react';
import { useProductById } from '../hooks/useProducts';
import { useAddToCart } from '../hooks/useCart';
import { VariantSelector } from '../components/product/VariantSelector';
import { Button } from '../components/ui/Button';
import { Spinner } from '../components/ui/Spinner';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { formatCurrency } from '../utils/formatCurrency';
import { useState } from 'react';
import type { ProductVariant } from '../types/product';

export function ProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const productId = Number(id);
  const { data: product, isLoading, isError, error, refetch } = useProductById(productId);
  const addToCartMutation = useAddToCart();
  const [selectedVariant, setSelectedVariant] = useState<ProductVariant | null>(null);
  const [addedToCart, setAddedToCart] = useState(false);

  if (isLoading) return <Spinner size="lg" />;

  if (isError) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <ErrorMessage
          message={error?.message || 'Failed to load product.'}
          onRetry={refetch}
        />
      </div>
    );
  }

  if (!product) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="flex flex-col items-center justify-center py-16 text-gray-500">
          <PackageOpen className="h-16 w-16 mb-4 text-gray-300" />
          <h3 className="text-lg font-medium text-gray-700 mb-1">Product not found</h3>
        </div>
      </div>
    );
  }

  const priceDisplay = selectedVariant
    ? formatCurrency(selectedVariant.totalPrice)
    : product.basePrice
    ? formatCurrency(product.basePrice)
    : product.variants.length === 1
    ? formatCurrency(product.variants[0].totalPrice)
    : `From ${formatCurrency(Math.min(...product.variants.map((v) => v.totalPrice)))}`;

  const variantToAdd = selectedVariant || (product.variants.length === 1 ? product.variants[0] : null);

  const handleAddToCart = () => {
    if (!variantToAdd) return;
    addToCartMutation.mutate(
      { variantId: variantToAdd.id, quantity: 1 },
      {
        onSuccess: () => {
          setAddedToCart(true);
          setTimeout(() => setAddedToCart(false), 2000);
        },
      }
    );
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <Link to="/products" className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 mb-6">
        <ArrowLeft className="h-4 w-4" /> Back to Products
      </Link>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 lg:gap-12">
        <div className="aspect-square bg-gray-100 rounded-xl overflow-hidden flex items-center justify-center">
          {product.imageUrl ? (
            <img
              src={product.imageUrl}
              alt={product.name}
              className="w-full h-full object-cover"
              onError={(e) => {
                (e.target as HTMLImageElement).style.display = 'none';
                (e.target as HTMLImageElement).parentElement!.classList.add('flex', 'items-center', 'justify-center');
              }}
            />
          ) : (
            <ShoppingBag className="h-24 w-24 text-gray-300" />
          )}
        </div>

        <div>
          <span className="text-xs font-medium text-blue-600 bg-blue-50 px-2 py-0.5 rounded-full">
            {product.categoryName}
          </span>
          <h1 className="mt-3 text-2xl sm:text-3xl font-bold text-gray-900">{product.name}</h1>
          <p className="mt-2 text-3xl font-bold text-gray-900">{priceDisplay}</p>

          {product.description && (
            <p className="mt-4 text-gray-600 leading-relaxed">{product.description}</p>
          )}

          <div className="mt-6 border-t border-gray-200 pt-6">
            {product.variants.length > 0 ? (
              <VariantSelector variants={product.variants} onSelect={setSelectedVariant} />
            ) : (
              <p className="text-sm text-gray-500">No variants available.</p>
            )}
          </div>

          <div className="mt-8 border-t border-gray-200 pt-6 flex gap-3">
            <Button
              size="lg"
              className="flex-1"
              disabled={!variantToAdd || addToCartMutation.isPending}
              onClick={handleAddToCart}
              loading={addToCartMutation.isPending}
            >
              {addedToCart ? 'Added!' : 'Add to Cart'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
