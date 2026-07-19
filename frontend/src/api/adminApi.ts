import axiosInstance from './axiosInstance';
import type { Product, VariantResponse } from '../types/product';
import type { Order } from '../types/order';

export interface CreateProductRequest {
  name: string;
  slug: string;
  description?: string;
  basePrice: number;
  imageUrl?: string;
  categoryId: number;
  active?: boolean;
}

export interface UpdateProductRequest {
  name?: string;
  slug?: string;
  description?: string;
  basePrice?: number;
  imageUrl?: string;
  categoryId?: number;
  active?: boolean;
}

export interface CreateVariantRequest {
  size: string;
  color: string;
  colorHex?: string;
  priceAdjustment?: number;
  stockQuantity: number;
  sku: string;
  imageUrl?: string;
  active?: boolean;
}

export interface UpdateVariantRequest {
  stockQuantity?: number;
  priceAdjustment?: number;
  active?: boolean;
}

export interface VariantResponseData {
  id: number;
  size: string;
  color: string;
  colorHex: string | null;
  priceAdjustment: number;
  totalPrice: number;
  stockQuantity: number;
  sku: string;
  imageUrl: string | null;
  active: boolean;
}

export const adminApi = {
  createProduct: (data: CreateProductRequest) =>
    axiosInstance.post<Product>('/api/admin/products', data).then((r) => r.data),

  updateProduct: (id: number, data: UpdateProductRequest) =>
    axiosInstance.put<Product>(`/api/admin/products/${id}`, data).then((r) => r.data),

  createVariant: (productId: number, data: CreateVariantRequest) =>
    axiosInstance.post<VariantResponseData>(`/api/admin/products/${productId}/variants`, data).then((r) => r.data),

  updateVariant: (variantId: number, data: UpdateVariantRequest) =>
    axiosInstance.put<VariantResponseData>(`/api/admin/products/variants/${variantId}`, data).then((r) => r.data),

  updateOrderStatus: (orderId: number, status: string) =>
    axiosInstance.put(`/api/admin/orders/${orderId}/status`, { status }).then((r) => r.data),

  getOrders: () =>
    axiosInstance.get<Order[]>('/api/orders').then((r) => r.data),

  getProducts: () =>
    axiosInstance.get<Product[]>('/api/products?size=100').then((r) => r.data.content),
};
