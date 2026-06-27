import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Asistencia {
  id?: number;
  empleadoId: number;
  fecha: string;
  hora: string;
  estado: string;
}

@Injectable({ providedIn: 'root' })
export class AsistenciaService {
  private base = '/api/asistencias';

  constructor(private http: HttpClient) {}

  porEmpleado(empleadoId: number): Observable<Asistencia[]> {
    return this.http.get<Asistencia[]>(`${this.base}/empleado/${empleadoId}`);
  }

  registrar(asistencia: Asistencia): Observable<Asistencia> {
    return this.http.post<Asistencia>(this.base, asistencia);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  countHoy(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.base}/count/hoy`);
  }

exportarPDF(empleadoId: number, nombreEmpleado: string): Observable<Blob> {
  return this.http.get(
    `${this.base}/export/pdf/empleado/${empleadoId}?nombreEmpleado=${encodeURIComponent(nombreEmpleado)}`,
    {
      responseType: 'blob'
    }
  );
}
}
