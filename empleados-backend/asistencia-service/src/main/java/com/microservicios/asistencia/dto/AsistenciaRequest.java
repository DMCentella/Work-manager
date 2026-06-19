package com.microservicios.asistencia.dto;

import com.microservicios.asistencia.entidad.EstadoAsistencia;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AsistenciaRequest {
    @NotNull
    private Long empleadoId;

    private LocalDate fecha;
    private LocalTime hora;
    private EstadoAsistencia estado = EstadoAsistencia.PRESENTE;
}
