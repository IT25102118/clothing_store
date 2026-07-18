import { Link } from 'react-router-dom';
import { Trash2, Minus, Plus, ShoppingBag } from 'lucide-react';
import type { CartItem } from '../../types/cart';
import { formatCurrency } from '../../utils/formatCurrency';
import { useUpdateCartItem, useRemoveCartItem } from '../../hooks/useCart';

interface CartItemRowProps {
  item: CartItem;
}

export function CartItemRow({ item }: CartItemRowProps) {
  const updateMutation = useUpdateCartItem();
  const removeMutation = useRemoveCartItem();

  const handleQuantityChange = (newQty: number) => {
    if (newQty < 1) return;
    updateMutation.mutate({ itemId: item.id, data: { quantity: newQty } });
  };

  return (
    <div className="flex items-center gap-4 py-4 border-b border-gray-200 last:border-b-0">
      <div className="w-20 h-20 bg-gray-100 rounded-lg overflow-hidden shrink-0 flex items-center justify-center">
        {item.productImageUrl ? (
          <img src={item.productImageUrl} alt={item.productName} className="w-full h-full object-cover" />
        ) : (
          <ShoppingBag className="h-8 w-8 text-gray-300" />
        )}
      </div>

      <div className="flex-1 min-w-0">
        <Link to={`/products/${item.id}`} className="text-sm font-medium text-gray-900 hover:text-blue-600 transition-colors line-clamp-1">
          {item.productName}
        </Link>
        <p className="text-xs text-gray-500 mt-0.5">
          {item.color} / {item.size}
        </p>
        <p className="text-sm font-semibold text-gray-900 mt-1">{formatCurrency(item.unitPrice)}</p>
      </div>

      <div className="flex items-center gap-1">
        <button
          onClick={() => handleQuantityChange(item.quantity - 1)}
          disabled={item.quantity <= 1 || updateMutation.isPending}
          className="p-1 rounded-md hover:bg-gray-100 disabled:opacity-30"
        >
          <Minus className="h-4 w-4" />
        </button>
        <span className="w-8 text-center text-sm font-medium">{item.quantity}</span>
        <button
          onClick={() => handleQuantityChange(item.quantity + 1)}
          disabled={updateMutation.isPending}
          className="p-1 rounded-md hover:bg-gray-100 disabled:opacity-30"
        >
          <Plus className="h-4 w-4" />
        </button>
      </div>

      <div className="text-right w-24">
        <p className="text-sm font-semibold text-gray-900">{formatCurrency(item.subtotal)}</p>
      </div>

      <button
        onClick={() => removeMutation.mutate(item.id)}
        disabled={removeMutation.isPending}
        className="p-2 text-gray-400 hover:text-red-500 transition-colors disabled:opacity-30"
        title="Remove item"
      >
        <Trash2 className="h-4 w-4" />
      </button>
    </div>
  );
}
