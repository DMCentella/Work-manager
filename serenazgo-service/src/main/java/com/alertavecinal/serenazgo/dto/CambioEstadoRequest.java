package com.alertavecinal.serenazgo.dto;

import com.alertavecinal.serenazgo.enums.EstadoIncidente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioEstadoRequest {

    private EstadoIncidente nuevoEstado;

}