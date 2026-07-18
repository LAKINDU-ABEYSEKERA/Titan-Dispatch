import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  MaintenanceLogResponse, 
  ActiveMaintenanceResponse,
  CreateMaintenanceLogCommand 
} from '../models/domain';

@Injectable({
  providedIn: 'root'
})
export class MaintenanceService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/maintenance';

  getLogs(): Observable<MaintenanceLogResponse[]> {
    return this.http.get<MaintenanceLogResponse[]>(this.baseUrl);
  }

  getActiveRoster(): Observable<ActiveMaintenanceResponse[]> {
    return this.http.get<ActiveMaintenanceResponse[]>(`${this.baseUrl}/active`);
  }

  submitLog(command: CreateMaintenanceLogCommand): Observable<void> {
    return this.http.post<void>(this.baseUrl, command);
  }

  sendToShop(equipmentId: string, expectedEndDate: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${equipmentId}/shop`, { expectedEndDate });
  }
}