import axiosInstance from './axiosInstance';
import type { Order, CheckoutRequest } from '../types/order';

export const orderApi = {
  checkout: (data: CheckoutRequest) =>
    axiosInstance.post<Order>('/api/orders/checkout', data).then((r) => r.data),

  getOrders: () =>
    axiosInstance.get<Order[]>('/api/orders').then((r) => r.data),

  getOrder: (id: number) =>
    axiosInstance.get<Order>(`/api/orders/${id}`).then((r) => r.data),
};
