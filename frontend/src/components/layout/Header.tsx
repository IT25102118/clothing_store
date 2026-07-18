import { Link } from 'react-router-dom';
import { ShoppingBag, User, LogIn } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export function Header() {
  const { user, logout } = useAuth();

  return (
    <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link to="/" className="flex items-center gap-2">
            <ShoppingBag className="h-6 w-6 text-blue-600" />
            <span className="text-xl font-bold text-gray-900">Clothing Store</span>
          </Link>

          <nav className="flex items-center gap-6">
            <Link to="/products" className="text-sm font-medium text-gray-600 hover:text-gray-900 transition-colors">
              Products
            </Link>

            {user ? (
              <div className="flex items-center gap-4">
                <span className="text-sm text-gray-500">{user.email}</span>
                <button
                  onClick={logout}
                  className="text-sm text-gray-600 hover:text-gray-900 flex items-center gap-1"
                >
                  <User className="h-4 w-4" />
                  Logout
                </button>
              </div>
            ) : (
              <Link
                to="/login"
                className="text-sm font-medium text-gray-600 hover:text-gray-900 flex items-center gap-1"
              >
                <LogIn className="h-4 w-4" />
                Login
              </Link>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
}
