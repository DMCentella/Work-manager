package com.microservicios.empleado.repositorio;

import com.microservicios.empleado.entidad.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Query("SELECT e FROM Empleado e WHERE "
            + "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(e.apellido) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(e.sexo) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "CAST(e.telefono AS string) LIKE CONCAT('%', :search, '%')")
    Page<Empleado> findBySearchTerm(String search, Pageable pageable);
}
