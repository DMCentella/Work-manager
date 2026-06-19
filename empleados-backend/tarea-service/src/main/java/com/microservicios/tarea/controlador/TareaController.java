package com.microservicios.tarea.controlador;

import com.microservicios.tarea.dto.TareaRequest;
import com.microservicios.tarea.dto.TareaResponse;
import com.microservicios.tarea.entidad.TareaEmpleado;
import com.microservicios.tarea.mapper.TareaMapper;
import com.microservicios.tarea.servicio.TareaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService tareaService;
    private final TareaMapper tareaMapper;

    public TareaController(TareaService tareaService, TareaMapper tareaMapper) {
        this.tareaService = tareaService;
        this.tareaMapper = tareaMapper;
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<TareaResponse>> porEmpleado(@PathVariable Long empleadoId) {
        List<TareaResponse> list = tareaService.porEmpleado(empleadoId).stream()
                .map(tareaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/empleado/{empleadoId}/pendientes")
    public ResponseEntity<List<TareaResponse>> pendientes(@PathVariable Long empleadoId) {
        List<TareaResponse> list = tareaService.pendientesPorEmpleado(empleadoId).stream()
                .map(tareaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaResponse> obtener(@PathVariable Long id) {
        TareaEmpleado t = tareaService.findOne(id);
        return t != null
                ? ResponseEntity.ok(tareaMapper.toResponse(t))
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest request) {
        TareaEmpleado entity = tareaMapper.toEntity(request);
        TareaEmpleado saved = tareaService.guardar(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaMapper.toResponse(saved));
    }

    @PutMapping("/{id}/completar")
    public ResponseEntity<TareaResponse> completar(@PathVariable Long id) {
        TareaEmpleado t = tareaService.completar(id);
        return t != null
                ? ResponseEntity.ok(tareaMapper.toResponse(t))
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tareaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
