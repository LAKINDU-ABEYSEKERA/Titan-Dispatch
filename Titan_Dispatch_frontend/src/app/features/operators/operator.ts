import { Component, inject, OnInit, signal, viewChild, computed } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { OperatorService, OperatorResponse } from '../../core/services/operator.service';
import { OperatorFormDrawer } from '../operators/components/operator-form-drawer/operator-form-drawer';

@Component({
  selector: 'app-operator-list',
  standalone: true,
  imports: [DatePipe, OperatorFormDrawer],
  templateUrl: './operator.html',
  styles: [`
    @keyframes slideFadeIn {
      0% { opacity: 0; transform: translateX(-20px); }
      100% { opacity: 1; transform: translateX(0); }
    }
    @keyframes shimmer {
      100% { transform: translateX(100%); }
    }
    .animate-row {
      animation: slideFadeIn 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards;
      opacity: 0; /* Starts hidden, animation reveals it */
    }
  `]
})
export class Operator implements OnInit {
  private readonly operatorService = inject(OperatorService);

  readonly operators = this.operatorService.operators;
  readonly isLoading = signal<boolean>(false);
  
  readonly formDrawer = viewChild.required(OperatorFormDrawer);

  // NEW: Search Engine
  readonly searchQuery = signal<string>('');

  readonly filteredOperators = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    if (!query) return this.operators();
    return this.operators().filter(op => 
      op.firstName.toLowerCase().includes(query) ||
      op.lastName.toLowerCase().includes(query) ||
      op.id.toLowerCase().includes(query)
    );
  });

  ngOnInit() {
    this.fetchOperators();
  }

  updateSearch(event: Event): void {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  fetchOperators() {
    this.isLoading.set(true);
    this.operatorService.getOperators().subscribe({
      next: () => this.isLoading.set(false),
      error: () => {
        this.isLoading.set(false);
        console.error('Failed to fetch operators');
      }
    });
  }

  openCreate() {
    this.formDrawer().open();
  }

  openEdit(operator: OperatorResponse) {
    this.formDrawer().open(operator);
  }

  deleteOperator(id: string) {
    if (!confirm('Are you sure you want to revoke this operator profile?')) return;
    
    this.isLoading.set(true);
    this.operatorService.deleteOperator(id).subscribe({
      next: () => this.fetchOperators(),
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        alert(err.error?.detail || 'Failed to delete operator.');
      }
    });
  }

  isLicenseExpired(dateStr: string): boolean {
    return new Date(dateStr) < new Date();
  }
}