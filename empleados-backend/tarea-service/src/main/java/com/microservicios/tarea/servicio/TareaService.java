package com.microservicios.tarea.servicio;

import com.microservicios.tarea.dto.TareaEvent;
import com.microservicios.tarea.entidad.TareaEmpleado;
import com.microservicios.tarea.repositorio.TareaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TareaService {

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
        rabbitTemplate.convertAndSend("notificaciones.exchange", "tarea.creada",
                new TareaEvent(saved.getId(), saved.getEmpleadoId(), saved.getDescripcion()));
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
        if (tarea != null) {
            tarea.setCompletada(true);
            tarea.setFechaCompletado(LocalDate.now());
            TareaEmpleado saved = tareaRepository.save(tarea);
            rabbitTemplate.convertAndSend("notificaciones.exchange", "tarea.completada",
                    new TareaEvent(saved.getId(), saved.getEmpleadoId(), saved.getDescripcion()));
            return saved;
        }
        return null;
    }

    public void eliminar(Long id) {
        tareaRepository.deleteById(id);
    }
}
