import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  { path: 'dashboard', loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [authGuard, adminGuard] },
  { path: 'empleados', loadComponent: () => import('./components/empleado-list/empleado-list.component').then(m => m.EmpleadoListComponent), canActivate: [authGuard, adminGuard] },
  { path: 'empleados/nuevo', loadComponent: () => import('./components/empleado-form/empleado-form.component').then(m => m.EmpleadoFormComponent), canActivate: [authGuard, adminGuard] },
  { path: 'empleados/editar/:id', loadComponent: () => import('./components/empleado-form/empleado-form.component').then(m => m.EmpleadoFormComponent), canActivate: [authGuard, adminGuard] },
  { path: 'empleados/:id/asistencia', loadComponent: () => import('./components/asistencia/asistencia.component').then(m => m.AsistenciaComponent), canActivate: [authGuard] },
  { path: 'empleados/:id/tareas', loadComponent: () => import('./components/tarea-gestion/tarea-gestion.component').then(m => m.TareaGestionComponent), canActivate: [authGuard, adminGuard] },
  { path: 'mi-panel', loadComponent: () => import('./components/mi-panel/mi-panel.component').then(m => m.MiPanelComponent), canActivate: [authGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
