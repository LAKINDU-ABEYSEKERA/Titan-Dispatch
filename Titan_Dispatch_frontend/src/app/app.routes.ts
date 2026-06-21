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
    canActivate: [authGuard],
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
      }
    ]
  },
  
  {
    path: '**',
    redirectTo: 'login'
  }
];