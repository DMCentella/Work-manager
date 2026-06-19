import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TareaEmpleado {
  id?: number;
  empleadoId: number;
  descripcion: string;
  completada: boolean;
  fechaAsignacion: string;
  fechaCompletado: string | null;
  placaVehiculo: string | null;
  zonaEntrega: string | null;
  totalPaquetes: number | null;
  numeroMesa: number | null;
  metaCajas: number | null;
}

@Injectable({ providedIn: 'root' })
export class TareaService {
  private base = '/api/tareas';

  constructor(private http: HttpClient) {}

  porEmpleado(empleadoId: number): Observable<TareaEmpleado[]> {
    return this.http.get<TareaEmpleado[]>(`${this.base}/empleado/${empleadoId}`);
  }

  pendientes(empleadoId: number): Observable<TareaEmpleado[]> {
    return this.http.get<TareaEmpleado[]>(`${this.base}/empleado/${empleadoId}/pendientes`);
  }

  crear(tarea: TareaEmpleado): Observable<TareaEmpleado> {
    return this.http.post<TareaEmpleado>(this.base, tarea);
  }

  completar(id: number): Observable<TareaEmpleado> {
    return this.http.put<TareaEmpleado>(`${this.base}/${id}/completar`, {});
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
