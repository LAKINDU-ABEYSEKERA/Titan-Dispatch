import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { DispatchService } from '../../core/services/dispatch';
import { DispatchSummary } from '../../core/models/domain';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {
  private readonly dispatchService = inject(DispatchService);
  
  readonly upcomingJobs = signal<DispatchSummary[]>([]);
  readonly atRiskCount = signal<number>(0);
  readonly isLoading = signal<boolean>(true);

  ngOnInit(): void {
    this.fetchOverviewData();
  }

  fetchOverviewData(): void {
    this.isLoading.set(true);
    this.dispatchService.getDispatches().subscribe({
      next: (data) => {
        // 1. Filter out old history (we only care about the present/future)
        let activeJobs = data.filter(d => d.status !== 'COMPLETED' && d.status !== 'CANCELLED');
        
        // 2. Sort Chronologically: Nearest dates at the top!
        activeJobs.sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime());
        
        // 3. Count Watchdog Conflicts to trigger the global warning banner
        const conflicts = activeJobs.filter(d => d.status === 'AT_RISK').length;
        
        this.upcomingJobs.set(activeJobs);
        this.atRiskCount.set(conflicts);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }
}