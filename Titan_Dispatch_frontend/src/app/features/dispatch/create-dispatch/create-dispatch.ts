import { Component, EventEmitter, inject, Input, Output, signal, effect } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { DispatchFormService } from '../../../core/services/dispatch-form';
import { ProblemDetail } from '../../../core/models/domain';

@Component({
  selector: 'app-create-dispatch',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './create-dispatch.html'
})
export class CreateDispatch {
  private readonly fb = inject(FormBuilder);
  private readonly dispatchFormService = inject(DispatchFormService);

  // Component API: Controlled by parent (DispatchList)
  @Input() set open(value: boolean) {
    this.isOpen.set(value);
    if (value) this.initializeForm();
  }
  @Output() closed = new EventEmitter<void>();
  @Output() success = new EventEmitter<void>();

  // Internal State
  readonly isOpen = signal<boolean>(false);
  readonly isHydrating = signal<boolean>(false);
  readonly isSubmitting = signal<boolean>(false);
  readonly toastMessage = signal<{ detail: string; status: number } | null>(null);

  // Data Lookups (Bound directly to the service)
  readonly equipmentOpts = this.dispatchFormService.equipment;
  readonly operatorOpts = this.dispatchFormService.operators;
  readonly jobSiteOpts = this.dispatchFormService.jobSites;

  // The Form
  readonly form = this.fb.nonNullable.group({
    equipmentId: ['', [Validators.required]],
    operatorId: ['', [Validators.required]],
    jobSiteId: ['', [Validators.required]],
    startDate: ['', [Validators.required]],
    requiresHeavyTransport: [false]
  });

  initializeForm(): void {
    this.form.reset();
    this.toastMessage.set(null);
    this.isHydrating.set(true);

    this.dispatchFormService.hydrateForm().subscribe({
      next: () => this.isHydrating.set(false),
      error: () => {
        this.isHydrating.set(false);
        this.toastMessage.set({ detail: 'Failed to load system lookup data.', status: 500 });
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.toastMessage.set(null);

    // Form value maps directly to CreateDispatchCommand
    this.dispatchFormService.allocate(this.form.getRawValue()).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.success.emit(); // Tell parent to refresh the grid
        this.closeDrawer();
      },
      error: (err: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        this.handleError(err);
      }
    });
  }

  closeDrawer(): void {
    this.isOpen.set(false);
    this.closed.emit();
  }

  private handleError(err: HttpErrorResponse): void {
    if (err.error && typeof err.error === 'object' && 'detail' in err.error) {
      const problem = err.error as ProblemDetail;
      this.toastMessage.set({ detail: problem.detail, status: problem.status });
    } else {
      this.toastMessage.set({ detail: 'Safety Interlock Engaged. Critical failure.', status: err.status || 500 });
    }
  }

  dismissToast(): void {
    this.toastMessage.set(null);
  }
}