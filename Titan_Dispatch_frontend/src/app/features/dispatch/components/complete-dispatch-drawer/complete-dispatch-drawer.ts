import { Component, inject, signal, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { DispatchService, CompleteDispatchCommand } from '../../../../core/services/dispatch';

@Component({
  selector: 'app-complete-dispatch-drawer',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './complete-dispatch-drawer.html'
})
export class CompleteDispatchDrawer {
  private readonly fb = inject(FormBuilder);
  private readonly dispatchService = inject(DispatchService);

  // Angular 19 Output to notify parent dashboard to refresh
  dispatchCompleted = output<void>();

  isOpen = signal(false);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  
  // Local state for the targeted dispatch
  activeDispatchId = signal<string | null>(null);

  // Strict mapping to match the Java DTO (endHours only)
  completionForm = this.fb.nonNullable.group({
    endHours: [0, [Validators.required, Validators.min(0.1)]]
  });

  // Called by the parent component, passing the specific row ID
  open(dispatchId: string) {
    this.activeDispatchId.set(dispatchId);
    this.completionForm.reset({ endHours: 0 });
    this.errorMessage.set(null);
    this.isOpen.set(true);
  }

  close() {
    this.isOpen.set(false);
    this.activeDispatchId.set(null);
  }

  submit() {
    if (this.completionForm.invalid || !this.activeDispatchId()) {
      this.completionForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Cast explicitly to ensure type safety matching the interface
    const payload: CompleteDispatchCommand = this.completionForm.getRawValue();

    this.dispatchService.completeDispatch(this.activeDispatchId()!, payload).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.dispatchCompleted.emit(); // Tell parent to refresh grid
        this.close();
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        // Display Spring Boot's validation error or a fallback message
        this.errorMessage.set(err.error?.detail || 'Failed to complete dispatch. Please check system logs.');
      }
    });
  }
}