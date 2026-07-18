import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JobSiteResponse, CreateJobSiteCommand, UpdateJobSiteCommand } from '../models/job-site';

@Injectable({
  providedIn: 'root'
})
export class JobSiteService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/job-sites';

  getAll(): Observable<JobSiteResponse[]> {
    return this.http.get<JobSiteResponse[]>(this.baseUrl);
  }

  create(command: CreateJobSiteCommand): Observable<JobSiteResponse> {
    return this.http.post<JobSiteResponse>(this.baseUrl, command);
  }

  update(id: string, command: UpdateJobSiteCommand): Observable<JobSiteResponse> {
    return this.http.put<JobSiteResponse>(`${this.baseUrl}/${id}`, command);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}