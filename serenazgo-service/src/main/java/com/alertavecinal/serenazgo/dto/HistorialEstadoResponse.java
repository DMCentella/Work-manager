package com.alertavecinal.serenazgo.dto;

import com.alertavecinal.serenazgo.enums.EstadoIncidente;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HistorialEstadoResponse {

    private Long id;

    private Long incidenteId;

    private Long serenazgoId;

    private EstadoIncidente estadoAnterior;

    private EstadoIncidente estadoNuevo;

    private LocalDateTime fechaCambio;
}