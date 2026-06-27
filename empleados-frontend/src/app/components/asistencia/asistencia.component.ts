import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { AsistenciaService, Asistencia } from '../../services/asistencia.service';
import { EmpleadoService, Empleado } from '../../services/empleado.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-asistencia',
  standalone: true,
  imports: [FormsModule, NgFor, NgIf, RouterLink],
  templateUrl: './asistencia.component.html',
  styleUrls: ['./asistencia.component.css']
})
export class AsistenciaComponent implements OnInit {
  empleadoId!: number;
  empleado: Empleado | null = null;
  asistencias: Asistencia[] = [];
  nuevaFecha = new Date().toISOString().split('T')[0];
  nuevoEstado = 'PRESENTE';
  loading = false;
  msg = '';
  isAdmin = false;
  exportUrl = '';

  constructor(
    private route: ActivatedRoute,
    private asistService: AsistenciaService,
    private empService: EmpleadoService,
    private auth: AuthService
  ) {
    this.isAdmin = this.auth.isAdmin();
  }

  ngOnInit(): void {
    this.empleadoId = +this.route.snapshot.paramMap.get('id')!;
this.empService.obtener(this.empleadoId).subscribe(e => {
  this.empleado = e;
});
    this.loadAsistencias();
  }

  loadAsistencias(): void {
    this.asistService.porEmpleado(this.empleadoId).subscribe(a => this.asistencias = a);
  }

  countBy(estado: string): number {
    return this.asistencias.filter(a => a.estado === estado).length;
  }

  registrar(): void {
    this.loading = true;
    this.msg = '';
    const ahora = new Date();
    const hora = ahora.getHours().toString().padStart(2, '0') + ':' + ahora.getMinutes().toString().padStart(2, '0') + ':00';
    this.asistService.registrar({
      empleadoId: this.empleadoId,
      fecha: this.nuevaFecha,
      hora: hora,
      estado: this.nuevoEstado
    }).subscribe({
      next: () => { this.msg = 'Asistencia registrada correctamente'; this.loading = false; this.loadAsistencias(); },
      error: () => { this.msg = 'Error al registrar'; this.loading = false; }
    });
  }

  onDelete(id: number): void {
    if (confirm('¿Eliminar esta asistencia?')) {
      this.asistService.eliminar(id).subscribe(() => this.loadAsistencias());
    }
  }
  descargarPDF(): void {

  const nombreCompleto =
      `${this.empleado.nombre} ${this.empleado.apellido}`;

  this.asistService
      .exportarPDF(this.empleadoId, nombreCompleto)
      .subscribe({
        next: (blob) => {

          const url = window.URL.createObjectURL(blob);

          const a = document.createElement('a');
          a.href = url;
          a.download = 'asistencias.pdf';
          a.click();

          window.URL.revokeObjectURL(url);
        },
        error: err => console.error(err)
      });
}
}
