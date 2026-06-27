package com.microservicios.asistencia.dto;

import com.microservicios.asistencia.entidad.EstadoAsistencia;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AsistenciaRequest {
    @NotNull(message = "El ID del empleado es obligatorio")
    private Long empleadoId;

    private LocalDate fecha;
    private LocalTime hora;

    @NotNull(message = "El estado de asistencia es obligatorio")
    private EstadoAsistencia estado = EstadoAsistencia.PRESENTE;
}
