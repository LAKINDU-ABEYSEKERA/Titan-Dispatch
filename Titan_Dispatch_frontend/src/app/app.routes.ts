import { Routes } from '@angular/router';
import { authGuard } from '../app/core/guards/auth-guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login)
  },
  {
    path: '',
    loadComponent: () => import('./core/layout/layout/layout').then(m => m.Layout),
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        // Perfectly aligned to your terminal's nested layout output
        loadComponent: () => import('./features/dashboard/dashboard/dashboard').then(m => m.Dashboard)
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];