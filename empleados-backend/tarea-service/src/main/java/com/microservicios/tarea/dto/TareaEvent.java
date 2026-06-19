package com.microservicios.tarea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaEvent {
    private Long tareaId;
    private Long empleadoId;
    private String descripcion;
}
