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
    this.equipmentService.getInventory().subscribe(data => {
      // Only show assets that aren't already in maintenance
      this.equipment.set(data.filter(e => e.status !== 'MAINTENANCE'));
    });
  }

  close(): void {
    this.isOpen.set(false);
    this.form.reset();
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    const { equipmentId, expectedEndDate } = this.form.getRawValue();
    this.maintenanceService.sendToShop(equipmentId, expectedEndDate).subscribe(() => {
      this.saved.emit();
      this.close();
    });
  }
}