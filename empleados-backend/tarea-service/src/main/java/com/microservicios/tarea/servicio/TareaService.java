package com.microservicios.tarea.servicio;

import com.microservicios.tarea.dto.TareaEvent;
import com.microservicios.tarea.entidad.TareaEmpleado;
import com.microservicios.tarea.repositorio.TareaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TareaService {

    private static final Logger log = LoggerFactory.getLogger(TareaService.class);
    private final TareaRepository tareaRepository;
    private final RabbitTemplate rabbitTemplate;

    public TareaService(TareaRepository tareaRepository, RabbitTemplate rabbitTemplate) {
        this.tareaRepository = tareaRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public TareaEmpleado guardar(TareaEmpleado tarea) {
        if (tarea.getFechaAsignacion() == null) {
            tarea.setFechaAsignacion(LocalDate.now());
        }
        TareaEmpleado saved = tareaRepository.save(tarea);
        try {
            rabbitTemplate.convertAndSend("notificaciones.exchange", "tarea.creada",
                    new TareaEvent(saved.getId(), saved.getEmpleadoId(), saved.getDescripcion()));
        } catch (Exception ex) {
            log.error("Error al enviar evento de tarea creada a RabbitMQ: {}", ex.getMessage());
        }
        return saved;
    }

    public List<TareaEmpleado> porEmpleado(Long empleadoId) {
        return tareaRepository.findByEmpleadoIdOrderByFechaAsignacionDesc(empleadoId);
    }

    public List<TareaEmpleado> pendientesPorEmpleado(Long empleadoId) {
        return tareaRepository.findByEmpleadoIdAndCompletadaFalse(empleadoId);
    }

    public TareaEmpleado findOne(Long id) {
        return tareaRepository.findById(id).orElse(null);
    }

    public TareaEmpleado completar(Long id) {
        TareaEmpleado tarea = tareaRepository.findById(id).orElse(null);
        if (tarea == null) {
            return null;
        }
        if (tarea.isCompletada()) {
            throw new IllegalArgumentException("La tarea con ID " + id + " ya está completada");
        }
        tarea.setCompletada(true);
        tarea.setFechaCompletado(LocalDate.now());
        TareaEmpleado saved = tareaRepository.save(tarea);
        try {
            rabbitTemplate.convertAndSend("notificaciones.exchange", "tarea.completada",
                    new TareaEvent(saved.getId(), saved.getEmpleadoId(), saved.getDescripcion()));
        } catch (Exception ex) {
            log.error("Error al enviar evento de tarea completada a RabbitMQ: {}", ex.getMessage());
        }
        return saved;
    }

    public void eliminar(Long id) {
        if (!tareaRepository.existsById(id)) {
            throw new IllegalArgumentException("La tarea con ID " + id + " no existe");
        }
        tareaRepository.deleteById(id);
    }
}
