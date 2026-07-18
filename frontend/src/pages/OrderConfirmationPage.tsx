import { useParams, Link } from 'react-router-dom';
import { CheckCircle, ArrowLeft, ShoppingBag } from 'lucide-react';
import { useOrder } from '../hooks/useOrders';
import { Button } from '../components/ui/Button';
import { Spinner } from '../components/ui/Spinner';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { formatCurrency } from '../utils/formatCurrency';

export function OrderConfirmationPage() {
  const { id } = useParams<{ id: string }>();
  const orderId = Number(id);
  const { data: order, isLoading, isError, error, refetch } = useOrder(orderId);

  if (isLoading) return <Spinner size="lg" />;

  if (isError) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-12">
        <ErrorMessage message={error?.message || 'Failed to load order.'} onRetry={refetch} />
      </div>
    );
  }

  if (!order) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-16 text-center">
        <ShoppingBag className="h-16 w-16 text-gray-300 mx-auto mb-4" />
        <h2 className="text-xl font-semibold text-gray-700">Order not found</h2>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="text-center mb-8">
        <CheckCircle className="h-16 w-16 text-green-500 mx-auto mb-4" />
        <h1 className="text-2xl font-bold text-gray-900">Order Confirmed!</h1>
        <p className="text-gray-500 mt-1">Thank you for your purchase.</p>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 p-6 mb-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-semibold text-gray-900">Order #{order.id}</h2>
          <span className="text-xs font-medium text-green-600 bg-green-50 px-2 py-1 rounded-full">
            {order.status}
          </span>
        </div>

        <div className="text-sm text-gray-500 space-y-1 mb-4">
          <p>Placed on {new Date(order.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}</p>
          <p>Shipping to: {order.shippingAddress}</p>
          <p>Payment: {order.paymentStatus === 'PAID' ? 'Paid' : 'Unpaid'}</p>
        </div>

        <div className="border-t border-gray-200 pt-4">
          <h3 className="text-sm font-semibold text-gray-900 mb-3">Items</h3>
          <div className="space-y-3">
            {order.items.map((item) => (
              <div key={item.id} className="flex justify-between text-sm">
                <div className="text-gray-600">
                  <span className="text-gray-900 font-medium">{item.productName}</span>
                  <span className="text-gray-400 ml-1">
                    ({item.size}, {item.color}) &times; {item.quantity}
                  </span>
                </div>
                <span className="text-gray-900 font-medium">{formatCurrency(item.subtotal)}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="border-t border-gray-200 mt-4 pt-4 flex justify-between text-base font-semibold text-gray-900">
          <span>Total</span>
          <span>{formatCurrency(order.totalAmount)}</span>
        </div>
      </div>

      <div className="flex justify-center gap-3">
        <Link to="/products">
          <Button variant="outline"><ArrowLeft className="h-4 w-4 mr-1" /> Continue Shopping</Button>
        </Link>
      </div>
    </div>
  );
}
