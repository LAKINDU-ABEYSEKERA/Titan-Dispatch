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

  ngOnInit(): void {
    // DEV-ONLY: Automatically negotiates a real token to satisfy backend 401 checks and routing guards
    if (isDevMode() && !this.authService.accessToken()) {
      // NOTE: Replace with your actual local testing credentials
      this.authService.login({ username: 'dev_operator', password: 'password123' }).subscribe({
        next: () => this.router.navigate(['/dashboard'])
      });
    }
  }
}