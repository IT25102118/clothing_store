import { PackageOpen } from 'lucide-react';

interface EmptyStateProps {
  title?: string;
  message?: string;
}

export function EmptyState({
  title = 'No products found',
  message = 'Try adjusting your search or filter criteria.',
}: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-gray-500">
      <PackageOpen className="h-16 w-16 mb-4 text-gray-300" />
      <h3 className="text-lg font-medium text-gray-700 mb-1">{title}</h3>
      <p className="text-sm">{message}</p>
    </div>
  );
}
