
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

  constructor(
    private http: HttpClient,
    private productService: ProductService
  ) {
    this.refreshLowStockCount();
  }

  getProducts(): Observable<Product[]> {
    return this.productService.getProducts();
  }

  refreshLowStockCount(): void {
    this.getProducts().subscribe({
      next: products => {
        const count = products.filter(
          p =>
            typeof p.stockLevel === 'number' &&
            typeof p.reorderThreshold === 'number' &&
            p.stockLevel <= p.reorderThreshold
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
