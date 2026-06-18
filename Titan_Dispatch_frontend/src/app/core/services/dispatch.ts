import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { DispatchSummary } from '../models/domain';

@Injectable({
  providedIn: 'root'
})
export class DispatchService {
  private readonly http = inject(HttpClient);

  // Core state signal
  private readonly _dispatches = signal<DispatchSummary[]>([]);

  // Public projections
  readonly dispatches = computed(() => this._dispatches());
  
  // Computed statistics for the dashboard cards
  readonly stats = computed(() => {
    const list = this._dispatches();
    return {
      total: list.length,
      active: list.filter(d => d.status === 'ACTIVE').length,
      scheduled: list.filter(d => d.status === 'SCHEDULED').length,
      completed: list.filter(d => d.status === 'COMPLETED').length
    };
  });

  getDispatches(): Observable<DispatchSummary[]> {
    return this.http.get<DispatchSummary[]>('/api/v1/dispatch').pipe(
      tap(data => this._dispatches.set(data))
    );
  }
}