import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateMaintenanceLogCommand, MaintenanceLogResponse } from '../models/domain';

@Injectable({
  providedIn: 'root'
})
export class MaintenanceService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/maintenance';

  getLogs(): Observable<MaintenanceLogResponse[]> {
    return this.http.get<MaintenanceLogResponse[]>(this.baseUrl);
  }

  submitLog(command: CreateMaintenanceLogCommand): Observable<void> {
    return this.http.post<void>(this.baseUrl, command);
  }
}