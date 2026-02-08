import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product, CustomOptionGroup } from '../models/product';
import { Observable, map } from 'rxjs';
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

  getProductById(productId: string): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/products/${productId}`).pipe(
      map(p => this.normalizeProduct(p))
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



  private normalizeProduct(p: Product): Product {
    return {
      ...p,
      productId: p.productId || (p as any).id,
      customOptions: p.customOptions?.length ? p.customOptions : DEFAULT_CUSTOM_OPTIONS
    };
  }
}
