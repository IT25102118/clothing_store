import { useState } from 'react';
import { useProducts, useCategories } from '../../hooks/useProducts';
import { useOrders } from '../../hooks/useOrders';
import { adminApi, type CreateProductRequest, type UpdateVariantRequest } from '../../api/adminApi';
import { Button } from '../../components/ui/Button';
import { Spinner } from '../../components/ui/Spinner';
import { ErrorMessage } from '../../components/ui/ErrorMessage';
import { formatCurrency } from '../../utils/formatCurrency';
import { useQueryClient } from '@tanstack/react-query';
import { toast } from '../../utils/toast';

type Tab = 'products' | 'orders';

export function AdminDashboardPage() {
  const [tab, setTab] = useState<Tab>('products');

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Admin Dashboard</h1>

      <div className="flex gap-4 mb-6 border-b border-gray-200">
        <button
          onClick={() => setTab('products')}
          className={`pb-3 text-sm font-medium border-b-2 transition-colors ${
            tab === 'products' ? 'border-blue-600 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700'
          }`}
        >
          Product Management
        </button>
        <button
          onClick={() => setTab('orders')}
          className={`pb-3 text-sm font-medium border-b-2 transition-colors ${
            tab === 'orders' ? 'border-blue-600 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700'
          }`}
        >
          Order Management
        </button>
      </div>

      {tab === 'products' && <ProductManagement />}
      {tab === 'orders' && <OrderManagement />}
    </div>
  );
}

