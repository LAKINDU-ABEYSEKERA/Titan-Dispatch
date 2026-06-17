import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/auth/auth';
import { ProblemDetail } from '../../../core/models/domain';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  // Strictly typed non-nullable form control group
  readonly loginForm = this.fb.nonNullable.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  // Presentation Signals
  readonly isLoading = signal<boolean>(false);
  readonly toastMessage = signal<{ detail: string; status: number } | null>(null);

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.toastMessage.set(null); // Clear previous errors

    this.authService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.handleBusinessError(err);
      }
    });
  }

  private handleBusinessError(err: HttpErrorResponse): void {
    // Structural parse of the RFC 7807 ProblemDetail error object from the backend
    if (err.error && typeof err.error === 'object' && 'detail' in err.error) {
      const problem = err.error as ProblemDetail;
      this.toastMessage.set({ detail: problem.detail, status: problem.status });
    } else if (err.status === 401) {
      this.toastMessage.set({ detail: 'Invalid enterprise credentials.', status: 401 });
    } else {
      this.toastMessage.set({ detail: 'Critical network/system failure. Interlock engaged.', status: err.status || 500 });
    }

    // Dismiss notice automatically after 5 seconds
    setTimeout(() => this.toastMessage.set(null), 5000);
  }

  dismissToast(): void {
    this.toastMessage.set(null);
  }
}