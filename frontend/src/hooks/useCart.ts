import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { cartApi } from '../api/cartApi';
import type { AddCartItemRequest, UpdateCartItemRequest } from '../types/cart';

export function useCart() {
  return useQuery({
    queryKey: ['cart'],
    queryFn: cartApi.getCart,
  });
}

export function useAddToCart() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: AddCartItemRequest) => cartApi.addItem(data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cart'] }),
  });
}

export function useUpdateCartItem() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ itemId, data }: { itemId: number; data: UpdateCartItemRequest }) =>
      cartApi.updateItem(itemId, data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cart'] }),
  });
}

export function useRemoveCartItem() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (itemId: number) => cartApi.removeItem(itemId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cart'] }),
  });
}

export function useClearCart() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: cartApi.clearCart,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cart'] }),
  });
}