function ProductManagement() {
  const { data: products, isLoading, isError, error, refetch } = useProducts(0, 100);
  const { data: categories } = useCategories();
  const [showCreateForm, setShowCreateForm] = useState(false);
  const queryClient = useQueryClient();

  const handleCreate = async (data: CreateProductRequest) => {
    try {
      await adminApi.createProduct(data);
      queryClient.invalidateQueries({ queryKey: ['products'] });
      setShowCreateForm(false);
      toast.success('Product created successfully');
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Failed to create product');
    }
  };

  if (isLoading) return <Spinner />;
  if (isError) return <ErrorMessage message={error?.message || 'Failed to load products'} onRetry={refetch} />;

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-900">Products ({products?.totalElements ?? 0})</h2>
        <Button size="sm" onClick={() => setShowCreateForm(true)}>Add Product</Button>
      </div>

      {showCreateForm && (
        <ProductCreateForm
          categories={categories ?? []}
          onSubmit={handleCreate}
          onCancel={() => setShowCreateForm(false)}
        />
      )}

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Name</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Category</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Price</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Variants</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Active</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {products?.content.map((product) => (
              <ProductRow key={product.id} product={product} />
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function ProductRow({ product }: { product: { id: number; name: string; categoryName: string; basePrice: number; variants: { id: number; size: string; color: string; stockQuantity: number; active: boolean; sku: string; priceAdjustment: number }[]; active: boolean } }) {
  const [expanded, setExpanded] = useState(false);
  const queryClient = useQueryClient();
  const [editVariant, setEditVariant] = useState<{ id: number; stockQuantity: number; active: boolean } | null>(null);

  const handleUpdateVariant = async (variantId: number, data: UpdateVariantRequest) => {
    try {
      await adminApi.updateVariant(variantId, data);
      queryClient.invalidateQueries({ queryKey: ['products'] });
      setEditVariant(null);
      toast.success('Variant updated');
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Failed to update variant');
    }
  };

  return (
    <>
      <tr className="hover:bg-gray-50 cursor-pointer" onClick={() => setExpanded(!expanded)}>
        <td className="px-4 py-3 font-medium text-gray-900">{product.name}</td>
        <td className="px-4 py-3 text-gray-600">{product.categoryName}</td>
        <td className="px-4 py-3 text-gray-900">{formatCurrency(product.basePrice)}</td>
        <td className="px-4 py-3 text-gray-600">{product.variants.length}</td>
        <td className="px-4 py-3">
          <span className={`inline-flex px-2 py-0.5 rounded-full text-xs font-medium ${product.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
            {product.active ? 'Active' : 'Inactive'}
          </span>
        </td>
        <td className="px-4 py-3">
          <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); setExpanded(!expanded); }}>
            {expanded ? 'Hide' : 'Variants'}
          </Button>
        </td>
      </tr>
      {expanded && (
        <tr>
          <td colSpan={6} className="px-4 py-3 bg-gray-50">
            <div className="space-y-2">
              {product.variants.map((v) => (
                <div key={v.id} className="flex items-center gap-4 text-sm">
                  <span className="w-20 text-gray-600">{v.size} / {v.color}</span>
                  <span className="w-32 text-gray-500 font-mono text-xs">{v.sku}</span>
                  <span className="w-20 text-gray-900">Stock: {v.stockQuantity}</span>
                  <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${v.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                    {v.active ? 'Active' : 'Inactive'}
                  </span>
                  <Button size="sm" variant="ghost" onClick={() => setEditVariant({ id: v.id, stockQuantity: v.stockQuantity, active: v.active })}>
                    Edit
                  </Button>
                </div>
              ))}
            </div>

            {editVariant && (
              <div className="mt-3 p-3 bg-white rounded-lg border border-gray-200">
                <h4 className="text-sm font-medium mb-2">Edit Variant #{editVariant.id}</h4>
                <div className="flex items-center gap-3">
                  <div>
                    <label className="text-xs text-gray-500">Stock</label>
                    <input
                      type="number"
                      min="0"
                      value={editVariant.stockQuantity}
                      onChange={(e) => setEditVariant({ ...editVariant, stockQuantity: Math.max(0, parseInt(e.target.value) || 0) })}
                      className="w-20 px-2 py-1 border border-gray-300 rounded text-sm"
                    />
                  </div>
                  <div>
                    <label className="text-xs text-gray-500">Active</label>
                    <select
                      value={editVariant.active ? 'true' : 'false'}
                      onChange={(e) => setEditVariant({ ...editVariant, active: e.target.value === 'true' })}
                      className="px-2 py-1 border border-gray-300 rounded text-sm"
                    >
                      <option value="true">Active</option>
                      <option value="false">Inactive</option>
                    </select>
                  </div>
                  <Button size="sm" onClick={() => handleUpdateVariant(editVariant.id, { stockQuantity: editVariant.stockQuantity, active: editVariant.active })}>
                    Save
                  </Button>
                  <Button size="sm" variant="ghost" onClick={() => setEditVariant(null)}>Cancel</Button>
                </div>
              </div>
            )}
          </td>
        </tr>
      )}
    </>
  );
}

function ProductCreateForm({ categories, onSubmit, onCancel }: { categories: { id: number; name: string }[]; onSubmit: (data: CreateProductRequest) => Promise<void>; onCancel: () => void }) {
  const [name, setName] = useState('');
  const [slug, setSlug] = useState('');
  const [description, setDescription] = useState('');
  const [basePrice, setBasePrice] = useState('');
  const [categoryId, setCategoryId] = useState(categories[0]?.id ?? 0);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await onSubmit({
        name,
        slug: slug || name.toLowerCase().replace(/\s+/g, '-'),
        description,
        basePrice: parseFloat(basePrice),
        categoryId,
      });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white rounded-xl border border-gray-200 p-4 mb-4 space-y-3">
      <h3 className="text-sm font-semibold">New Product</h3>
      <div className="grid grid-cols-2 gap-3">
        <input placeholder="Name" value={name} onChange={(e) => setName(e.target.value)} required className="px-3 py-2 border border-gray-300 rounded-lg text-sm" />
        <input placeholder="Slug" value={slug} onChange={(e) => setSlug(e.target.value)} className="px-3 py-2 border border-gray-300 rounded-lg text-sm" />
      </div>
      <textarea placeholder="Description" value={description} onChange={(e) => setDescription(e.target.value)} className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm" rows={2} />
      <div className="grid grid-cols-2 gap-3">
        <input type="number" step="0.01" placeholder="Base Price" value={basePrice} onChange={(e) => setBasePrice(e.target.value)} required className="px-3 py-2 border border-gray-300 rounded-lg text-sm" />
        <select value={categoryId} onChange={(e) => setCategoryId(Number(e.target.value))} className="px-3 py-2 border border-gray-300 rounded-lg text-sm">
          {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
      </div>
      <div className="flex gap-2">
        <Button type="submit" size="sm" loading={submitting}>Create</Button>
        <Button type="button" size="sm" variant="ghost" onClick={onCancel}>Cancel</Button>
      </div>
    </form>
  );
}

function OrderManagement() {
  const queryClient = useQueryClient();
  const { data: orders, isLoading, isError, error, refetch } = useOrders();

  const handleStatusUpdate = async (orderId: number, status: string) => {
    try {
      await adminApi.updateOrderStatus(orderId, status);
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      toast.success(`Order #${orderId} updated to ${status}`);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : 'Failed to update order');
    }
  };

  if (isLoading) return <Spinner />;
  if (isError) return <ErrorMessage message={error?.message || 'Failed to load orders'} onRetry={refetch} />;

  return (
    <div>
      <h2 className="text-lg font-semibold text-gray-900 mb-4">Orders ({(orders ?? []).length})</h2>
      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Order ID</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Items</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Total</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Status</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Payment</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {orders?.map((order) => (
              <tr key={order.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-medium text-gray-900">#{order.id}</td>
                <td className="px-4 py-3 text-gray-600">{order.items.length}</td>
                <td className="px-4 py-3 text-gray-900">{formatCurrency(order.totalAmount)}</td>
                <td className="px-4 py-3">
                  <span className={`inline-flex px-2 py-0.5 rounded-full text-xs font-medium ${
                    order.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                    order.status === 'CONFIRMED' ? 'bg-blue-100 text-blue-700' :
                    order.status === 'SHIPPED' ? 'bg-purple-100 text-purple-700' :
                    order.status === 'DELIVERED' ? 'bg-green-100 text-green-700' :
                    'bg-red-100 text-red-700'
                  }`}>
                    {order.status}
                  </span>
                </td>
                <td className="px-4 py-3 text-gray-600">{order.paymentStatus}</td>
                <td className="px-4 py-3">
                  <select
                    value={order.status}
                    onChange={(e) => handleStatusUpdate(order.id, e.target.value)}
                    className="px-2 py-1 border border-gray-300 rounded text-sm"
                  >
                    <option value="PENDING">Pending</option>
                    <option value="CONFIRMED">Confirmed</option>
                    <option value="SHIPPED">Shipped</option>
                    <option value="DELIVERED">Delivered</option>
                    <option value="CANCELLED">Cancelled</option>
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
