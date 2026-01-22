

import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth';
import { CartService } from './services/cart';
import { ProductService } from './services/product';
import { Subscription } from 'rxjs';
import { LowStockAlerts } from './shared/components/low-stock-alerts/low-stock-alerts';
import { Navbar } from './shared/components/navbar/navbar';
import { Footer } from './shared/components/footer/footer';
import { OrderService } from './services/order';
import { InventoryService } from './services/inventory';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, LowStockAlerts, Navbar, Footer],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit, OnDestroy {
  lowStockCount = 0;
  cartCount = 0;
  showLowStockModal = false;
  private productsSubscription?: Subscription;
  private cartSubscription?: Subscription;

  constructor(
    private authService: AuthService,
    private router: Router,
    public cartService: CartService,
    private productService: ProductService,
    private orderService: OrderService,
    private inventoryService: InventoryService
  ) { }

  ngOnInit(): void {
    // Subscribe to low stock count from Inventory Service
    this.productsSubscription = this.inventoryService.lowStockCount$.subscribe(
      count => this.lowStockCount = count
    );

    // Subscribe to cart to update cart count
    this.cartSubscription = this.cartService.cart$.subscribe(
      cart => {
        this.cartCount = cart ? cart.items.length : 0;
      }
    );
    // Initialize cart
    this.cartService.getCart().subscribe();
  }

  ngOnDestroy(): void {
    if (this.productsSubscription) {
      this.productsSubscription.unsubscribe();
    }
    if (this.cartSubscription) {
      this.cartSubscription.unsubscribe();
    }
  }

  get isLoggedIn(): boolean {
    return !!this.authService.getCurrentUser();
  }

  get isAdmin(): boolean {
    const user = this.authService.getCurrentUser();
    return !!user && user.role === 'ADMIN';
  }

  openLowStockAlerts(): void {
    this.showLowStockModal = true;
  }

  closeLowStockAlerts(): void {
    this.showLowStockModal = false;
  }


  logout(): void {
    this.authService.logout();
    this.cartService.clearCart().subscribe();
    this.router.navigate(['/login']);
  }
}
