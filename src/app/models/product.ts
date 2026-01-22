// src/app/models/product.ts

// OPTION TYPES MUST MATCH EXISTING COMPONENT CODE
export type CustomOptionType = 'colour' | 'size' | 'material';

export interface CustomOptionItem {
  label: string;
  priceModifier: number;
}

export interface CustomOptionGroup {
  type: CustomOptionType;
  options: CustomOptionItem[];
}

export interface Product {
  productId: string;
  name: string;
  description?: string;
  category?: string;
  basePrice: number;
  previewImage: string;

  customOptions: CustomOptionGroup[];

  // 🔹 Inventory fields for admin dashboard
  stockLevel?: number;        // current stock
  reorderThreshold?: number;  // threshold for low-stock alert
  isActive?: boolean;         // can hide products later if needed
}
