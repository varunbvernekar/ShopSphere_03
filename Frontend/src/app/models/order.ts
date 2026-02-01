
export type OrderStatus = 'Placed' | 'Confirmed' | 'Packed' | 'Shipped' | 'Delivered' | 'Cancelled';

export interface OrderItem {
  productId: string;
  name: string;
  image: string;
  quantity: number;
  color?: string;
  size?: string;
  material?: string;
  price: number;
}

export interface LogisticsInfo {
  carrier: string;
  trackingId: string;
  currentLocation: string;
}

export interface Order {
  id?: number;
  userId: number;
  placedOn: string;
  amount: number;
  status: OrderStatus;
  items: OrderItem[];
  estimatedDelivery: string;
  logistics: LogisticsInfo;
  deliveryAddress?: Address;
}

export interface Address {
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
}
