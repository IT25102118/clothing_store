import { useState } from 'react';
import { Search, SlidersHorizontal, X } from 'lucide-react';
import { useProducts, useCategories } from '../hooks/useProducts';
import { ProductGrid } from '../components/product/ProductGrid';
import { Button } from '../components/ui/Button';
import { EmptyState } from '../components/ui/EmptyState';
import type { Category } from '../types/product';

export function ProductListPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [categoryId, setCategoryId] = useState<number | undefined>();
  const [showFilters, setShowFilters] = useState(false);

  const { data: categories } = useCategories();
  const { data, isLoading, isError, error, refetch } = useProducts(page, 12, categoryId, search || undefined);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    setSearch(searchInput);
  };

  const handleCategorySelect = (cat: Category | undefined) => {
    setPage(0);
    setCategoryId(cat?.id);
  };

  const clearFilters = () => {
    setPage(0);
    setSearch('');
    setSearchInput('');
    setCategoryId(undefined);
  };

  const totalPages = data?.totalPages ?? 0;
  const hasFilters = search || categoryId;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Products</h1>
        <button
          onClick={() => setShowFilters(!showFilters)}
          className="sm:hidden flex items-center gap-2 text-sm text-gray-600"
        >
          <SlidersHorizontal className="h-4 w-4" />
          Filters
        </button>
      </div>

      <div className="flex flex-col lg:flex-row gap-8">
        <aside className={`lg:w-64 shrink-0 ${showFilters ? 'block' : 'hidden lg:block'}`}>
          <div className="lg:sticky lg:top-24 space-y-6">
            <form onSubmit={handleSearch}>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                <input
                  type="text"
                  value={searchInput}
                  onChange={(e) => setSearchInput(e.target.value)}
                  placeholder="Search products..."
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
                />
              </div>
              {searchInput !== search && (
                <Button type="submit" size="sm" className="w-full mt-2">
                  Search
                </Button>
              )}
            </form>

            <div>
              <h3 className="text-sm font-semibold text-gray-900 mb-3">Categories</h3>
              <div className="space-y-2">
                <button
                  onClick={() => handleCategorySelect(undefined)}
                  className={`block w-full text-left px-3 py-2 text-sm rounded-lg transition-colors ${
                    !categoryId ? 'bg-blue-50 text-blue-700 font-medium' : 'text-gray-600 hover:bg-gray-100'
                  }`}
                >
                  All
                </button>
                {categories?.map((cat) => (
                  <button
                    key={cat.id}
                    onClick={() => handleCategorySelect(cat)}
                    className={`block w-full text-left px-3 py-2 text-sm rounded-lg transition-colors ${
                      categoryId === cat.id ? 'bg-blue-50 text-blue-700 font-medium' : 'text-gray-600 hover:bg-gray-100'
                    }`}
                  >
                    {cat.name}
                  </button>
                ))}
              </div>
            </div>

            {hasFilters && (
              <Button variant="ghost" size="sm" onClick={clearFilters} className="w-full flex items-center justify-center gap-1">
                <X className="h-4 w-4" /> Clear Filters
              </Button>
            )}
          </div>
        </aside>

        <div className="flex-1 min-w-0">
          {hasFilters && (
            <div className="flex items-center gap-2 mb-4 text-sm text-gray-500">
              <span>
                {data?.totalElements ?? 0} result{data?.totalElements !== 1 ? 's' : ''}
                {search && <> for &ldquo;{search}&rdquo;</>}
              </span>
              {(page > 0) && <span>(page {page + 1})</span>}
            </div>
          )}

          <ProductGrid
            products={data?.content}
            isLoading={isLoading}
            isError={isError}
            error={error}
            onRetry={refetch}
          />

          {totalPages > 1 && (
            <div className="flex items-center justify-center gap-2 mt-8">
              <Button
                variant="outline"
                size="sm"
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
              >
                Previous
              </Button>
              {Array.from({ length: totalPages }, (_, i) => (
                <Button
                  key={i}
                  variant={i === page ? 'primary' : 'ghost'}
                  size="sm"
                  onClick={() => setPage(i)}
                >
                  {i + 1}
                </Button>
              ))}
              <Button
                variant="outline"
                size="sm"
                disabled={page >= totalPages - 1}
                onClick={() => setPage(page + 1)}
              >
                Next
              </Button>
            </div>
          )}

          {data?.content && data.content.length === 0 && !isLoading && (
            <EmptyState
              title="No results found"
              message="Try a different search term or filter."
            />
          )}
        </div>
      </div>
    </div>
  );
}
