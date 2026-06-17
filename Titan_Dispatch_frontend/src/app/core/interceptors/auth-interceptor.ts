import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, filter, take, throwError, BehaviorSubject } from 'rxjs';
import { AuthService } from '../auth/auth';

// Module-level state to manage concurrent 401s seamlessly
let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.accessToken();

  // Rule 1: Always ensure the browser sends the HttpOnly refresh cookie
  let authReq = req.clone({ withCredentials: true });

  // Rule 2: Attach the Access Token if we hold it in memory
  if (token) {
    authReq = authReq.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Rule 3: 401 Retry Logic (Ignore failures on auth endpoints to prevent loops)
      if (error.status === 401 && !authReq.url.includes('/api/v1/auth/')) {
        
        if (!isRefreshing) {
          isRefreshing = true;
          refreshTokenSubject.next(null);

          return authService.refresh().pipe(
            switchMap((response) => {
              isRefreshing = false;
              refreshTokenSubject.next(response.access_token);
              
              // Retry the original failed request with the new token
              return next(authReq.clone({
                setHeaders: { Authorization: `Bearer ${response.access_token}` }
              }));
            }),
            catchError((refreshErr) => {
              isRefreshing = false;
              authService.accessToken.set(null);
              // In a full implementation, you would trigger a router redirect to /login here
              return throwError(() => refreshErr);
            })
          );
        } else {
          // If a refresh is already in progress, queue subsequent failed requests
          return refreshTokenSubject.pipe(
            filter(newToken => newToken !== null),
            take(1),
            switchMap((newToken) => {
              return next(authReq.clone({
                setHeaders: { Authorization: `Bearer ${newToken as string}` }
              }));
            })
          );
        }
      }
      
      // Pass any other errors (like 409, 400, 429) downstream to the caller or an ErrorInterceptor
      return throwError(() => error);
    })
  );
};