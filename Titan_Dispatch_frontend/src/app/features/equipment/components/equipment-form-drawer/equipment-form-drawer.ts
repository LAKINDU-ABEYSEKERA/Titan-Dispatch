import { Component, inject, signal, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { EquipmentService } from '../../../../core/services/equipment.service';
import { EquipmentResponse } from '../../../../core/models/domain';

@Component({
  selector: 'app-equipment-form-drawer',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: 'equipment-form-drawer.html'
})
export class EquipmentFormDrawer {
  private readonly fb = inject(FormBuilder);
  private readonly equipmentService = inject(EquipmentService);

  saved = output<void>();

  isOpen = signal(false);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  activeId = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    assetTag: ['', [Validators.required, Validators.maxLength(50)]],
    internalHourlyRate: [0, [Validators.required, Validators.min(0.01)]],
    insuranceExpiration: ['', [Validators.required]]
  });

  openForCreate() {
    this.activeId.set(null);
    this.form.reset();
    this.errorMessage.set(null);
    this.isOpen.set(true);
  }

  openForEdit(equipment: EquipmentResponse) {
    this.activeId.set(equipment.id);
    this.form.patchValue({
      assetTag: equipment.assetTag,
      internalHourlyRate: equipment.internalHourlyRate,
      insuranceExpiration: equipment.insuranceExpiration
    });
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

    const payload = this.form.getRawValue();
    const request$ = this.activeId() 
      ? this.equipmentService.updateEquipment(this.activeId()!, payload)
      : this.equipmentService.createEquipment(payload);

    request$.subscribe({
      next: () => {
        this.isLoading.set(false);
        this.saved.emit();
        this.close();
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.detail || 'Failed to save equipment.');
      }
    });
  }
}