export interface CartItem {
  id: number;
  variantId: number;
  productName: string;
  productSlug: string;
  productImageUrl: string | null;
  size: string;
  color: string;
  colorHex: string | null;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface Cart {
  id: number;
  items: CartItem[];
  totalItems: number;
  subtotal: number;
}

export interface AddCartItemRequest {
  variantId: number;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}
