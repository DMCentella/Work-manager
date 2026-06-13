package com.alertavecinal.serenazgo.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidenteDTO {

    private Long id;

    private String tipo;

    private String descripcion;

    private String direccion;

    private String estado;
}