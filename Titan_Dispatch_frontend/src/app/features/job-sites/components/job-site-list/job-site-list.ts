import { Component, ChangeDetectionStrategy, inject, signal, computed, OnInit } from '@angular/core';
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
  styles: [`/* Keep your existing styles here */`]
})
export class JobSiteList implements OnInit {
  private readonly jobSiteService = inject(JobSiteService);

  readonly jobSites = signal<JobSiteResponse[]>([]);
  readonly isLoading = signal<boolean>(true);
  readonly isDrawerOpen = signal<boolean>(false);
  readonly selectedJobSite = signal<JobSiteResponse | null>(null);

  // NEW: Search Engine
  readonly searchQuery = signal<string>('');
  readonly filteredJobSites = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    if (!query) return this.jobSites();
    return this.jobSites().filter(site => 
      site.projectCode.toLowerCase().includes(query) ||
      site.siteName.toLowerCase().includes(query)
    );
  });

  ngOnInit(): void {
    this.loadJobSites();
  }

  updateSearch(event: Event): void {
    this.searchQuery.set((event.target as HTMLInputElement).value);
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