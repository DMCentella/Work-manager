package com.microservicios.asistencia.servicio;

import com.microservicios.asistencia.dto.AsistenciaEvent;
import com.microservicios.asistencia.entidad.Asistencia;
import com.microservicios.asistencia.repositorio.AsistenciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AsistenciaService {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaService.class);
    private final AsistenciaRepository asistenciaRepository;
    private final RabbitTemplate rabbitTemplate;

    public AsistenciaService(AsistenciaRepository asistenciaRepository, RabbitTemplate rabbitTemplate) {
        this.asistenciaRepository = asistenciaRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Asistencia> listar() {
        return asistenciaRepository.findAll();
    }

    public List<Asistencia> porEmpleado(Long empleadoId) {
        return asistenciaRepository.findByEmpleadoId(empleadoId);
    }

    public Asistencia buscar(Long id) {
        return asistenciaRepository.findById(id).orElse(null);
    }

    public Asistencia guardar(Asistencia asistencia) {
        if (asistencia.getHora() == null) {
            asistencia.setHora(java.time.LocalTime.now());
        }
        if (asistencia.getFecha() == null) {
            asistencia.setFecha(LocalDate.now());
        }
        Asistencia saved = asistenciaRepository.save(asistencia);
        try {
            rabbitTemplate.convertAndSend("notificaciones.exchange", "asistencia.registrada",
                    new AsistenciaEvent(saved.getEmpleadoId(), saved.getEstado().name()));
        } catch (Exception ex) {
            log.error("Error al enviar evento de asistencia a RabbitMQ: {}", ex.getMessage());
        }
        return saved;
    }

    public void eliminar(Long id) {
        if (!asistenciaRepository.existsById(id)) {
            throw new IllegalArgumentException("La asistencia con ID " + id + " no existe");
        }
        asistenciaRepository.deleteById(id);
    }

    public long countHoy() {
        return asistenciaRepository.countByFecha(LocalDate.now());
    }
}
