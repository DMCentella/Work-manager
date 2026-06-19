import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NgIf, AsyncPipe, NgFor } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { NotificationService, Notificacion } from '../../services/notification.service';
import { map, Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, NgIf, AsyncPipe, NgFor],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isLoggedIn$: Observable<boolean>;
  isAdmin$: Observable<boolean>;
  notifications$: Observable<Notificacion[]>;
  unreadCount$: Observable<number>;
  username: string = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private notifService: NotificationService
  ) {
    this.isLoggedIn$ = this.auth.currentUser$.pipe(map(u => !!u));
    this.isAdmin$ = this.auth.currentUser$.pipe(map(u => u?.roles?.includes('ROLE_ADMIN')));
    this.notifications$ = this.notifService.notifications$;
    this.unreadCount$ = this.notifService.unreadCount$;
    this.auth.currentUser$.subscribe(u => this.username = u?.username || '');
  }

  ngOnInit(): void {
    if (this.auth.isLoggedIn()) {
      this.notifService.connect();
    }
  }

  onNotifClick(n: Notificacion): void {
    this.notifService.markAsRead(n.id);
  }

  markAllAsRead(): void {
    this.notifService.markAllAsRead();
  }

  logout(): void {
    this.notifService.disconnect();
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
