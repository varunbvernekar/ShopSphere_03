
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { InventoryService } from '../../../services/inventory';
import { Product } from '../../../models/product';


@Component({
  selector: 'app-admin-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-inventory.html',
  styleUrls: ['./admin-inventory.css']
})
export class AdminInventory implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  searchQuery = '';
  statusFilter: 'All' | 'In Stock' | 'Low Stock' = 'All';

  // Summary Metrics
  get totalProducts(): number {
    return this.products.length;
  }

  get lowStockCount(): number {
    return this.products.filter(p => this.isLowStock(p)).length;
  }

  // Inventory editing (Stock & Threshold)
  editingStockProduct: Product | null = null;
  editStockLevel = 0;
  editReorderThreshold = 0;


  constructor(
    private inventoryService: InventoryService
  ) { }

  ngOnInit(): void {
    this.refreshData();
  }

  refreshData(): void {
    this.inventoryService.getAllInventory().subscribe({
      next: (inventory) => {
        this.products = inventory.map(item => ({
          productId: item.productId,
          name: item.productName,
          stockLevel: item.quantity,
          reorderThreshold: item.reorderThreshold
        } as any));
        this.filterProducts();
      },
      error: err => {
        console.error('Failed to load inventory data', err);
      }
    });
  }
  // Search & Filter Logic
  onSearchChange(): void {
    this.filterProducts();
  }

  onFilterChange(status: 'All' | 'In Stock' | 'Low Stock'): void {
    this.statusFilter = status;
    this.filterProducts();
  }

  private filterProducts(): void {
    let temp = this.products;

    // 1. Filter by Status
    if (this.statusFilter === 'Low Stock') {
      temp = temp.filter(p => this.isLowStock(p));
    } else if (this.statusFilter === 'In Stock') {
      temp = temp.filter(p => !this.isLowStock(p));
    }

    // 2. Filter by Search Query
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      temp = temp.filter(p =>
        p.name.toLowerCase().includes(query) ||
        p.productId.toLowerCase().includes(query)
      );
    }

    this.filteredProducts = temp;
  }


  private isLowStock(product: Product): boolean {
    return (
      typeof product.stockLevel === 'number' &&
      typeof product.reorderThreshold === 'number' &&
      product.stockLevel <= product.reorderThreshold
    );
  }

  getStatusClass(product: Product): string {
    if (this.isLowStock(product)) {
      return 'status-pill low-stock';
    }
    return 'status-pill in-stock';
  }

  getStatusLabel(product: Product): string {
    if (this.isLowStock(product)) {
      return 'Low Stock';
    }
    return 'In Stock';
  }

  // --- Inventory (Stock) Management ---
  openStockEditor(product: Product): void {
    this.editingStockProduct = product;
    this.editStockLevel = product.stockLevel || 0;
    this.editReorderThreshold = product.reorderThreshold || 0;
  }

  closeStockEditor(): void {
    this.editingStockProduct = null;
  }

  saveStockChanges(): void {
    if (!this.editingStockProduct) return;

    this.inventoryService.updateInventory(
      this.editingStockProduct.productId,
      this.editStockLevel,
      this.editReorderThreshold
    ).subscribe({
      next: () => {
        alert('Inventory updated successfully!');
        this.refreshData();
        this.closeStockEditor();
      },
      error: (err: any) => {
        console.error('Failed to update inventory', err);
        alert('Failed to update inventory');
      }
    });
  }


}

