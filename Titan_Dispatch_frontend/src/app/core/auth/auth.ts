import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthRequest, AuthResponse } from '../models/domain'; // Adjust path as needed

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  
  // The Access Token lives purely in memory
  readonly accessToken = signal<string | null>(null);

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/v1/auth/login', credentials).pipe(
      tap(response => this.accessToken.set(response.access_token))
    );
  }

  refresh(): Observable<AuthResponse> {
    // No body needed; the HttpOnly cookie handles the payload
    return this.http.post<AuthResponse>('/api/v1/auth/refresh', {}).pipe(
      tap(response => this.accessToken.set(response.access_token))
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>('/api/v1/auth/logout', {}).pipe(
      tap(() => this.accessToken.set(null))
    );
  }
}