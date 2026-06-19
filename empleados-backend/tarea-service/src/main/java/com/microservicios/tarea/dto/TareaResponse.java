package com.microservicios.tarea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaResponse {
    private Long id;
    private Long empleadoId;
    private String descripcion;
    private boolean completada;
    private LocalDate fechaAsignacion;
    private LocalDate fechaCompletado;
    private String placaVehiculo;
    private String zonaEntrega;
    private Integer totalPaquetes;
    private Integer numeroMesa;
    private Integer metaCajas;
}
