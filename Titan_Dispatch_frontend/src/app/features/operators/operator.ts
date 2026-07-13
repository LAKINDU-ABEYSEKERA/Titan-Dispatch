import { Component, inject, OnInit, signal, viewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { OperatorService, OperatorResponse } from '../../core/services/operator.service';
import { OperatorFormDrawer } from '../operators/components/operator-form-drawer/operator-form-drawer';

@Component({
  selector: 'app-operator-list',
  standalone: true,
  imports: [DatePipe, OperatorFormDrawer],
  templateUrl: './operator.html'
})
export class Operator implements OnInit {
  private readonly operatorService = inject(OperatorService);

  readonly operators = this.operatorService.operators;
  readonly isLoading = signal<boolean>(false);
  
  readonly formDrawer = viewChild.required(OperatorFormDrawer);

  ngOnInit() {
    this.fetchOperators();
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