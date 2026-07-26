import { Component, ChangeDetectionStrategy, EventEmitter, inject, Input, Output, signal, effect } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { JobSiteService } from '../../../../core/services/job-site.service';
import { JobSiteResponse } from '../../../../core/models/job-site';
import { ProblemDetail } from '../../../../core/models/domain';

@Component({
  selector: 'app-job-site-drawer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: 'job-site-drawer.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class JobSiteDrawer {
  private readonly fb = inject(FormBuilder);
  private readonly jobSiteService = inject(JobSiteService);

  @Input() set isOpen(value: boolean) {
    this._isOpen.set(value);
    if (value) this.initializeForm();
  }
  get isOpen(): boolean { return this._isOpen(); }
  
  @Input() set jobSite(value: JobSiteResponse | null) {
    this._jobSite.set(value);
  }

  @Output() closed = new EventEmitter<void>();
  @Output() success = new EventEmitter<void>();

  private readonly _isOpen = signal<boolean>(false);
  private readonly _jobSite = signal<JobSiteResponse | null>(null);
  
  readonly isSubmitting = signal<boolean>(false);
  readonly toastMessage = signal<{ detail: string; status: number } | null>(null);

  readonly form = this.fb.nonNullable.group({
    projectCode: ['', [Validators.required]],
    siteName: ['', [Validators.required]],
    latitude: [0, [Validators.required]],
    longitude: [0, [Validators.required]],
    geofenceRadiusMeters: [500, [Validators.required, Validators.min(1)]],
    heavyTransportRate: [500.00, [Validators.required, Validators.min(0)]] // NEW FIELD
  });

  constructor() {
    effect(() => {
      const site = this._jobSite();
      if (site) {
        this.form.patchValue(site);
        this.form.controls.projectCode.disable();
      } else {
        this.form.reset({ geofenceRadiusMeters: 500, heavyTransportRate: 500.00 });
        this.form.controls.projectCode.enable();
      }
    });
  }

  initializeForm(): void {
    this.toastMessage.set(null);
    this.isSubmitting.set(false);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.toastMessage.set(null);

    const site = this._jobSite();
    const request$ = site 
      ? this.jobSiteService.update(site.id, this.form.getRawValue())
      : this.jobSiteService.create(this.form.getRawValue());

    request$.subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.success.emit();
      },
      error: (err: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        this.handleError(err);
      }
    });
  }

  closeDrawer(): void {
    this.closed.emit();
  }

  private handleError(err: HttpErrorResponse): void {
    if (err.error && typeof err.error === 'object' && 'detail' in err.error) {
      const problem = err.error as ProblemDetail;
      this.toastMessage.set({ detail: problem.detail, status: problem.status });
    } else {
      this.toastMessage.set({ detail: 'System rejected the transaction.', status: err.status || 500 });
    }
  }

  dismissToast(): void {
    this.toastMessage.set(null);
  }
}