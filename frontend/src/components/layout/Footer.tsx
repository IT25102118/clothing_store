export function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-400 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div>
            <h3 className="text-white font-semibold mb-3">Clothing Store</h3>
            <p className="text-sm">Your destination for premium fashion.</p>
          </div>
          <div>
            <h3 className="text-white font-semibold mb-3">Quick Links</h3>
            <ul className="space-y-2 text-sm">
              <li><a href="/products" className="hover:text-white transition-colors">All Products</a></li>
              <li><a href="/" className="hover:text-white transition-colors">Home</a></li>
            </ul>
          </div>
          <div>
            <h3 className="text-white font-semibold mb-3">Contact</h3>
            <p className="text-sm">support@clothingstore.com</p>
          </div>
        </div>
        <div className="border-t border-gray-800 mt-8 pt-6 text-center text-sm">
          &copy; {new Date().getFullYear()} Clothing Store. All rights reserved.
        </div>
      </div>
    </footer>
  );
}
