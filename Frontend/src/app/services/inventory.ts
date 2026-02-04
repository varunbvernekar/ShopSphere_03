
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { Product } from '../models/product';
import { ProductService } from './product';

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

  updateStock(productId: string, stockLevel: number): Observable<any> {
    const payload = { quantity: stockLevel };
    return this.http.put(`${this.inventoryApiUrl}/${productId}`, payload).pipe(
      tap(() => this.refreshLowStockCount())
    );
  }

  updateReorderThreshold(productId: string, reorderThreshold: number): Observable<any> {
    const payload = { threshold: reorderThreshold };
    return this.http.put(`${this.inventoryApiUrl}/${productId}`, payload).pipe(
      tap(() => this.refreshLowStockCount())
    );
  }

  updateInventory(productId: string, stockLevel: number, reorderThreshold: number): Observable<any> {
    const payload = { quantity: stockLevel, threshold: reorderThreshold };
    return this.http.put(`${this.inventoryApiUrl}/${productId}`, payload).pipe(
      tap(() => this.refreshLowStockCount())
    );
  }
}

export interface InventoryItem {
  productId: string;
  productName: string;
  productPrice: number;
  quantity: number;
  reorderThreshold: number;
}
