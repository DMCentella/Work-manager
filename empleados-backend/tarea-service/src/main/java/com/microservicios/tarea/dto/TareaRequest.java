package com.microservicios.tarea.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TareaRequest {
    @NotNull(message = "El ID del empleado es obligatorio")
    private Long empleadoId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no debe exceder 500 caracteres")
    private String descripcion;

    @Size(max = 20, message = "La placa no debe exceder 20 caracteres")
    private String placaVehiculo;

    @Size(max = 100, message = "La zona no debe exceder 100 caracteres")
    private String zonaEntrega;

    @PositiveOrZero(message = "El total de paquetes no puede ser negativo")
    private Integer totalPaquetes;

    @PositiveOrZero(message = "El número de mesa no puede ser negativo")
    private Integer numeroMesa;

    @PositiveOrZero(message = "La meta de cajas no puede ser negativa")
    private Integer metaCajas;
}
