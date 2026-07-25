import { Component, inject, OnInit, signal, viewChild, computed } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { DispatchService } from '../../../core/services/dispatch';
import { ProblemDetail } from '../../../core/models/domain';
import { CreateDispatch } from '../create-dispatch/create-dispatch';
import { CompleteDispatchDrawer } from '../components/complete-dispatch-drawer/complete-dispatch-drawer';

@Component({
  selector: 'app-dispatch-list',
  standalone: true,
  imports: [DatePipe, CreateDispatch, CompleteDispatchDrawer],
  templateUrl: './dispatch-list.html',
  styleUrl: './dispatch-list.scss'
})
export class DispatchList implements OnInit {
  private readonly dispatchService = inject(DispatchService);

  readonly dispatches = this.dispatchService.dispatches;
  readonly stats = this.dispatchService.stats;

  readonly isLoading = signal<boolean>(false);
  readonly toastMessage = signal<{ detail: string; status: number } | null>(null);
  
  readonly isDrawerOpen = signal<boolean>(false);
  readonly completeDrawer = viewChild.required(CompleteDispatchDrawer);

  // NEW: Search Signal
  readonly searchQuery = signal<string>('');

  readonly atRiskCount = computed(() => {
    return this.dispatches().filter(d => d.status === 'AT_RISK').length;
  });

  // UPGRADED: The Intelligent Sorting & Filtering Engine
  readonly sortedDispatches = computed(() => {
    let list = [...this.dispatches()];
    const query = this.searchQuery().toLowerCase().trim();
    const now = Date.now();

    // 1. Apply Search Filter first (Blazing fast client-side search)
    if (query) {
      list = list.filter(d => 
        d.equipmentTag.toLowerCase().includes(query) ||
        d.projectCode.toLowerCase().includes(query) ||
        d.operatorName.toLowerCase().includes(query) ||
        d.id.toLowerCase().includes(query)
      );
    }

    // 2. Apply Sorting Logic
    return list.sort((a, b) => {
      if (a.status === 'AT_RISK' && b.status !== 'AT_RISK') return -1;
      if (b.status === 'AT_RISK' && a.status !== 'AT_RISK') return 1;

      const isAInactive = a.status === 'COMPLETED' || a.status === 'CANCELLED';
      const isBInactive = b.status === 'COMPLETED' || b.status === 'CANCELLED';
      if (isAInactive && !isBInactive) return 1;
      if (!isAInactive && isBInactive) return -1;

      const diffA = Math.abs(new Date(a.startDate).getTime() - now);
      const diffB = Math.abs(new Date(b.startDate).getTime() - now);
      return diffA - diffB;
    });
  });

  ngOnInit(): void {
    this.fetchDispatches();
  }

  // NEW: Helper to update the search signal on keystroke
  updateSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
  }

  isOverdue(dateString: string): boolean {
    return new Date(dateString).getTime() < Date.now();
  }

  openCompleteDrawer(dispatchId: string, startEngineHours: number): void {
    this.completeDrawer().open(dispatchId, startEngineHours);
  }

  activateJob(dispatchId: string): void {
    if (!confirm('Are you sure you want to engage this asset?')) return;
    
    this.isLoading.set(true);
    this.dispatchService.activateDispatch(dispatchId).subscribe({
      next: () => this.fetchDispatches(),
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.handleError(err);
      }
    });
  }

  cancelJob(dispatchId: string): void {
    if (!confirm('WARNING: Are you sure you want to cancel this dispatch? This will immediately release the equipment back into the available pool.')) return;
    
    this.isLoading.set(true);
    this.dispatchService.cancelDispatch(dispatchId).subscribe({
      next: () => this.fetchDispatches(),
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.handleError(err);
      }
    });
  }

  fetchDispatches(): void {
    this.isLoading.set(true);
    this.toastMessage.set(null);

    this.dispatchService.getDispatches().subscribe({
      next: () => this.isLoading.set(false),
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.handleError(err);
      }
    });
  }

  private handleError(err: HttpErrorResponse): void {
    if (err.error && typeof err.error === 'object' && 'detail' in err.error) {
      const problem = err.error as ProblemDetail;
      this.toastMessage.set({ detail: problem.detail, status: problem.status });
    } else {
      this.toastMessage.set({ detail: 'Failed to communicate with Titan core.', status: err.status || 500 });
    }
    setTimeout(() => this.toastMessage.set(null), 6000);
  }

  dismissToast(): void {
    this.toastMessage.set(null);
  }
}