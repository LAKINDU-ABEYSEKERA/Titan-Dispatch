import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe, CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { EquipmentService } from '../../core/services/equipment.service';
import { ProblemDetail } from '../../core/models/domain';

@Component({
  selector: 'app-equipment',
  standalone: true,
  // Added DecimalPipe, CurrencyPipe, and DatePipe to satisfy template compiler demands
  imports: [DecimalPipe, CurrencyPipe, DatePipe],
  templateUrl: './equipment.html',
  styleUrl: './equipment.scss'
})
export class Equipment implements OnInit {
  private readonly equipmentService = inject(EquipmentService);

  // Directly track the service's computed data projection signal
  readonly inventory = this.equipmentService.equipment;

  // Visual state controls
  readonly isLoading = signal<boolean>(false);
  readonly toastMessage = signal<{ detail: string; status: number } | null>(null);

  ngOnInit(): void {
    this.loadInventory();
  }

  loadInventory(): void {
    this.isLoading.set(true);
    this.toastMessage.set(null);

    this.equipmentService.getInventory().subscribe({
      next: () => {
        this.isLoading.set(false);
      },
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
      this.toastMessage.set({ 
        detail: 'Failed to synchronize heavy machinery logistics matrix.', 
        status: err.status || 500 
      });
    }
    
    setTimeout(() => this.toastMessage.set(null), 6000);
  }

  dismissToast(): void {
    this.toastMessage.set(null);
  }
}