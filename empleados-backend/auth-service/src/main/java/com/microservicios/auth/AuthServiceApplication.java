package com.microservicios.auth;

import com.microservicios.auth.entidad.Rol;
import com.microservicios.auth.entidad.Usuario;
import com.microservicios.auth.repositorio.RolRepository;
import com.microservicios.auth.repositorio.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner initAdmin(UsuarioRepository usuarioRepo,
                                        RolRepository rolRepo,
                                        PasswordEncoder encoder) {
        return args -> {
            Rol rolAdmin = rolRepo.findByNombre("ROLE_ADMIN").orElseGet(() ->
                    rolRepo.save(new Rol("ROLE_ADMIN")));
            Rol rolUser = rolRepo.findByNombre("ROLE_USER").orElseGet(() ->
                    rolRepo.save(new Rol("ROLE_USER")));

            Usuario u = usuarioRepo.findByUsername("admin").orElse(null);
            if (u == null) {
                u = new Usuario();
                u.setUsername("admin");
            }
            u.setPassword(encoder.encode("12345"));
            u.setEnabled(true);
            u.getRoles().clear();
            u.getRoles().add(rolAdmin);
            usuarioRepo.save(u);
            System.out.println("ADMIN listo: admin / 12345");
        };
    }
}
