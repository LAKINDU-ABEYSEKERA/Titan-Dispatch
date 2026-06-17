import { ApplicationConfig, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from '../app/core/interceptors/auth-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    // Corrected to use the stable, non-experimental member name
    provideZonelessChangeDetection(),
    
    provideRouter(routes),
    
    // Configures clean streaming fetch architecture and token interception
    provideHttpClient(withFetch(), withInterceptors([authInterceptor]))
  ]
};