package com.microservicios.tarea.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TareaRequest {
    @NotNull
    private Long empleadoId;

    @NotBlank
    private String descripcion;

    private String placaVehiculo;
    private String zonaEntrega;
    private Integer totalPaquetes;
    private Integer numeroMesa;
    private Integer metaCajas;
}
