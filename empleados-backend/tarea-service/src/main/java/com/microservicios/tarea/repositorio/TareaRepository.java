package com.microservicios.tarea.repositorio;

import com.microservicios.tarea.entidad.TareaEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TareaRepository extends JpaRepository<TareaEmpleado, Long> {
    List<TareaEmpleado> findByEmpleadoIdOrderByFechaAsignacionDesc(Long empleadoId);
    List<TareaEmpleado> findByEmpleadoIdAndCompletadaFalse(Long empleadoId);
}
