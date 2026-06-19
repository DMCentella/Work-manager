package com.microservicios.asistencia.controlador;

import com.microservicios.asistencia.dto.AsistenciaRequest;
import com.microservicios.asistencia.dto.AsistenciaResponse;
import com.microservicios.asistencia.mapper.AsistenciaMapper;
import com.microservicios.asistencia.servicio.AsistenciaService;
import com.microservicios.asistencia.util.reportes.AsistenciaExporterPDF;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final AsistenciaMapper asistenciaMapper;

    public AsistenciaController(AsistenciaService asistenciaService, AsistenciaMapper asistenciaMapper) {
        this.asistenciaService = asistenciaService;
        this.asistenciaMapper = asistenciaMapper;
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<AsistenciaResponse>> porEmpleado(@PathVariable Long empleadoId) {
        List<AsistenciaResponse> list = asistenciaService.porEmpleado(empleadoId).stream()
                .map(asistenciaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaResponse> obtener(@PathVariable Long id) {
        var a = asistenciaService.buscar(id);
        return a != null
                ? ResponseEntity.ok(asistenciaMapper.toResponse(a))
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<AsistenciaResponse> registrar(@Valid @RequestBody AsistenciaRequest request) {
        var entity = asistenciaMapper.toEntity(request);
        var saved = asistenciaService.guardar(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(asistenciaMapper.toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/hoy")
    public ResponseEntity<Map<String, Long>> countHoy() {
        return ResponseEntity.ok(Map.of("count", asistenciaService.countHoy()));
    }

    @GetMapping("/export/pdf/empleado/{empleadoId}")
    public void exportarPDF(@PathVariable Long empleadoId,
                            @RequestParam("nombreEmpleado") String nombreEmpleado,
                            HttpServletResponse response) throws IOException {
        var asistencias = asistenciaService.porEmpleado(empleadoId);
        response.setContentType("application/pdf");
        String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        response.setHeader("Content-Disposition",
                "attachment; filename=asistencias_" + empleadoId + "_" + fecha + ".pdf");
        new AsistenciaExporterPDF(asistencias, nombreEmpleado).exportar(response);
    }
}
