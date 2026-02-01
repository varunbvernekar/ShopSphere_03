import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardStats {
    totalOrders: number;
    totalRevenue: number;
    totalProducts: number;
    activeProducts: number;
}

export interface ProductSales {
    name: string;
    image: string;
    timesOrdered: number;
    totalQuantity: number;
    totalRevenue: number;
}

@Injectable({
    providedIn: 'root'
})
export class DashboardService {
    private readonly apiUrl = 'http://localhost:8080/api/dashboard';

    constructor(private http: HttpClient) { }

    getStats(): Observable<DashboardStats> {
        return this.http.get<DashboardStats>(`${this.apiUrl}/stats`);
    }

    getTopSellingProducts(limit: number = 5): Observable<ProductSales[]> {
        return this.http.get<ProductSales[]>(`${this.apiUrl}/products/top-selling?limit=${limit}`);
    }
}
