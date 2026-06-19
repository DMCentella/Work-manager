import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { EmpleadoService, Empleado, PageResponse } from '../../services/empleado.service';
import { PaginationComponent } from '../pagination/pagination.component';

@Component({
  selector: 'app-empleado-list',
  standalone: true,
  imports: [RouterLink, FormsModule, NgFor, NgIf, PaginationComponent],
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
    this.exportUrl = this.empService.exportarPDF();
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

  onDelete(id: number): void {
    if (confirm('¿Estás seguro de eliminar al empleado?')) {
      this.empService.eliminar(id).subscribe(() => this.loadData());
    }
  }
}
