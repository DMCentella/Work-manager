import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, NgClass } from '@angular/common';
import { EmpleadoService, Empleado, PageResponse } from '../../services/empleado.service';
import { PaginationComponent } from '../pagination/pagination.component';

@Component({
  selector: 'app-empleado-list',
  standalone: true,
  imports: [RouterLink, FormsModule, NgFor, NgIf,  PaginationComponent],
  templateUrl: './empleado-list.component.html',
  styleUrls: ['./empleado-list.component.css']
})
export class EmpleadoListComponent implements OnInit {
  empleados: Empleado[] = [];
  page = 0;
  totalPages = 0;
  totalElements = 0;
  searchTerm = '';
  exportUrl = '';

  constructor(private empService: EmpleadoService) {
  
  
    
  }

  descargarPDF() {
    this.empService.exportarPDF().subscribe({
        next: (blob) => {
            const url = window.URL.createObjectURL(blob);

            const a = document.createElement('a');
            a.href = url;
            a.download = 'empleados.pdf';
            a.click();

            window.URL.revokeObjectURL(url);
        }
    });
}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.empService.listar(this.page, 5, this.searchTerm || undefined).subscribe({
      next: (res: PageResponse) => {
        this.empleados = res.content;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
      }
    });
  }

  onSearch(): void {
    this.page = 0;
    this.loadData();
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.page = 0;
    this.loadData();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.loadData();
  }

onInactivar(id: number): void {
  if (confirm('¿Estás seguro de dar de baja al empleado?')) {
    this.empService.inactivar(id).subscribe(() => this.loadData());
  }
}
onActivar(id: number): void {
  if (confirm('¿Deseas reactivar al empleado?')) {
    this.empService.activar(id).subscribe(() => this.loadData());
  }
}
}
