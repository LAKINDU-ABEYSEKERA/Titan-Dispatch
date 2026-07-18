import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login)
  },
  {
    path: '',
    loadComponent: () => import('./core/layout/layout/layout').then(m => m.Layout),
    canActivate: [authGuard], // <-- This protects ALL children below it
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dispatch/dispatch-list/dispatch-list').then(m => m.DispatchList)
      },
      {
        path: 'equipment',
        loadComponent: () => import('./features/equipment/equipment').then(m => m.Equipment)
      },
      {
        path: 'maintenance',
        loadComponent: () => import('./features/maintenance/maintenance-dashboard/maintenance-dashboard').then(m => m.MaintenanceDashboard)
      },
      {
        path: 'operators',
        loadComponent: () => import('./features/operators/operator').then(m => m.Operator)
      },
      {
        path: 'job-sites',
        loadComponent: () => import('./features/job-sites/components/job-site-list/job-site-list').then(m => m.JobSiteList),
        title: 'Titan | Job Sites'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];