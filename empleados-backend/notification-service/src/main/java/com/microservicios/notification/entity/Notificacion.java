package com.microservicios.notification.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long empleadoId;

    @Column(nullable = false)
    private String mensaje;

    private Boolean leida = false;

    private LocalDateTime fechaCreacion;
}
