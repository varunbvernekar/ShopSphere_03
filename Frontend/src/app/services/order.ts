import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap } from 'rxjs';
import { Order } from '../models/order';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getAllOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/orders`);
  }

  getOrdersForUser(userId: number): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/orders?userId=${userId}`);
  }

  createOrder(order: Omit<Order, 'id'>): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/orders`, order);
  }

  updateOrder(order: Order): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/orders/${order.id}`, order);
  }

  cancelOrder(orderId: number): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/orders/${orderId}/status`, { status: 'Cancelled' });
  }
}
