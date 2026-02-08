// src/app/components/admin/admin-dashboard/admin-dashboard.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DashboardService, DashboardStats } from '../../../services/dashboard';
import { OrderService } from '../../../services/order';
import { Order } from '../../../models/order';

interface RepeatedProductReportItem {
  name: string;
  image: string;
  timesOrdered: number;
  totalQuantity: number;
  totalRevenue: number;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})
export class AdminDashboard implements OnInit {
  // Stats
  activeProductsCount = 0;
  totalOrders = 0;
  totalRevenue = 0;
  avgOrderValue = 0;


  // Reports
  orders: Order[] = [];
  filteredOrders: Order[] = [];
  repeatedProductsReport: any[] = [];

  showReports = true;
  reportStartDate = '';
  reportEndDate = '';

  constructor(
    private dashboardService: DashboardService,
    private orderService: OrderService
  ) { }

  ngOnInit(): void {
    this.loadStats();
    this.loadOrders();
    this.loadTopSellingProducts();
  }

  loadStats(): void {
    this.dashboardService.getStats().subscribe({
      next: (stats: DashboardStats) => {
        this.totalOrders = stats.totalOrders;
        this.totalRevenue = stats.totalRevenue;
        this.activeProductsCount = stats.activeProducts;

        this.avgOrderValue = this.totalOrders > 0 ? this.totalRevenue / this.totalOrders : 0;
      },
      error: err => console.error('Failed to load dashboard stats', err)
    });
  }

  loadTopSellingProducts(): void {
    this.dashboardService.getTopSellingProducts(100).subscribe({
      next: (data) => {
        this.repeatedProductsReport = data.filter((item: any) => item.totalQuantity > 5);
      },
      error: (err) => console.error('Failed to load top selling products', err)
    });
  }

  loadOrders(): void {
    this.orderService.getAllOrders().subscribe({
      next: orders => {
        this.orders = orders;
        this.filteredOrders = orders;
      },
      error: err => console.error('Failed to load orders', err)
    });
  }


  filterReports(): void {
    if (!this.reportStartDate || !this.reportEndDate) {
      alert('Please select both start and end dates');
      return;
    }

    const start = new Date(this.reportStartDate);
    const end = new Date(this.reportEndDate);
    end.setHours(23, 59, 59, 999);

    this.filteredOrders = this.orders.filter(order => {
      const orderDate = this.parseDate(order.placedOn);
      return orderDate >= start && orderDate <= end;
    });
  }

  private parseDate(dateStr: string): Date {
    const parts = dateStr.split('/');
    if (parts.length === 3) {
      return new Date(parseInt(parts[2]), parseInt(parts[1]) - 1, parseInt(parts[0]));
    }
    return new Date(dateStr);
  }

  getReportRevenue(): number {
    return this.filteredOrders.reduce((sum, o) => sum + o.amount, 0);
  }

  getReportOrderCount(): number {
    return this.filteredOrders.length;
  }
}
