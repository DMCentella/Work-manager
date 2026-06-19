import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NgFor, NgIf } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { AsistenciaService, Asistencia } from '../../services/asistencia.service';
import { TareaService, TareaEmpleado } from '../../services/tarea.service';

@Component({
  selector: 'app-mi-panel',
  standalone: true,
  imports: [RouterLink, NgFor, NgIf],
  templateUrl: './mi-panel.component.html',
  styleUrls: ['./mi-panel.component.css']
})
export class MiPanelComponent implements OnInit {
  empleado: any = null;
  asistencias: Asistencia[] = [];
  tareas: TareaEmpleado[] = [];
  tools: any = {};
  msg = '';

  constructor(
    private auth: AuthService,
    private http: HttpClient,
    private asistService: AsistenciaService,
    private tareaService: TareaService
  ) {}

  ngOnInit(): void {
    const token = this.auth.getToken();
    if (!token) return;

    const payload = JSON.parse(atob(token.split('.')[1]));
    const empleadoId = payload.empleadoId;

    if (empleadoId) {
      this.http.get('/api/empleados/' + empleadoId).subscribe({
        next: (e: any) => {
          this.empleado = e;
        },
        error: () => {}
      });
    }

    if (empleadoId) {
      this.asistService.porEmpleado(empleadoId).subscribe(a => this.asistencias = a);
      this.tareaService.porEmpleado(empleadoId).subscribe(t => {
        this.tareas = t;
        if (t.length > 0) {
          this.tools = {
            placaVehiculo: t[0].placaVehiculo,
            zonaEntrega: t[0].zonaEntrega,
            totalPaquetes: t[0].totalPaquetes,
            numeroMesa: t[0].numeroMesa,
            metaCajas: t[0].metaCajas
          };
        }
      });
    }
  }

  hasTools(): boolean {
    return !!(this.tools.placaVehiculo || this.tools.zonaEntrega || this.tools.totalPaquetes || this.tools.numeroMesa || this.tools.metaCajas);
  }

  countBy(estado: string): number {
    return this.asistencias.filter(a => a.estado === estado).length;
  }

  onCompletar(id: number): void {
    if (confirm('¿Marcar esta tarea como completada?')) {
      this.tareaService.completar(id).subscribe({
        next: () => {
          this.msg = 'Tarea completada.';
          setTimeout(() => this.msg = '', 3000);
          this.tareaService.porEmpleado(this.empleado.id).subscribe(t => this.tareas = t);
        },
        error: () => this.msg = 'Error al completar tarea.'
      });
    }
  }
}
