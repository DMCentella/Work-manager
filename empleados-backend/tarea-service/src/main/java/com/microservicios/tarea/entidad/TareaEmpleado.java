package com.microservicios.tarea.entidad;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tareas_empleado")
public class TareaEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private boolean completada = false;

    private LocalDate fechaAsignacion;
    private LocalDate fechaCompletado;
    private String placaVehiculo;
    private String zonaEntrega;
    private Integer totalPaquetes;
    private Integer numeroMesa;
    private Integer metaCajas;
}
