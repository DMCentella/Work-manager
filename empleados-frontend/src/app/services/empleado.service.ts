import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Empleado {
  id?: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  sexo: string;
  salario: number;
  fecha: string;
  cargo: string;
  departamento: string;
}

export interface PageResponse {
  content: Empleado[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class EmpleadoService {
  private base = '/api/empleados';

  constructor(private http: HttpClient) {}

  listar(page: number = 0, size: number = 5, search?: string): Observable<PageResponse> {
    let url = `${this.base}?page=${page}&size=${size}`;
    if (search) url += `&search=${search}`;
    return this.http.get<PageResponse>(url);
  }

  obtener(id: number): Observable<Empleado> {
    return this.http.get<Empleado>(`${this.base}/${id}`);
  }

  crear(empleado: Empleado): Observable<Empleado> {
    return this.http.post<Empleado>(this.base, empleado);
  }

  actualizar(id: number, empleado: Empleado): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.base}/${id}`, empleado);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  count(): Observable<number> {
    return this.http.get<number>(`${this.base}/count`);
  }

  porDepartamento(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.base}/departamentos`);
  }

  porSexo(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.base}/sexos`);
  }

  exportarPDF(): string {
    return `${this.base}/export/pdf`;
  }
}
