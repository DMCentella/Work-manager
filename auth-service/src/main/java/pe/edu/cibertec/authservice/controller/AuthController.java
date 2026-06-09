package pe.edu.cibertec.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.authservice.dto.AuthResponse;
import pe.edu.cibertec.authservice.dto.ErrorMessage;
import pe.edu.cibertec.authservice.dto.GenericResponseDto;
import pe.edu.cibertec.authservice.dto.LoginRequest;
import pe.edu.cibertec.authservice.dto.RegisterRequest;
import pe.edu.cibertec.authservice.service.AuthService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponseDto<AuthResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(GenericResponseDto.<AuthResponse>builder()
                    .response(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GenericResponseDto.<AuthResponse>builder()
                            .error(ErrorMessage.builder()
                                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                                    .dateError(java.time.LocalDate.now())
                                    .message("Credenciales incorrectas")
                                    .build())
                            .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponseDto<AuthResponse>> registerUser(@RequestBody RegisterRequest signUpRequest) {
        try {
            AuthResponse response = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(GenericResponseDto.<AuthResponse>builder()
                    .response(response)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(GenericResponseDto.<AuthResponse>builder()
                            .error(ErrorMessage.builder()
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .dateError(java.time.LocalDate.now())
                                    .message(e.getMessage())
                                    .build())
                            .build());
        }
    }
}
