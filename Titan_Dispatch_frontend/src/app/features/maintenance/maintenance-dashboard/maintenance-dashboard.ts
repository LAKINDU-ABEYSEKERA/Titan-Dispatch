import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe, CurrencyPipe } from '@angular/common';
import { MaintenanceService } from '../../../core/services/maintenance.service';
import { MaintenanceLogResponse } from '../../../core/models/domain';

@Component({
  selector: 'app-maintenance-dashboard',
  standalone: true,
  imports: [DatePipe, CurrencyPipe],
  templateUrl: 'maintenance-dashboard.html'
})
export class MaintenanceDashboard implements OnInit {
  private readonly maintenanceService = inject(MaintenanceService);
  
  readonly logs = signal<MaintenanceLogResponse[]>([]);
  readonly isLoading = signal<boolean>(false);

  ngOnInit(): void {
    this.fetchLogs();
  }

  fetchLogs(): void {
    this.isLoading.set(true);
    this.maintenanceService.getLogs().subscribe({
      next: (data) => {
        this.logs.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }
}