package com.microservicios.asistencia.dto;

import com.microservicios.asistencia.entidad.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaResponse {
    private Long id;
    private Long empleadoId;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoAsistencia estado;
}
