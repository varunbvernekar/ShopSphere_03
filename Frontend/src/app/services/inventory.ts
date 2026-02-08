import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { InventoryItem } from '../models/inventory-item';

@Injectable({
  providedIn: 'root'
})

export class InventoryService {
  private lowStockCountSubject = new BehaviorSubject<number>(0);
  public lowStockCount$ = this.lowStockCountSubject.asObservable();
  private readonly inventoryApiUrl = 'http://localhost:8080/api/inventory';

  constructor(private http: HttpClient) {
    this.refreshLowStockCount();
  }

  getAllInventory(): Observable<InventoryItem[]> {
    return this.http.get<InventoryItem[]>(this.inventoryApiUrl);
  }

  refreshLowStockCount(): void {
    this.getAllInventory().subscribe({
      next: items => {
        const count = items.filter(
          item =>
            typeof item.quantity === 'number' &&
            typeof item.reorderThreshold === 'number' &&
            item.quantity <= item.reorderThreshold
        ).length;
        this.lowStockCountSubject.next(count);
      }
    });
  }

  updateInventory(productId: string, stockLevel: number, reorderThreshold: number): Observable<any> {
    const payload = { quantity: stockLevel, threshold: reorderThreshold };
    return this.http.put(`${this.inventoryApiUrl}/${productId}`, payload).pipe(
      tap(() => this.refreshLowStockCount())
    );
  }
}


