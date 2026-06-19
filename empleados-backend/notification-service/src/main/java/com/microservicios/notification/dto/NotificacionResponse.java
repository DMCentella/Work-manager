package com.microservicios.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponse {
    private Long id;
    private Long empleadoId;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fechaCreacion;
}
