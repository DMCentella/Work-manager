import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { EmpleadoService } from '../../services/empleado.service';
import { AsistenciaService } from '../../services/asistencia.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  totalEmpleados = 0;
  asistenciasHoy = 0;
  deptoCount = 0;
  sexoCount = 0;
  departamentos: Map<string, number> = new Map();
  sexos: Map<string, number> = new Map();
  exportUrl = '';

  constructor(
    private empService: EmpleadoService,
    private asistService: AsistenciaService,
    private auth: AuthService
  ) {
    
  }
descargarPDF(): void {
  this.empService.exportarPDF().subscribe({
    next: (blob: Blob) => {
      const url = window.URL.createObjectURL(blob);

      const a = document.createElement('a');
      a.href = url;
      a.download = 'empleados.pdf';
      a.click();

      window.URL.revokeObjectURL(url);
    },
    error: (err) => {
      console.error('Error al descargar PDF', err);
    }
  });
}
  ngOnInit(): void {
    this.empService.count().subscribe(c => this.totalEmpleados = c);
    this.asistService.countHoy().subscribe(r => this.asistenciasHoy = r.count);
    this.empService.porDepartamento().subscribe(d => {
      this.departamentos = new Map(Object.entries(d));
      this.deptoCount = Object.keys(d).length;
    });
    this.empService.porSexo().subscribe(s => {
      this.sexos = new Map(Object.entries(s));
      this.sexoCount = Object.keys(s).length;
    });
  }

  getWidth(val: number): number {
    return this.totalEmpleados > 0 ? (val * 100 / this.totalEmpleados) : 0;
  }
}
