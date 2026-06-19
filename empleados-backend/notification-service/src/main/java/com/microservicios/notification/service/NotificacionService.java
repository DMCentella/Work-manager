package com.microservicios.notification.service;

import com.microservicios.notification.entity.Notificacion;
import com.microservicios.notification.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Notificacion crear(Long empleadoId, String mensaje) {
        Notificacion n = new Notificacion();
        n.setEmpleadoId(empleadoId);
        n.setMensaje(mensaje);
        n.setLeida(false);
        n.setFechaCreacion(LocalDateTime.now());
        return notificacionRepository.save(n);
    }

    public List<Notificacion> porEmpleado(Long empleadoId) {
        return notificacionRepository.findByEmpleadoIdOrderByFechaCreacionDesc(empleadoId);
    }

    public Notificacion marcarLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id).orElse(null);
        if (n != null) {
            n.setLeida(true);
            return notificacionRepository.save(n);
        }
        return null;
    }

    public void marcarTodasLeidas(Long empleadoId) {
        List<Notificacion> notificaciones = notificacionRepository.findByEmpleadoIdOrderByFechaCreacionDesc(empleadoId);
        notificaciones.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(notificaciones);
    }

    public long noLeidas(Long empleadoId) {
        return notificacionRepository.countByEmpleadoIdAndLeidaFalse(empleadoId);
    }

    public List<Notificacion> todas() {
        return notificacionRepository.findAll();
    }
}
