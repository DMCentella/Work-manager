package com.microservicios.notification.repository;

import com.microservicios.notification.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByEmpleadoIdOrderByFechaCreacionDesc(Long empleadoId);

    long countByEmpleadoIdAndLeidaFalse(Long empleadoId);
}
