import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, NgIf],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  success = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router,   private notificationService: NotificationService) {
    if (this.auth.isLoggedIn()) {
      
      this.router.navigate([this.auth.isAdmin() ? '/dashboard' : '/mi-panel']);
    }
  }

  onSubmit(): void {
    if (!this.username || !this.password) return;
    this.loading = true;
    this.error = '';
    this.auth.login(this.username, this.password).subscribe({
      next: (res) => {
          this.notificationService.connect();

  this.router.navigate([
    res.roles.includes('ROLE_ADMIN')
      ? '/dashboard'
      : '/mi-panel'
  ]);
      },
      error: () => {
        this.error = 'Usuario o contraseña inválidos';
        this.loading = false;
      }
    });
  }
}
