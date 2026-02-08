import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  private readonly apiUrl = 'http://localhost:8080/api/delivery';

  constructor(private http: HttpClient) { }

  updateLogistics(orderId: number, logistics: any): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${orderId}/logistics`, logistics);
  }

  getTrackingUrl(carrier: string, trackingId: string): string {
    const c = carrier.toLowerCase();
    if (c.includes('shiprocket') || c.includes('delhivery')) return `https://www.shiprocket.in/tracking/${trackingId}`;
    if (c.includes('fedex')) return `https://www.fedex.com/apps/fedextrack/?tracknumbers=${trackingId}`;
    if (c.includes('ups')) return `https://www.ups.com/track?tracknum=${trackingId}`;
    if (c.includes('dhl')) return `https://www.dhl.com/en/express/tracking.html?AWB=${trackingId}`;
    return `https://www.17track.net/en/track?nums=${trackingId}`;
  }
}

