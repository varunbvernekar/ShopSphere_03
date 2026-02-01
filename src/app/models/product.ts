
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


  stockLevel?: number;
  reorderThreshold?: number;
  isActive?: boolean;
}
