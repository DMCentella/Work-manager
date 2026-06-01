package com.alertavecinal.incident_service.controller;

import com.alertavecinal.incident_service.entity.EstadoIncidente;
import com.alertavecinal.incident_service.entity.Incidente;
import com.alertavecinal.incident_service.service.IncidenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidentes")
public class IncidenteController {

    @Autowired
    private IncidenteService incidenteService;

    // POST: http://localhost:8082/api/incidentes (Reportar incidente)
    @PostMapping
    public ResponseEntity<Incidente> crearIncidente(@RequestBody Incidente incidente) {

        Incidente nuevoIncidente = incidenteService.registrarIncidente(incidente);
        return new ResponseEntity<>(nuevoIncidente, HttpStatus.CREATED);
    }

    // GET: http://localhost:8082/api/incidentes (Ver listado global)
    @GetMapping
    public ResponseEntity<List<Incidente>> listarIncidentes() {
        return ResponseEntity.ok(incidenteService.obtenerTodos());
    }

    // GET: http://localhost:8082/api/incidentes/{id} (Seguimiento del ciudadano)
    @GetMapping("/{id}")
    public ResponseEntity<Incidente> buscarPorId(@PathVariable Long id) {
        return incidenteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: http://localhost:8082/api/incidentes/{id}/atencion (Acción del Serenazgo)
    @PutMapping("/{id}/atencion")
    public ResponseEntity<?> atenderIncidente(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        try {
            EstadoIncidente estado = EstadoIncidente.valueOf(payload.get("estado").toUpperCase());
            String comentario = payload.get("comentario");

            return incidenteService.actualizarEstadoYComentario(id, estado, comentario)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Estado inválido. Valores permitidos: PENDIENTE, EN_PROCESO, ATENDIDO, ANULADO");
        }
    }

    // DELETE: http://localhost:8082/api/incidentes/{id} (Acción del Administrador)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarIncidente(@PathVariable Long id) {
        if (incidenteService.eliminarIncidente(id)) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content (Éxito)
        }
        return ResponseEntity.notFound().build();
    }


}