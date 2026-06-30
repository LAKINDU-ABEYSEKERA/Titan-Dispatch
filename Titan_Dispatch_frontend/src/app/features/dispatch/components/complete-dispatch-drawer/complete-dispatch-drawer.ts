import { Component, inject, signal, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DispatchService } from '../../../../core/services/dispatch';

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

  completionForm = this.fb.nonNullable.group({
    endEngineHours: [0, [Validators.required, Validators.min(0)]],
    completionNotes: ['']
  });

  // Called by the parent component, passing the specific row ID
  open(dispatchId: string) {
    this.activeDispatchId.set(dispatchId);
    this.completionForm.reset();
    this.errorMessage.set(null);
    this.isOpen.set(true);
  }

  close() {
    this.isOpen.set(false);
    this.activeDispatchId.set(null);
  }

  submit() {
    if (this.completionForm.invalid || !this.activeDispatchId()) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const payload = this.completionForm.getRawValue();

    this.dispatchService.completeDispatch(this.activeDispatchId()!, payload).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.dispatchCompleted.emit(); // Tell parent to refresh grid
        this.close();
      },
      error: (err: Error) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.message); // Display Spring Boot's validation error
      }
    });
  }
}