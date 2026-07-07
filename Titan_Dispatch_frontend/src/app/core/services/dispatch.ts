import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { DispatchSummary } from '../models/domain';

export interface CompleteDispatchCommand {
  endHours: number;
}

@Injectable({
  providedIn: 'root'
})
export class DispatchService {
  private readonly http = inject(HttpClient);

  private readonly _dispatches = signal<DispatchSummary[]>([]);

  readonly dispatches = computed(() => this._dispatches());
  
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

  activateDispatch(dispatchId: string): Observable<void> {
    return this.http.put<void>(`/api/v1/dispatch/${dispatchId}/activate`, {}).pipe(
      tap(() => console.log(`[DispatchService] Activated dispatch ${dispatchId}`))
    );
  }

  cancelDispatch(dispatchId: string): Observable<void> {
    return this.http.put<void>(`/api/v1/dispatch/${dispatchId}/cancel`, {}).pipe(
      tap(() => console.log(`[DispatchService] Cancelled dispatch ${dispatchId}`))
    );
  }

  completeDispatch(dispatchId: string, payload: CompleteDispatchCommand): Observable<void> {
    return this.http.put<void>(`/api/v1/dispatch/${dispatchId}/complete`, payload).pipe(
      tap(() => console.log(`[DispatchService] Successfully completed dispatch ${dispatchId}`))
    );
  }
}