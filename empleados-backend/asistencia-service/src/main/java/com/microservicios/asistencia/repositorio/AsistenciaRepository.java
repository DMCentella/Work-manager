package com.microservicios.asistencia.repositorio;

import com.microservicios.asistencia.entidad.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByEmpleadoId(Long empleadoId);
    List<Asistencia> findByFecha(LocalDate fecha);
    long countByFecha(LocalDate fecha);
}
