package com.microservicios.empleado.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class EmpleadoRequest {
    @NotEmpty
    private String nombre;

    @NotEmpty
    private String apellido;

    @NotEmpty @Email
    private String email;

    @NotEmpty
    @Pattern(regexp = "^[0-9]{9}$", message = "El telefono debe tener 9 digitos")
    private String telefono;

    @NotEmpty
    private String sexo;

    @NotNull
    private double salario;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha;

    @NotEmpty
    private String cargo;

    @NotEmpty
    private String departamento;
}
