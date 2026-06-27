import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { TareaService, TareaEmpleado } from '../../services/tarea.service';
import { EmpleadoService, Empleado } from '../../services/empleado.service';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-tarea-gestion',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf, RouterLink],
  templateUrl: './tarea-gestion.component.html',
  styleUrls: ['./tarea-gestion.component.css']
})
export class TareaGestionComponent implements OnInit {
  empleadoId!: number;
  empleado: Empleado | null = null;
  tareas: TareaEmpleado[] = [];
  nuevaTarea: any = { descripcion: '', placaVehiculo: '', zonaEntrega: '', totalPaquetes: null, numeroMesa: '', metaCajas: null };

  constructor(
    private route: ActivatedRoute,
    private tareaService: TareaService,
    private empService: EmpleadoService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.empleadoId = +this.route.snapshot.paramMap.get('id')!;
    this.empService.obtener(this.empleadoId).subscribe(e => this.empleado = e);
    this.loadTareas();
    this.notificationService.notifications$.subscribe(() => {
  this.loadTareas();
});
  }

  loadTareas(): void {
    this.tareaService.porEmpleado(this.empleadoId).subscribe(t => this.tareas = t);
  }

  onCrear(): void {
    if (!this.nuevaTarea.descripcion) return;
    this.tareaService.crear({
      empleadoId: this.empleadoId,
      descripcion: this.nuevaTarea.descripcion,
      completada: false,
      fechaAsignacion: null,
      fechaCompletado: null,
      placaVehiculo: this.nuevaTarea.placaVehiculo || null,
      zonaEntrega: this.nuevaTarea.zonaEntrega || null,
      totalPaquetes: this.nuevaTarea.totalPaquetes || null,
      numeroMesa: this.nuevaTarea.numeroMesa || null,
      metaCajas: this.nuevaTarea.metaCajas || null
    }).subscribe(() => {
      this.nuevaTarea = { descripcion: '', placaVehiculo: '', zonaEntrega: '', totalPaquetes: null, numeroMesa: '', metaCajas: null };
      this.loadTareas();
    });
  }

  onDelete(id: number): void {
    if (confirm('¿Eliminar esta tarea?')) {
      this.tareaService.eliminar(id).subscribe(() => this.loadTareas());
    }
  }
}
