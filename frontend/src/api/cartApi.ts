import axiosInstance from './axiosInstance';
import type { Cart, AddCartItemRequest, UpdateCartItemRequest } from '../types/cart';

export const cartApi = {
  getCart: () =>
    axiosInstance.get<Cart>('/api/cart').then((r) => r.data),

  addItem: (data: AddCartItemRequest) =>
    axiosInstance.post<Cart>('/api/cart/items', data).then((r) => r.data),

  updateItem: (itemId: number, data: UpdateCartItemRequest) =>
    axiosInstance.put<Cart>(`/api/cart/items/${itemId}`, data).then((r) => r.data),

  removeItem: (itemId: number) =>
    axiosInstance.delete<Cart>(`/api/cart/items/${itemId}`).then((r) => r.data),

  clearCart: () =>
    axiosInstance.delete('/api/cart').then(() => undefined),
};
