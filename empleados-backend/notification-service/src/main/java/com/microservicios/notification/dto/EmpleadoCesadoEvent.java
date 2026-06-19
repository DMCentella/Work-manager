package com.microservicios.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoCesadoEvent {
    private Long empleadoId;
    private String nombre;
}
