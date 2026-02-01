import { CartItem } from './cart-item';

export interface Cart {
    id: number;
    userId: number;
    items: CartItem[];
    subtotal: number;
    tax: number;
    shipping: number;
    totalAmount: number;
}
