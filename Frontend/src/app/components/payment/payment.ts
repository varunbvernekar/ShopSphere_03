
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../../services/cart';
import { AuthService } from '../../services/auth';
import { OrderService } from '../../services/order';
import { ProductService } from '../../services/product';
import { InventoryService } from '../../services/inventory';
import { UserService } from '../../services/user';
import { Order, OrderStatus, Address } from '../../models/order';
import { Cart } from '../../models/cart';
import { CartItem } from '../../models/cart-item';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment.html',
  styleUrls: ['./payment.css']
})
export class Payment implements OnInit {
  cart: Cart | null = null;
  cartItems: CartItem[] = [];
  isLoading = false;
  errorMessage = '';

  // Address form
  address: Address = {
    street: '',
    city: '',
    state: '',
    zipCode: '',
    country: ''
  };

  // Payment form
  paymentMethod = 'card';
  cardNumber = '';
  cardName = '';
  cardExpiry = '';
  cardCvv = '';
  upiId = '';

  get subtotal(): number {
    return this.cart?.subtotal || 0;
  }

  get tax(): number {
    return this.cart?.tax || 0;
  }

  get shipping(): number {
    return this.cart?.shipping || 0;
  }

  get total(): number {
    return this.cart?.totalAmount || 0;
  }

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private userService: UserService,
    private orderService: OrderService,
    private productService: ProductService,
    private inventoryService: InventoryService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (!user || user.role !== 'CUSTOMER') {
      this.router.navigate(['/products']);
      return;
    }

    // Load cart from server
    this.cartService.getCart().subscribe({
      next: (cart) => {
        this.cart = cart;
        this.cartItems = cart ? cart.items : [];
        if (this.cartItems.length === 0) {
          this.router.navigate(['/products']);
        }
      },
      error: () => this.router.navigate(['/products'])
    });

    // Fetch latest user profile to pre-fill address
    if (user.id) {
      this.userService.getUser(user.id).subscribe({
        next: (userData) => {
          if (userData.address) {
            this.address = { ...userData.address };
          }
        },
        error: (err) => {
          console.error('Failed to fetch user profile for address pre-fill', err);
          // Fallback to cached user address if fresh fetch fails
          if (user.address) {
            this.address = { ...user.address };
          }
        }
      });
    }
  }

  onSubmit(): void {
    // Validate address
    if (!this.address.street || !this.address.city || !this.address.state ||
      !this.address.zipCode || !this.address.country) {
      this.errorMessage = 'Please fill in all address fields.';
      return;
    }

    // Validate payment (basic validation)
    if (this.paymentMethod === 'card') {
      if (!this.cardNumber || !this.cardName || !this.cardExpiry || !this.cardCvv) {
        this.errorMessage = 'Please fill in all payment details.';
        return;
      }
    } else if (this.paymentMethod === 'upi') {
      if (!this.upiId) {
        this.errorMessage = 'Please enter a valid UPI ID.';
        return;
      }
    }

    const user = this.authService.getCurrentUser();
    if (!user || !user.id) {
      this.errorMessage = 'Please log in to complete your order.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const today = new Date();
    const placedOn = today.toISOString();
    const estimated = new Date();
    estimated.setDate(today.getDate() + 7);
    const estimatedDelivery = estimated.toLocaleDateString('en-IN');

    const payload: Omit<Order, 'id'> = {
      userId: user.id,
      placedOn,
      amount: 0,
      status: 'Placed' as OrderStatus,
      items: [],
      estimatedDelivery,
      logistics: {
        carrier: 'Not assigned',
        trackingId: '-',
        currentLocation: 'Not Available'
      },
      deliveryAddress: { ...this.address }
    };

    this.orderService.createOrder(payload).subscribe({
      next: () => {
        this.cartService.clearCart().subscribe();
        this.router.navigate(['/orders']);
      },
      error: err => {
        console.error('Failed to place order', err);
        this.errorMessage = 'Failed to place order. Please try again.';
        this.isLoading = false;
      }
    });
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'https://via.placeholder.com/150?text=No+Image';
  }

  onCancel(): void {
    this.router.navigate(['/products']);
  }
}
