import { Component, inject, signal, output, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { MaintenanceService } from '../../../../core/services/maintenance.service';
import { EquipmentService } from '../../../../core/services/equipment.service';
import { CreateMaintenanceLogCommand } from '../../../../core/models/domain';

@Component({
  selector: 'app-maintenance-form-drawer',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './maintenance-form-drawer.html'
})
export class MaintenanceFormDrawer implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly maintenanceService = inject(MaintenanceService);
  private readonly equipmentService = inject(EquipmentService);

  saved = output<void>();

  isOpen = signal(false);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  
  readonly inventory = this.equipmentService.equipment;

  form = this.fb.nonNullable.group({
    equipmentId: ['', [Validators.required]],
    serviceDate: ['', [Validators.required]],
    hoursAtService: [0, [Validators.required, Validators.min(0)]],
    serviceType: ['PREVENTATIVE', [Validators.required]],
    totalCost: [0, [Validators.required, Validators.min(0)]],
    notes: ['', [Validators.required, Validators.maxLength(1000)]]
  });

  ngOnInit() {
    if (this.inventory().length === 0) {
      this.equipmentService.getInventory().subscribe();
    }
  }

  open() {
    this.form.reset({ serviceType: 'PREVENTATIVE', hoursAtService: 0, totalCost: 0 });
    
    // Generates '2026-07-11' for the HTML date picker
    const now = new Date();
    const cleanDateString = now.toISOString().split('T')[0];
    this.form.patchValue({ serviceDate: cleanDateString });
    
    this.errorMessage.set(null);
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

    const rawValue = this.form.getRawValue();
    
    // THE FIX: We intercept the raw value and attach T00:00:00 so the Java backend accepts it
    const payload = {
      ...rawValue,
      serviceDate: `${rawValue.serviceDate}T00:00:00`
    } as unknown as CreateMaintenanceLogCommand;
    
    this.maintenanceService.submitLog(payload).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.saved.emit();
        this.close();
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.detail || 'Failed to submit maintenance log.');
      }
    });
  }
}