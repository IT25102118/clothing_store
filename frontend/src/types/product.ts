export interface ProductVariant {
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

export interface Product {
  id: number;
  name: string;
  slug: string;
  description: string;
  basePrice: number;
  imageUrl: string | null;
  categoryId: number;
  categoryName: string;
  variants: ProductVariant[];
  active: boolean;
}

export interface Category {
  id: number;
  name: string;
  slug: string;
  description: string | null;
  imageUrl: string | null;
  parentId: number | null;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
