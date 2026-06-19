package com.microservicios.empleado.controlador;

import com.microservicios.empleado.dto.EmpleadoRequest;
import com.microservicios.empleado.dto.EmpleadoResponse;
import com.microservicios.empleado.entidad.Empleado;
import com.microservicios.empleado.mapper.EmpleadoMapper;
import com.microservicios.empleado.servicio.EmpleadoService;
import com.microservicios.empleado.util.reportes.EmpleadoExporterPDF;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final EmpleadoMapper empleadoMapper;

    public EmpleadoController(EmpleadoService empleadoService, EmpleadoMapper empleadoMapper) {
        this.empleadoService = empleadoService;
        this.empleadoMapper = empleadoMapper;
    }

    @GetMapping
    public ResponseEntity<Page<EmpleadoResponse>> listar(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "search", required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Empleado> result = (search != null && !search.trim().isEmpty())
                ? empleadoService.search(search.trim(), pageable)
                : empleadoService.findAll(pageable);
        return ResponseEntity.ok(result.map(empleadoMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> obtener(@PathVariable Long id) {
        Empleado e = empleadoService.findOne(id);
        return e != null
                ? ResponseEntity.ok(empleadoMapper.toResponse(e))
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponse> crear(@Valid @RequestBody EmpleadoRequest request) {
        Empleado entity = empleadoMapper.toEntity(request);
        Empleado saved = empleadoService.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody EmpleadoRequest request) {
        Empleado existente = empleadoService.findOne(id);
        if (existente == null) return ResponseEntity.notFound().build();
        empleadoMapper.updateEntity(request, existente);
        Empleado saved = empleadoService.save(existente);
        return ResponseEntity.ok(empleadoMapper.toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        empleadoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/pdf")
    public void exportarPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        response.setHeader("Content-Disposition", "attachment; filename=empleados_" + fecha + ".pdf");
        new EmpleadoExporterPDF(empleadoService.findAll()).exportar(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(empleadoService.count());
    }

    @GetMapping("/departamentos")
    public ResponseEntity<Map<String, Long>> porDepartamento() {
        Map<String, Long> deptos = new HashMap<>();
        for (Empleado e : empleadoService.findAll()) {
            deptos.merge(e.getDepartamento(), 1L, Long::sum);
        }
        return ResponseEntity.ok(deptos);
    }

    @GetMapping("/sexos")
    public ResponseEntity<Map<String, Long>> porSexo() {
        Map<String, Long> sexos = new HashMap<>();
        for (Empleado e : empleadoService.findAll()) {
            sexos.merge(e.getSexo(), 1L, Long::sum);
        }
        return ResponseEntity.ok(sexos);
    }
}
