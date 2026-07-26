import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

export interface OperatorResponse {
  id: string;
  firstName: string;
  lastName: string;
  licenseExpiration: string;
  hourlyRate: number; // <-- ADDED
}

export interface CreateOperatorCommand {
  firstName: string;
  lastName: string;
  licenseExpiration: string;
  hourlyRate: number; // <-- ADDED
}

export interface UpdateOperatorCommand {
  firstName: string;
  lastName: string;
  licenseExpiration: string;
  hourlyRate: number; // <-- ADDED
}

@Injectable({ providedIn: 'root' })
export class OperatorService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/operators';

  readonly operators = signal<OperatorResponse[]>([]);

  getOperators(): Observable<OperatorResponse[]> {
    return this.http.get<OperatorResponse[]>(this.apiUrl).pipe(
      tap(data => this.operators.set(data))
    );
  }

  createOperator(cmd: CreateOperatorCommand): Observable<OperatorResponse> {
    return this.http.post<OperatorResponse>(this.apiUrl, cmd);
  }

  updateOperator(id: string, cmd: UpdateOperatorCommand): Observable<OperatorResponse> {
    return this.http.put<OperatorResponse>(`${this.apiUrl}/${id}`, cmd);
  }

  deleteOperator(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}