import { Link } from 'react-router-dom';
import { Home, SearchX } from 'lucide-react';
import { Button } from '../components/ui/Button';

export function NotFoundPage() {
  return (
    <div className="min-h-[60vh] flex items-center justify-center px-4">
      <div className="text-center">
        <SearchX className="h-20 w-20 text-gray-300 mx-auto mb-6" />
        <h1 className="text-4xl font-bold text-gray-900 mb-2">404</h1>
        <p className="text-lg text-gray-600 mb-6">Page not found</p>
        <p className="text-sm text-gray-500 mb-8">
          The page you're looking for doesn't exist or has been moved.
        </p>
        <Link to="/products">
          <Button>
            <Home className="h-4 w-4 mr-2" />
            Browse Products
          </Button>
        </Link>
      </div>
    </div>
  );
}
