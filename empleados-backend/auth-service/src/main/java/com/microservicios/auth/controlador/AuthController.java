package com.microservicios.auth.controlador;

import com.microservicios.auth.dto.LoginRequest;
import com.microservicios.auth.dto.LoginResponse;
import com.microservicios.auth.dto.MessageResponse;
import com.microservicios.auth.dto.RegisterRequest;
import com.microservicios.auth.entidad.Usuario;
import com.microservicios.auth.repositorio.UsuarioRepository;
import com.microservicios.auth.servicio.UsuarioService;
import com.microservicios.common.jwt.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioService usuarioService,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", usuario.getId().toString());
        if (usuario.getEmpleadoId() != null) {
            extraClaims.put("empleadoId", usuario.getEmpleadoId().toString());
        }

        String token = JwtUtil.generateToken(request.getUsername(), roles, extraClaims);
        return ResponseEntity.ok(new LoginResponse(token, request.getUsername(), roles));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("El nombre de usuario ya existe"));
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(request.getPassword());
        usuario.setEmpleadoId(request.getEmpleadoId());
        usuarioService.registrar(usuario);
        return ResponseEntity.ok(new MessageResponse("Usuario registrado correctamente"));
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validate(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            JwtUtil.validateToken(token);
            return ResponseEntity.ok(Map.of("valid", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}
