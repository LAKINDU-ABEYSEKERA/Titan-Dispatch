import { Component, inject, OnInit, signal, viewChild, computed } from '@angular/core';
import { DecimalPipe, CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { EquipmentService } from '../../core/services/equipment.service';
import { ProblemDetail } from '../../core/models/domain';
import { EquipmentFormDrawer } from './components/equipment-form-drawer/equipment-form-drawer';

@Component({
  selector: 'app-equipment',
  standalone: true,
  imports: [DecimalPipe, CurrencyPipe, DatePipe, EquipmentFormDrawer],
  templateUrl: './equipment.html',
  styleUrl: './equipment.scss'
})
export class Equipment implements OnInit {
  private readonly equipmentService = inject(EquipmentService);

  readonly inventory = this.equipmentService.equipment;

  readonly isLoading = signal<boolean>(false);
  readonly toastMessage = signal<{ detail: string; status: number } | null>(null);
  readonly drawer = viewChild.required(EquipmentFormDrawer);

  // NEW: Search Engine
  readonly searchQuery = signal<string>('');
  readonly filteredInventory = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    if (!query) return this.inventory();
    return this.inventory().filter(asset => 
      asset.assetTag.toLowerCase().includes(query) ||
      asset.status.toLowerCase().includes(query)
    );
  });

  ngOnInit(): void {
    this.loadInventory();
  }

  updateSearch(event: Event): void {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  loadInventory(): void {
    this.isLoading.set(true);
    this.toastMessage.set(null);
    this.equipmentService.getInventory().subscribe({
      next: () => this.isLoading.set(false),
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.handleError(err);
      }
    });
  }

  deleteAsset(id: string): void {
    if (confirm('CRITICAL: Are you sure you want to soft-delete this asset? It will be removed from dispatch availability.')) {
      this.isLoading.set(true);
      this.equipmentService.deleteEquipment(id).subscribe({
        next: () => {
          this.toastMessage.set({ detail: 'Asset successfully archived.', status: 200 });
          this.loadInventory();
        },
        error: (err: HttpErrorResponse) => {
          this.isLoading.set(false);
          this.handleError(err);
        }
      });
    }
  }

  private handleError(err: HttpErrorResponse): void {
    if (err.error && typeof err.error === 'object' && 'detail' in err.error) {
      const problem = err.error as ProblemDetail;
      this.toastMessage.set({ detail: problem.detail, status: problem.status });
    } else {
      this.toastMessage.set({ detail: 'Failed to synchronize heavy machinery logistics matrix.', status: err.status || 500 });
    }
    setTimeout(() => this.toastMessage.set(null), 6000);
  }

  dismissToast(): void {
    this.toastMessage.set(null);
  }
}