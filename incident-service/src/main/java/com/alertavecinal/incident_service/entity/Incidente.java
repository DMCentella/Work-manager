package com.alertavecinal.incident_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "incidentes")
@Data
public class Incidente implements Serializable {


    private static final long serialVersionUID = 1L; // Requerido para la serialización

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos del ciudadano (opcional si es anónimo)
    private String nombresCiudadano;
    private String apellidosCiudadano;
    private boolean esAnonimo;

    private String descripcion;
    private String ubicacionExacta;
    private String prioridad; // ALTA, MEDIA, BAJA

    @Lob
    private String evidenciaFotoUrl; // URL o Base64 de la captura

    @Enumerated(EnumType.STRING)
    private EstadoIncidente estado;

    private LocalDateTime fechaCreacion;

    private String comentarioSerenazgo; // Acción/comentario agregado por el agente

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoIncidente.PENDIENTE; // Todo incidente nace PENDIENTE
    }










}
