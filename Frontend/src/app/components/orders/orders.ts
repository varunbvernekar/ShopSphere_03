
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Order, OrderStatus } from '../../models/order';
import { OrderService } from '../../services/order';
import { AuthService } from '../../services/auth';
import { User } from '../../models/user';
import { FormsModule } from '@angular/forms';
import { DeliveryTracking } from './delivery-tracking/delivery-tracking';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, DeliveryTracking],
  templateUrl: './orders.html',
  styleUrl: './orders.css'
})
export class OrdersPage implements OnInit {
  // Enum values for the progress stepper in the UI
  orderSteps: OrderStatus[] = ['Confirmed', 'Packed', 'Shipped', 'Delivered'];

  orders: Order[] = [];
  selectedOrder: Order | null = null;

  currentUser: User | null = null;
  isLoading = false;
  errorMessage = '';

  constructor(
    private orderService: OrderService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    // 1. Identify User Role
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser?.id) {
      this.loadOrders();
    }
  }

  /**
   * Fetches orders for the current user.
   */
  private loadOrders(): void {
    if (!this.currentUser?.id) return;

    this.isLoading = true;
    this.errorMessage = '';

    this.orderService.getOrdersForUser(this.currentUser.id).subscribe({
      next: (orders) => {
        this.orders = orders;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load orders.';
        this.isLoading = false;
      }
    });
  }

  // --- Customer Actions ---

  selectOrder(order: Order): void {
    this.selectedOrder = order;
  }

  cancelOrder(order: Order): void {
    if (!this.canCancel(order)) {
      console.log('This order cannot be cancelled.');
      return;
    }
    if (!confirm('Are you sure you want to cancel this order?')) return;

    this.orderService.cancelOrder(order.id!).subscribe({
      next: (updatedOrder) => {
        console.log('Order cancelled.');
        this.updateLocalOrder(updatedOrder);
      },
      error: (err) => console.error(err.error?.message || 'Failed to cancel order')
    });
  }


  // --- Helpers ---

  private updateLocalOrder(updated: Order): void {
    const index = this.orders.findIndex(u => u.id === updated.id);
    if (index !== -1) this.orders[index] = updated;
    if (this.selectedOrder?.id === updated.id) this.selectedOrder = updated;
  }

  getTotalItems(order: Order): number {
    return order.items?.reduce((sum, item) => sum + item.quantity, 0) || 0;
  }

  getStatusIcon(status: OrderStatus): string {
    const icons: Record<string, string> = {
      'Confirmed': 'schedule',
      'Packed': 'package_2',
      'Shipped': 'local_shipping',
      'Delivered': 'check_circle'
    };
    return icons[status] || 'assignment';
  }

  canCancel(order: Order): boolean {
    return order.status !== 'Shipped' && order.status !== 'Delivered' && order.status !== 'Cancelled';
  }

  getOrderId(order: Order): string {
    return order.id ? `ORD${order.id.toString().padStart(3, '0')}` : 'ORD000';
  }
}
