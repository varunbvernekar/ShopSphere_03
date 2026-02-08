
import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { InventoryItem } from '../../../models/inventory-item';
import { InventoryService } from '../../../services/inventory';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-low-stock-alerts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './low-stock-alerts.html',
  styleUrls: ['./low-stock-alerts.css']
})
export class LowStockAlerts implements OnInit, OnDestroy, OnChanges {
  @Input() isOpen: boolean = false;
  @Output() closeEvent = new EventEmitter<void>();

  lowStockProducts: InventoryItem[] = [];
  private subscription?: Subscription;

  constructor(
    private inventoryService: InventoryService,
    private router: Router
  ) { }

  ngOnInit(): void {
    if (this.isOpen) {
      this.loadLowStockProducts();
    }
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.loadLowStockProducts();
    }
  }

  loadLowStockProducts(): void {
    this.subscription = this.inventoryService.getAllInventory().subscribe({
      next: items => {
        this.lowStockProducts = items.filter(
          item =>
            typeof item.quantity === 'number' &&
            typeof item.reorderThreshold === 'number' &&
            item.quantity <= item.reorderThreshold
        );
      },
      error: err => {
        console.error('Failed to load low stock products', err);
      }
    });
  }

  close(): void {
    this.closeEvent.emit();
  }

  goToInventory(item: InventoryItem): void {
    this.close();
    this.router.navigate(['/admin/inventory']);
  }
}

