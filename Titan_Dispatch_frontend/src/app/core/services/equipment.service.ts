import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { EquipmentResponse } from '../models/domain';

@Injectable({
  providedIn: 'root'
})
export class EquipmentService {
  private readonly http = inject(HttpClient);

  // Private writeable state segment
  private readonly _equipment = signal<EquipmentResponse[]>([]);

  // Public read-only projection exposed to layout controllers
  readonly equipment = computed(() => this._equipment());

  getInventory(): Observable<EquipmentResponse[]> {
    return this.http.get<EquipmentResponse[]>('/api/v1/equipment').pipe(
      tap(data => this._equipment.set(data))
    );
  }

  createEquipment(payload: any): Observable<EquipmentResponse> {
    return this.http.post<EquipmentResponse>('/api/v1/equipment', payload);
  }

  updateEquipment(id: string, payload: any): Observable<EquipmentResponse> {
    return this.http.put<EquipmentResponse>(`/api/v1/equipment/${id}`, payload);
  }

  deleteEquipment(id: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/equipment/${id}`);
  }
}