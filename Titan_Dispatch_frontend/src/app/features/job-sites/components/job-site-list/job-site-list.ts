import { Component, ChangeDetectionStrategy, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JobSiteService } from '../../../../core/services/job-site.service';
import { JobSiteResponse } from '../../../../core/models/job-site';
import { JobSiteDrawer } from '../job-site-drawer/job-site-drawer';

@Component({
  selector: 'app-job-site-list',
  standalone: true,
  imports: [CommonModule, JobSiteDrawer],
  templateUrl: './job-site-list.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [`
    :host {
      display: block;
      width: 100%;
      height: 100%;
    }
    
    @keyframes slideFadeIn {
      0% {
        opacity: 0;
        transform: translateY(15px);
      }
      100% {
        opacity: 1;
        transform: translateY(0);
      }
    }
    
    .animate-row {
      animation: slideFadeIn 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
      opacity: 0; /* Starts hidden, animation reveals it */
    }
  `]
})
export class JobSiteList implements OnInit {
  private readonly jobSiteService = inject(JobSiteService);

  readonly jobSites = signal<JobSiteResponse[]>([]);
  readonly isLoading = signal<boolean>(true);
  readonly isDrawerOpen = signal<boolean>(false);
  readonly selectedJobSite = signal<JobSiteResponse | null>(null);

  ngOnInit(): void {
    this.loadJobSites();
  }

  loadJobSites(): void {
    this.isLoading.set(true);
    this.jobSiteService.getAll().subscribe({
      next: (data) => {
        this.jobSites.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  openDrawer(jobSite?: JobSiteResponse): void {
    this.selectedJobSite.set(jobSite || null);
    this.isDrawerOpen.set(true);
  }

  closeDrawer(): void {
    this.isDrawerOpen.set(false);
    this.selectedJobSite.set(null);
  }

  onDrawerSuccess(): void {
    this.closeDrawer();
    this.loadJobSites();
  }
}