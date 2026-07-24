import { Component, inject, signal, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MaintenanceService } from '../../../../core/services/maintenance.service';
import { EquipmentService } from '../../../../core/services/equipment.service';
import { EquipmentResponse } from '../../../../core/models/domain';

@Component({
  selector: 'app-send-to-shop-drawer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: 'send-to-shop-drawer.html'
})
export class SendToShopDrawer {
  private readonly fb = inject(FormBuilder);
  private readonly maintenanceService = inject(MaintenanceService);
  private readonly equipmentService = inject(EquipmentService);

  readonly isOpen = signal(false);
  readonly equipment = signal<EquipmentResponse[]>([]);
  @Output() saved = new EventEmitter<void>();

  readonly form = this.fb.nonNullable.group({
    equipmentId: ['', Validators.required],
    expectedEndDate: ['', Validators.required]
  });

  open(): void {
    this.isOpen.set(true);
    
    this.equipmentService.getInventory().subscribe({
      next: (data) => {
        // THE BULLETPROOF FIX:
        // We flip the logic to explicitly ban 'MAINTENANCE' and 'DISPATCHED'.
        // We also force uppercase to prevent JSON serialization casing bugs.
        this.equipment.set(data.filter(e => {
          const safeStatus = e.status?.toUpperCase() || '';
          return safeStatus !== 'MAINTENANCE' && safeStatus !== 'DISPATCHED';
        }));
      }
    });
  }

  close(): void {
    this.isOpen.set(false);
    this.form.reset();
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    const { equipmentId, expectedEndDate } = this.form.getRawValue();
    this.maintenanceService.sendToShop(equipmentId, expectedEndDate).subscribe({
      next: () => {
        this.saved.emit();
        this.close();
      }
    });
  }
}