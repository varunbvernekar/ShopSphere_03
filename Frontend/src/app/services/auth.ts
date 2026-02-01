import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUser: User | null = null;
  private readonly tokenKey = 'auth_token';
  private readonly apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUser = JSON.parse(storedUser);
    }
  }

  register(user: User): Observable<boolean> {
    return this.http.post<any>(`${this.apiUrl}/register`, user).pipe(
      map(response => !!response)
    );
  }

  login(email: string, password: string): Observable<boolean> {
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password }).pipe(
      map(response => {
        if (response && response.token) {
          this.setSession(response.user, response.token);
          return true;
        }
        return false;
      })
    );
  }

  logout(): void {
    this.currentUser = null;
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('currentUser');
  }

  getCurrentUser(): User | null {
    return this.currentUser;
  }

  isLoggedIn(): boolean {
    return this.currentUser != null;
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  updateCurrentUser(user: User): void {
    this.currentUser = user;
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  private setSession(user: User, token: string): void {
    this.currentUser = user;
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem('currentUser', JSON.stringify(user));
  }
}
