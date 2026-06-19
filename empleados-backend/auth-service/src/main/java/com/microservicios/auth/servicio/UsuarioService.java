package com.microservicios.auth.servicio;

import com.microservicios.auth.entidad.Rol;
import com.microservicios.auth.entidad.Usuario;
import com.microservicios.auth.repositorio.RolRepository;
import com.microservicios.auth.repositorio.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Rol rolUser = rolRepository.findByNombre("ROLE_USER")
                .orElseGet(() -> rolRepository.save(new Rol("ROLE_USER")));
        usuario.setRoles(Collections.singleton(rolUser));
        usuario.setEnabled(true);
        return usuarioRepository.save(usuario);
    }
}
