import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product, CustomOptionGroup } from '../models/product';
import { Observable, map, switchMap } from 'rxjs';
import { DEFAULT_CUSTOM_OPTIONS } from '../config/product.config';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products`).pipe(
      map(products => products.map(p => this.normalizeProduct(p)))
    );
  }

  addProduct(product: any, imageFile?: File): Observable<Product> {
    const formData = new FormData();
    formData.append('product', JSON.stringify(product));
    if (imageFile) formData.append('image', imageFile);

    return this.http.post<Product>(`${this.apiUrl}/products`, formData).pipe(
      map(p => this.normalizeProduct(p))
    );
  }

  getProductById(productId: string): Observable<Product | undefined> {
    return this.getProducts().pipe(
      map(products => products.find(p => p.productId === productId))
    );
  }

  updateProduct(product: Product): Observable<Product> {
    const payload = { ...product, id: product.productId };
    return this.http.put<Product>(`${this.apiUrl}/products/${product.productId}`, payload).pipe(
      map(p => this.normalizeProduct(p))
    );
  }

  deleteProduct(productId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/${productId}`);
  }

  updateStock(productId: string, stockLevel: number): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/products/${productId}/stock`, { stockLevel }).pipe(
      map(p => this.normalizeProduct(p))
    );
  }

  updateReorderThreshold(productId: string, reorderThreshold: number): Observable<Product> {
    // Assuming backend handles partial updates or we fetch-modify-save
    // For now keeping it simple as before but cleaner
    return this.getProductById(productId).pipe(
      switchMap(product => {
        if (!product) throw new Error('Product not found');
        return this.updateProduct({ ...product, reorderThreshold });
      })
    );
  }

  private normalizeProduct(p: Product): Product {
    return {
      ...p,
      productId: p.productId || (p as any).id,
      customOptions: p.customOptions?.length ? p.customOptions : DEFAULT_CUSTOM_OPTIONS
    };
  }
}
