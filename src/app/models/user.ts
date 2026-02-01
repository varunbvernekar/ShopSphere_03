export type UserRole = 'ADMIN' | 'CUSTOMER';

export interface Address {
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
}

export interface User {
  id?: number;
  name: string;
  email: string;
  password?: string;
  role?: UserRole;
  phoneNumber?: string;
  address?: Address;
  dateOfBirth?: string;
  gender?: string;
}
