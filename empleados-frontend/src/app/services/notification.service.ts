import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
import { AuthService } from './auth.service';

export interface Notificacion {
  id: number;
  empleadoId: number;
  mensaje: string;
  leida: boolean;
  fechaCreacion: string;
}

declare var SockJS: any;

@Injectable({ providedIn: 'root' })
export class NotificationService implements OnDestroy {
  private client: Client;
  private notificationsSubject = new BehaviorSubject<Notificacion[]>([]);
  private unreadCountSubject = new BehaviorSubject<number>(0);

  notifications$: Observable<Notificacion[]> = this.notificationsSubject.asObservable();
  unreadCount$: Observable<number> = this.unreadCountSubject.asObservable();

  constructor(private auth: AuthService) {
    this.client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: {},
      debug: (msg: string) => console.log('[STOMP]', msg),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });
  }

  connect(): void {
    const token = this.auth.getToken();
    if (!token) {
      console.warn('[STOMP] No hay token, no se conecta');
      return;
    }

    const empleadoId = this.getEmpleadoIdFromToken(token);
    const isAdmin = this.auth.isAdmin();

    console.log('[STOMP] Conectando via SockJS a /ws, empleadoId:', empleadoId, 'admin:', isAdmin);

    this.client.configure({
      connectHeaders: { Authorization: `Bearer ${token}` }
    });

    this.client.onConnect = () => {
      console.log('[STOMP] Conectado');

      if (empleadoId) {
        const topic = '/topic/notificaciones/' + empleadoId;
        console.log('[STOMP] Suscribiendo a', topic);
        this.client.subscribe(topic, (message: IMessage) => {
          console.log('[STOMP] Notificacion recibida:', message.body);
          this.onMessage(message.body);
        });
        
      }

      if (isAdmin) {
        console.log('[STOMP] Suscribiendo a /topic/dashboard');
        this.client.subscribe('/topic/dashboard', (message: IMessage) => {
          console.log('[STOMP] Dashboard recibido:', message.body);
          this.onMessage(message.body);
        });
         console.log('[STOMP] Suscribiendo a /topic/notificaciones/admin');

  this.client.subscribe('/topic/notificaciones/admin', (message: IMessage) => {
    console.log('[STOMP] Notificación admin:', message.body);
    this.onMessage(message.body);
  });
      }


    };

    this.client.onStompError = (frame) => {
      console.error('[STOMP] Error STOMP:', frame.headers['message'], frame.body);
    };

    this.client.onWebSocketClose = (evt) => {
      console.warn('[STOMP] SockJS cerrado, codigo:', evt.code);
    };

    this.client.activate();
  }

  private onMessage(body: string): void {
    try {
      const notif = JSON.parse(body) as Notificacion;
      const current = this.notificationsSubject.value;
      this.notificationsSubject.next([notif, ...current]);
      this.updateUnreadCount();
    } catch (e) {
      console.error('[STOMP] Error al parsear:', e, body);
    }
  }

  private getEmpleadoIdFromToken(token: string): number | null {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.empleadoId || null;
    } catch {
      return null;
    }
  }

  disconnect(): void {
    if (this.client.active) {
      this.client.deactivate();
    }
  }

  getNotifications(): Notificacion[] {
    return this.notificationsSubject.value;
  }

  getUnreadCount(): number {
    return this.notificationsSubject.value.filter(n => !n.leida).length;
  }

  markAsRead(id: number): void {
    const updated = this.notificationsSubject.value.map(n =>
      n.id === id ? { ...n, leida: true } : n
    );
    this.notificationsSubject.next(updated);
    this.updateUnreadCount();
  }

  markAllAsRead(): void {
    const updated = this.notificationsSubject.value.map(n => ({ ...n, leida: true }));
    this.notificationsSubject.next(updated);
    this.updateUnreadCount();
  }

  private updateUnreadCount(): void {
    this.unreadCountSubject.next(this.getUnreadCount());
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
  
}
