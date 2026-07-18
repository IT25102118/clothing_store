import axiosInstance from './axiosInstance';
import type { Product, Category, PageResponse } from '../types/product';

export const productApi = {
  getProducts: (params: { page?: number; size?: number; categoryId?: number; search?: string }) =>
    axiosInstance.get<PageResponse<Product>>('/api/products', { params }).then((r) => r.data),

  getProductById: (id: number) =>
    axiosInstance.get<Product>(`/api/products/${id}`).then((r) => r.data),

  getProductBySlug: (slug: string) =>
    axiosInstance.get<Product>(`/api/products/slug/${slug}`).then((r) => r.data),

  getCategories: () =>
    axiosInstance.get<Category[]>('/api/categories').then((r) => r.data),

  getProductsByCategory: (categoryId: number, params: { page?: number; size?: number }) =>
    axiosInstance.get<PageResponse<Product>>(`/api/categories/${categoryId}/products`, { params }).then((r) => r.data),
};
