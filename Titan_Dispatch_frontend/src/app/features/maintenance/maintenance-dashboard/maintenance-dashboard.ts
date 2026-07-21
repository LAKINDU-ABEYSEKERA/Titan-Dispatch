import { Component, inject, OnInit, signal, viewChild } from '@angular/core';
import { DatePipe, CurrencyPipe, CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { MaintenanceService } from '../../../core/services/maintenance.service';
import { MaintenanceLogResponse, ActiveMaintenanceResponse } from '../../../core/models/domain';
import { MaintenanceFormDrawer } from '../components/maintenance-form-drawer/maintenance-form-drawer';
import { SendToShopDrawer } from '../components/send-to-shop-drawer/send-to-shop-drawer';

@Component({
  selector: 'app-maintenance-dashboard',
  standalone: true,
  imports: [CommonModule, DatePipe, CurrencyPipe, MaintenanceFormDrawer, SendToShopDrawer],
  templateUrl: './maintenance-dashboard.html'
})
export class MaintenanceDashboard implements OnInit {
  private readonly maintenanceService = inject(MaintenanceService);
  
  readonly logs = signal<MaintenanceLogResponse[]>([]);
  readonly activeRoster = signal<ActiveMaintenanceResponse[]>([]);
  readonly isLoading = signal<boolean>(true);

  readonly drawer = viewChild.required(MaintenanceFormDrawer);
  readonly shopDrawer = viewChild.required(SendToShopDrawer);

  ngOnInit(): void {
    this.fetchDashboardData();
  }

  fetchDashboardData(): void {
    this.isLoading.set(true);
    
    forkJoin({
      active: this.maintenanceService.getActiveRoster(),
      history: this.maintenanceService.getLogs()
    }).subscribe({
      next: (result) => {
        this.activeRoster.set(result.active);
        this.logs.set(result.history);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  getBadgeClass(type: string): string {
    switch (type.toUpperCase()) {
      case 'PREVENTATIVE': return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20';
      case 'REPAIR': return 'bg-amber-500/10 text-amber-400 border-amber-500/20';
      case 'EMERGENCY': return 'bg-red-500/10 text-red-400 border-red-500/20';
      default: return 'bg-blue-500/10 text-blue-400 border-blue-500/20';
    }
  }

  // NEW: Transforms the raw enum into a past-tense UI label to prevent dispatcher confusion
  formatServiceBadge(type: string): string {
    switch (type.toUpperCase()) {
      case 'PREVENTATIVE': return 'Maint. Completed';
      case 'REPAIR': return 'Repair Resolved';
      case 'INSPECTION': return 'Inspection Passed';
      case 'EMERGENCY': return 'Emergency Fixed';
      default: return type;
    }
  }
}