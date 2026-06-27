package com.microservicios.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String mensaje;
    private String error;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponse(String mensaje, int status) {
        this.mensaje = mensaje;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String mensaje, String error, int status) {
        this.mensaje = mensaje;
        this.error = error;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
