import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { DispatchService } from '../../../core/services/dispatch';
import { ProblemDetail } from '../../../core/models/domain';
// 1. Import the CreateDispatch component
import { CreateDispatch } from '../create-dispatch/create-dispatch';

@Component({
  selector: 'app-dispatch-list',
  standalone: true,
  // 2. Add CreateDispatch to the imports array
  imports: [DatePipe, CreateDispatch],
  templateUrl: './dispatch-list.html',
  styleUrl: './dispatch-list.scss'
})
export class DispatchList implements OnInit {
  private readonly dispatchService = inject(DispatchService);

  readonly dispatches = this.dispatchService.dispatches;
  readonly stats = this.dispatchService.stats;

  readonly isLoading = signal<boolean>(false);
  readonly toastMessage = signal<{ detail: string; status: number } | null>(null);
  
  // 3. Create a Signal to manage the drawer state
  readonly isDrawerOpen = signal<boolean>(false);

  ngOnInit(): void {
    this.fetchDispatches();
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
      this.toastMessage.set({ detail: 'Failed to retrieve dispatch log from Titan core.', status: err.status || 500 });
    }
    setTimeout(() => this.toastMessage.set(null), 6000);
  }

  dismissToast(): void {
    this.toastMessage.set(null);
  }
}