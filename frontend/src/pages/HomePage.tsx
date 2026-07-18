import { Link } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';
import { useProducts } from '../hooks/useProducts';
import { ProductGrid } from '../components/product/ProductGrid';

export function HomePage() {
  const { data, isLoading, isError, error, refetch } = useProducts(0, 8);

  return (
    <div>
      <section className="bg-gradient-to-br from-blue-600 via-blue-700 to-purple-700 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 text-center">
          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold mb-4">
            Discover Your Style
          </h1>
          <p className="text-lg sm:text-xl text-blue-100 mb-8 max-w-2xl mx-auto">
            Premium clothing for every occasion. Explore our curated collection of tops, bottoms, outerwear, and accessories.
          </p>
          <Link
            to="/products"
            className="inline-flex items-center gap-2 bg-white text-blue-700 px-8 py-3 rounded-lg font-semibold hover:bg-blue-50 transition-colors"
          >
            Shop Now <ArrowRight className="h-5 w-5" />
          </Link>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-2xl font-bold text-gray-900">Featured Products</h2>
          <Link to="/products" className="text-sm font-medium text-blue-600 hover:text-blue-800 flex items-center gap-1">
            View All <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
        <ProductGrid
          products={data?.content}
          isLoading={isLoading}
          isError={isError}
          error={error}
          onRetry={refetch}
        />
      </section>

      <section className="bg-gray-100 py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Why Shop With Us?</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-8">
            <div className="bg-white rounded-xl p-6 shadow-sm">
              <div className="text-blue-600 text-3xl font-bold mb-2">Free Shipping</div>
              <p className="text-gray-600">On orders over $50</p>
            </div>
            <div className="bg-white rounded-xl p-6 shadow-sm">
              <div className="text-blue-600 text-3xl font-bold mb-2">Easy Returns</div>
              <p className="text-gray-600">30-day return policy</p>
            </div>
            <div className="bg-white rounded-xl p-6 shadow-sm">
              <div className="text-blue-600 text-3xl font-bold mb-2">Quality Guarantee</div>
              <p className="text-gray-600">100% satisfaction</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
