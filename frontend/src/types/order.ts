export interface OrderItem {
  id: number;
  variantId: number;
  productName: string;
  productSlug: string;
  productImageUrl: string | null;
  size: string;
  color: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface Order {
  id: number;
  status: string;
  totalAmount: number;
  shippingAddress: string;
  paymentStatus: string;
  items: OrderItem[];
  createdAt: string;
}

export interface CheckoutRequest {
  shippingAddress: string;
}
