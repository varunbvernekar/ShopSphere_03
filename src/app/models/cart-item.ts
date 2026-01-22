export interface CartItem {
  id: string;
  productId: string;
  name: string;
  image: string;
  quantity: number;
  price: number;
  color?: string;
  size?: string;
  material?: string;
}
