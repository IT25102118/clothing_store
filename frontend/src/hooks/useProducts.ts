import { useQuery } from '@tanstack/react-query';
import { productApi } from '../api/productApi';

export function useProducts(page = 0, size = 10, categoryId?: number, search?: string) {
  return useQuery({
    queryKey: ['products', page, size, categoryId, search],
    queryFn: () => productApi.getProducts({ page, size, categoryId, search }),
    placeholderData: (prev) => prev,
  });
}

export function useProductById(id: number) {
  return useQuery({
    queryKey: ['product', id],
    queryFn: () => productApi.getProductById(id),
    enabled: !!id,
  });
}

export function useProductBySlug(slug: string) {
  return useQuery({
    queryKey: ['product', 'slug', slug],
    queryFn: () => productApi.getProductBySlug(slug),
    enabled: !!slug,
  });
}

export function useCategories() {
  return useQuery({
    queryKey: ['categories'],
    queryFn: productApi.getCategories,
    staleTime: 5 * 60 * 1000,
  });
}

export function useProductsByCategory(categoryId: number, page = 0, size = 10) {
  return useQuery({
    queryKey: ['products', 'category', categoryId, page, size],
    queryFn: () => productApi.getProductsByCategory(categoryId, { page, size }),
    enabled: !!categoryId,
    placeholderData: (prev) => prev,
  });
}
