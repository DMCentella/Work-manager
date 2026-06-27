package com.microservicios.empleado.servicio;

import com.microservicios.empleado.dto.EmpleadoCesadoEvent;
import com.microservicios.empleado.entidad.Empleado;
import com.microservicios.empleado.enums.Estadoempleado;
import com.microservicios.empleado.repositorio.EmpleadoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EmpleadoService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoService.class);
    private final EmpleadoRepository empleadoRepository;
    private final RabbitTemplate rabbitTemplate;

    public EmpleadoService(EmpleadoRepository empleadoRepository, RabbitTemplate rabbitTemplate) {
        this.empleadoRepository = empleadoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Empleado> findAll() {
        List<Empleado> list = new ArrayList<>();
        empleadoRepository.findAll().forEach(list::add);
        return list;
    }

    public Page<Empleado> findAll(Pageable pageable) {
        return empleadoRepository.findAll(pageable);
    }

    public Page<Empleado> search(String search, Pageable pageable) {
        return empleadoRepository.findBySearchTerm(search, pageable);
    }

    public Empleado findOne(Long id) {
        return empleadoRepository.findById(id).orElse(null);
    }

    public Empleado save(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public void inactivar(Long id) {
        Empleado e = empleadoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("El empleado con ID " + id + " no existe"));

        e.setEstado(Estadoempleado.INACTIVO);

        empleadoRepository.save(e);
    }

    public void activar(Long id) {
        Empleado e = empleadoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Empleado no encontrado"));

        e.setEstado(Estadoempleado.ACTIVO);

        empleadoRepository.save(e);
    }

    public long count() {
        return empleadoRepository.count();
    }
}
