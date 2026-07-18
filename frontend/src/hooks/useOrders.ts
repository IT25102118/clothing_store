import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { orderApi } from '../api/orderApi';
import type { CheckoutRequest } from '../types/order';

export function useOrders() {
  return useQuery({
    queryKey: ['orders'],
    queryFn: orderApi.getOrders,
  });
}

export function useOrder(id: number) {
  return useQuery({
    queryKey: ['order', id],
    queryFn: () => orderApi.getOrder(id),
    enabled: !!id,
  });
}

export function useCheckout() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: CheckoutRequest) => orderApi.checkout(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
}
