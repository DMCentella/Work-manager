import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<any>(JSON.parse(localStorage.getItem('currentUser') || 'null'));
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    return this.http.post('/api/auth/login', { username, password }).pipe(
      map((res: any) => {
        localStorage.setItem('currentUser', JSON.stringify(res));
        localStorage.setItem('token', res.token);
        this.currentUserSubject.next(res);
        return res;
      })
    );
  }

  register(username: string, password: string, empleadoId?: number): Observable<any> {
    return this.http.post('/api/auth/register', { username, password, empleadoId });
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return user?.roles?.includes('ROLE_ADMIN');
  }

  getRoles(): string[] {
    return this.currentUserSubject.value?.roles || [];
  }

  getUsername(): string {
    return this.currentUserSubject.value?.username || '';
  }
}
