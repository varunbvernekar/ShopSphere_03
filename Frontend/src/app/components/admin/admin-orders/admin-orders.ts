
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Order, OrderStatus } from '../../../models/order';
import { OrderService } from '../../../services/order';
import { DeliveryService } from '../../../services/delivery';

@Component({
    selector: 'app-admin-orders',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './admin-orders.html',
    styleUrls: ['./admin-orders.css']
})
export class AdminOrders implements OnInit {
    orders: Order[] = [];
    selectedAdminOrder: Order | null = null;
    isLoading = false;
    errorMessage = '';
    orderSteps: OrderStatus[] = ['Confirmed', 'Packed', 'Shipped', 'Delivered'];

    constructor(
        private orderService: OrderService,
        private deliveryService: DeliveryService
    ) { }

    ngOnInit(): void {
        this.loadOrders();
    }

    loadOrders(): void {
        this.isLoading = true;
        this.errorMessage = '';
        this.orderService.getAllOrders().subscribe({
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

    viewAdminOrderDetails(order: Order): void {
        this.selectedAdminOrder = order;
    }

    closeAdminOrderDetails(): void {
        this.selectedAdminOrder = null;
    }

    isAdminOrderCancellable(order: Order): boolean {
        return order.status === 'Placed' || order.status === 'Confirmed' || order.status === 'Packed';
    }

    onAdminStatusChange(order: Order, newStatus: OrderStatus): void {
        if (!order.id) return;
        const originalStatus = order.status;
        order.status = newStatus;

        this.orderService.updateOrderStatus(order.id, newStatus).subscribe({
            next: (updated) => this.updateLocalOrder(updated),
            error: (err) => {
                order.status = originalStatus;
                alert(err.error?.message || 'Update failed');
            }
        });
    }

    onAdminLogisticsChange(order: Order): void {
        if (!order.id) return;
        this.deliveryService.updateLogistics(order.id, order.logistics).subscribe({
            next: (updated) => {
                this.updateLocalOrder(updated);
                alert('Logistics updated.');
            },
            error: () => alert('Failed to update logistics')
        });
    }

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

    private updateLocalOrder(updated: Order): void {
        const index = this.orders.findIndex(u => u.id === updated.id);
        if (index !== -1) this.orders[index] = updated;
        if (this.selectedAdminOrder?.id === updated.id) this.selectedAdminOrder = updated;
    }
}
