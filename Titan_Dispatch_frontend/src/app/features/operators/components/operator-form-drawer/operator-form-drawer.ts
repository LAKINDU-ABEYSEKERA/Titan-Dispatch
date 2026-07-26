import { Component, inject, signal, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { OperatorService, OperatorResponse, CreateOperatorCommand, UpdateOperatorCommand } from '../../../../core/services/operator.service';
import { ProblemDetail } from '../../../../core/models/domain';

@Component({
  selector: 'app-operator-form-drawer',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './operator-form-drawer.html'
})
export class OperatorFormDrawer {
  private readonly fb = inject(FormBuilder);
  private readonly operatorService = inject(OperatorService);

  saved = output<void>();

  isOpen = signal(false);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  activeOperatorId = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    licenseExpiration: ['', [Validators.required]],
    hourlyRate: [0.00, [Validators.required, Validators.min(0)]] // <-- ADDED
  });

  open(operator?: OperatorResponse) {
    this.errorMessage.set(null);
    if (operator) {
      this.activeOperatorId.set(operator.id);
      this.form.patchValue({
        firstName: operator.firstName,
        lastName: operator.lastName,
        licenseExpiration: operator.licenseExpiration,
        hourlyRate: operator.hourlyRate // <-- ADDED
      });
    } else {
      this.activeOperatorId.set(null);
      this.form.reset({ hourlyRate: 0.00 }); // <-- ADDED DEFAULT
    }
    this.isOpen.set(true);
  }

  close() {
    this.isOpen.set(false);
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const payload = this.form.getRawValue();
    const id = this.activeOperatorId();

    const request$ = id 
      ? this.operatorService.updateOperator(id, payload as UpdateOperatorCommand)
      : this.operatorService.createOperator(payload as CreateOperatorCommand);

    request$.subscribe({
      next: () => {
        this.isLoading.set(false);
        this.saved.emit();
        this.close();
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        if (err.error && typeof err.error === 'object' && 'detail' in err.error) {
          const problem = err.error as ProblemDetail;
          this.errorMessage.set(problem.detail);
        } else {
          this.errorMessage.set('Failed to save operator profile.');
        }
      }
    });
  }
}