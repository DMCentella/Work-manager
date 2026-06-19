package com.microservicios.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaRegistradaEvent {
    private Long empleadoId;
    private String estado;
}
