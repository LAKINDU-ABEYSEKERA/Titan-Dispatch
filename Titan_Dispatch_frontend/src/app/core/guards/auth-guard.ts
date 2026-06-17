import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth/auth';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Read state directly from our pure in-memory authentication Signal
  if (authService.accessToken()) {
    return true;
  }

  // Enforce fallback redirection straight to the security gate
  return router.createUrlTree(['/login']);
};