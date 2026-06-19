package com.microservicios.auth.repositorio;

import com.microservicios.auth.entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Usuario> findByEmpleadoId(Long empleadoId);
}
