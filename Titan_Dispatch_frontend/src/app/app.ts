import { Component, inject, isDevMode, OnInit } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { AuthService } from '../app/core/auth/auth';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html'
})
export class AppComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  
  // The Static Guard: Survives the Strict Mode double-render
  private static isLoggingIn = false;

  ngOnInit(): void {
    // Check if we are already in the middle of logging in
    if (isDevMode() && !this.authService.accessToken() && !AppComponent.isLoggingIn) {
      
      AppComponent.isLoggingIn = true; // Lock the door
      
      this.authService.login({ username: 'dev_operator', password: 'titan123' }).subscribe({
        next: () => {
          AppComponent.isLoggingIn = false; // Unlock on success
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          AppComponent.isLoggingIn = false; // Unlock on failure
          console.error('[Auth] Auto-login failed during dev boot', err);
        }
      });
    }
  }
}