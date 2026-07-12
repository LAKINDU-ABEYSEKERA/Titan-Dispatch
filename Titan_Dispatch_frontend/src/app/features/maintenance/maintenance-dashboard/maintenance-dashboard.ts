import { Component, inject, OnInit, signal, viewChild } from '@angular/core';
import { DatePipe, CurrencyPipe } from '@angular/common';
import { MaintenanceService } from '../../../core/services/maintenance.service';
import { MaintenanceLogResponse } from '../../../core/models/domain';
import { MaintenanceFormDrawer } from '../components/maintenance-form-drawer/maintenance-form-drawer';

@Component({
  selector: 'app-maintenance-dashboard',
  standalone: true,
  // FIX: Make sure the drawer component is imported here
  imports: [DatePipe, CurrencyPipe, MaintenanceFormDrawer],
  templateUrl: './maintenance-dashboard.html'
})
export class MaintenanceDashboard implements OnInit {
  private readonly maintenanceService = inject(MaintenanceService);
  
  readonly logs = signal<MaintenanceLogResponse[]>([]);
  readonly isLoading = signal<boolean>(false);

  // FIX: This exposes the drawer to the HTML template so drawer().open() works
  readonly drawer = viewChild.required(MaintenanceFormDrawer);

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