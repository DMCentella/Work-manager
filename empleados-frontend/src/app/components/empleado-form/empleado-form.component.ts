import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { EmpleadoService, Empleado } from '../../services/empleado.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-empleado-form',
  standalone: true,
  imports: [FormsModule, RouterLink, NgIf],
  templateUrl: './empleado-form.component.html',
  styleUrls: ['./empleado-form.component.css']
})
export class EmpleadoFormComponent implements OnInit {
  empleado: Empleado = { nombre: '', apellido: '', email: '', telefono: '', sexo: '', salario: 0, fecha: new Date().toISOString().split('T')[0], cargo: '', departamento: '' };
  isEdit = false;
  tieneUsuario = false;
  username = '';
  password = '';
  showPass = false;
  loading = false;
  success = '';
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private empService: EmpleadoService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.empService.obtener(+id).subscribe(e => {
        this.empleado = e;
        this.tieneUsuario = false;
      });
    }
  }

  onSubmit(): void {
    this.loading = true;
    this.success = '';
    this.error = '';

    const save$ = this.isEdit
      ? this.empService.actualizar(this.empleado.id!, this.empleado)
      : this.empService.crear(this.empleado);

    save$.subscribe({
      next: (emp) => {
        if (this.username && this.username.trim() && this.password && this.password.trim()) {
          this.auth.register(this.username.trim(), this.password, emp.id).subscribe({
            next: () => {
              this.success = 'Empleado y usuario creados correctamente.';
              this.loading = false;
              setTimeout(() => this.router.navigate(['/empleados']), 1500);
            },
            error: (err) => {
              this.success = 'Empleado guardado, pero error al crear usuario: ' + (err.error?.error || 'Error');
              this.loading = false;
            }
          });
        } else {
          this.success = this.isEdit ? 'Empleado actualizado.' : 'Empleado creado.';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/empleados']), 1500);
        }
      },
      error: (err) => {
        this.error = 'Error al guardar: ' + (err.error?.message || 'Datos inválidos');
        this.loading = false;
      }
    });
  }

  togglePass(): void { this.showPass = !this.showPass; }

  generarPass(): void {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$!';
    let pass = '';
    for (let i = 0; i < 10; i++) pass += chars.charAt(Math.floor(Math.random() * chars.length));
    this.password = pass;
    this.showPass = true;
  }
}
