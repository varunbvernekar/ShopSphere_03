
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
  selectedAdminOrder: Order | null = null;

  isAdmin = false;
  currentUser: User | null = null;
  isLoading = false;
  errorMessage = '';

  constructor(
    private orderService: OrderService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    // 1. Identify User Role (Admin vs Customer)
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser?.id) {
      this.isAdmin = this.currentUser.role === 'ADMIN';
      this.loadOrders();
    }
  }

  /**
   * Fetches orders based on role.
   * Admin -> All Orders.
   * Customer -> Only their orders.
   * Logic is enforced by Backend Security (OrderController).
   */
  private loadOrders(): void {
    if (!this.currentUser?.id) return;

    this.isLoading = true;
    this.errorMessage = '';

    const request$ = this.isAdmin
      ? this.orderService.getAllOrders()
      : this.orderService.getOrdersForUser(this.currentUser.id);

    request$.subscribe({
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
    if (!this.isAdmin) this.selectedOrder = order;
  }

  /**
   * Requests order cancellation.
   * Backend (OrderServiceImpl) enforces rules:
   * - Customer can only cancel 'Placed' orders.
   * - Stock is automatically restored.
   */
  cancelOrder(order: Order): void {
    if (!order.id || !confirm('Cancel this order?')) return;

    this.orderService.cancelOrder(order.id).subscribe({
      next: (updated) => {
        this.updateLocalOrder(updated);
        alert('Order cancelled.');
      },
      error: (err) => alert(err.error?.message || 'Failed to cancel order')
    });
  }

  // --- Admin Actions ---

  viewAdminOrderDetails(order: Order): void {
    this.selectedAdminOrder = order;
  }

  closeAdminOrderDetails(): void {
    this.selectedAdminOrder = null;
  }

  /**
   * Updates status (e.g., Placed -> Confirmed).
   * Backend validates allowed transitions.
   */
  isAdminOrderCancellable(order: Order): boolean {
    return order.status === 'Confirmed' || order.status === 'Packed';
  }

  onAdminStatusChange(order: Order, newStatus: OrderStatus): void {
    if (!order.id) return;

    // Optimistic update or wait for result? 
    // Here we wait to ensure backend allows it.
    const originalStatus = order.status;
    order.status = newStatus; // UI optimism

    this.orderService.updateOrder(order).subscribe({
      next: (updated) => this.updateLocalOrder(updated),
      error: (err) => {
        order.status = originalStatus; // Revert on failure
        alert(err.error?.message || 'Update failed');
      }
    });
  }

  /**
   * Updates logistics (Tracking ID, Carrier).
   */
  onAdminLogisticsChange(order: Order): void {
    if (!order.id) return;
    this.orderService.updateLogistics(order.id, order.logistics).subscribe({
      next: (updated) => {
        this.updateLocalOrder(updated);
        alert('Logistics updated.');
      },
      error: () => alert('Failed to update logistics')
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

  getOrderId(order: Order): string {
    return order.id ? `ORD${order.id.toString().padStart(3, '0')}` : 'ORD000';
  }
}
