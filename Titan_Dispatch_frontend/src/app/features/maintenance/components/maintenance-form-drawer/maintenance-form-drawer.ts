import { Component, inject, signal, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MaintenanceService } from '../../../../core/services/maintenance.service';
import { EquipmentService } from '../../../../core/services/equipment.service';
import { EquipmentResponse } from '../../../../core/models/domain';

@Component({
  selector: 'app-maintenance-form-drawer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './maintenance-form-drawer.html'
})
export class MaintenanceFormDrawer {
  private readonly fb = inject(FormBuilder);
  private readonly maintenanceService = inject(MaintenanceService);
  private readonly equipmentService = inject(EquipmentService);

  readonly isOpen = signal(false);
  
  // ALIGNED: These match the HTML template perfectly now
  readonly equipment = signal<EquipmentResponse[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  
  @Output() saved = new EventEmitter<void>();

  readonly form = this.fb.nonNullable.group({
    equipmentId: ['', Validators.required],
    serviceDate: ['', Validators.required],
    hoursAtService: [0, [Validators.required, Validators.min(0.1)]],
    serviceType: ['', Validators.required],
    totalCost: [0, [Validators.required, Validators.min(0)]],
    notes: ['']
  });

  open(): void {
    this.isOpen.set(true);
    this.errorMessage.set(null);
    this.equipmentService.getInventory().subscribe({
      next: (data) => {
        // THE FIX: Only allow logging service for vehicles currently in the shop
        this.equipment.set(data.filter(e => e.status === 'MAINTENANCE'));
      }
    });
  }

  close(): void {
    this.isOpen.set(false);
    this.errorMessage.set(null);
    this.form.reset({
      hoursAtService: 0,
      totalCost: 0
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.maintenanceService.submitLog(this.form.getRawValue()).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.saved.emit();
        this.close();
      },
      error: () => {
        this.isLoading.set(false);
        this.errorMessage.set('Failed to submit log. Please verify network connection.');
      }
    });
  }
}