import { Link } from 'react-router-dom';
import { ShoppingBag } from 'lucide-react';
import { Button } from '../ui/Button';
import { formatCurrency } from '../../utils/formatCurrency';
import { useClearCart } from '../../hooks/useCart';
import type { Cart } from '../../types/cart';

interface CartSummaryProps {
  cart: Cart;
}

export function CartSummary({ cart }: CartSummaryProps) {
  const clearMutation = useClearCart();

  return (
    <div className="bg-gray-50 rounded-xl p-6 border border-gray-200">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Order Summary</h3>

      <div className="space-y-3 text-sm">
        <div className="flex justify-between text-gray-600">
          <span>Items ({cart.totalItems})</span>
          <span>{formatCurrency(cart.subtotal)}</span>
        </div>
        <div className="flex justify-between text-gray-600">
          <span>Shipping</span>
          <span className="text-green-600 font-medium">Free</span>
        </div>
        <div className="border-t border-gray-200 pt-3 flex justify-between text-base font-semibold text-gray-900">
          <span>Total</span>
          <span>{formatCurrency(cart.subtotal)}</span>
        </div>
      </div>

      <Link to="/checkout">
        <Button size="lg" className="w-full mt-6">
          <ShoppingBag className="h-4 w-4 mr-2" />
          Proceed to Checkout
        </Button>
      </Link>

      <Button
        variant="ghost"
        size="sm"
        className="w-full mt-2 text-gray-500"
        onClick={() => clearMutation.mutate()}
        loading={clearMutation.isPending}
      >
        Clear Cart
      </Button>
    </div>
  );
}
