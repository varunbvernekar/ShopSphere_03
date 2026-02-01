import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getUser(userId: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/${userId}`);
  }

  updateUser(user: User): Observable<User> {
    if (!user.id) {
      throw new Error('User id is required to update a user');
    }
    return this.http.put<User>(`${this.apiUrl}/users/${user.id}`, user);
  }
}

