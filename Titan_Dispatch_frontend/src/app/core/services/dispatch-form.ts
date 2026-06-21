import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { forkJoin, Observable, tap } from 'rxjs';
import { EquipmentDropdown, OperatorDropdown, JobSiteDropdown, CreateDispatchCommand } from '../models/domain';

@Injectable({
  providedIn: 'root'
})
export class DispatchFormService {
  private readonly http = inject(HttpClient);

  // Private Signals holding the lookup arrays
  private readonly _equipment = signal<EquipmentDropdown[]>([]);
  private readonly _operators = signal<OperatorDropdown[]>([]);
  private readonly _jobSites = signal<JobSiteDropdown[]>([]);

  // Public Projections
  readonly equipment = computed(() => this._equipment());
  readonly operators = computed(() => this._operators());
  readonly jobSites = computed(() => this._jobSites());

  // Concurrently fetch all lookup data
  hydrateForm(): Observable<[EquipmentDropdown[], OperatorDropdown[], JobSiteDropdown[]]> {
    return forkJoin([
      this.http.get<EquipmentDropdown[]>('/api/v1/dispatch/form-data/equipment'),
      this.http.get<OperatorDropdown[]>('/api/v1/dispatch/form-data/operators'),
      this.http.get<JobSiteDropdown[]>('/api/v1/dispatch/form-data/job-sites')
    ]).pipe(
      tap(([equipment, operators, jobSites]) => {
        this._equipment.set(equipment);
        this._operators.set(operators);
        this._jobSites.set(jobSites);
      })
    );
  }

  // Submit the allocation command
  allocate(command: CreateDispatchCommand): Observable<void> {
    return this.http.post<void>('/api/v1/dispatch/allocate', command);
  }
}