package com.microservicios.notification.controller;

import com.microservicios.notification.dto.NotificacionResponse;
import com.microservicios.notification.entity.Notificacion;
import com.microservicios.notification.mapper.NotificacionMapper;
import com.microservicios.notification.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificationController {

    private final NotificacionService notificacionService;
    private final NotificacionMapper notificacionMapper;

    public NotificationController(NotificacionService notificacionService,
                                  NotificacionMapper notificacionMapper) {
        this.notificacionService = notificacionService;
        this.notificacionMapper = notificacionMapper;
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<NotificacionResponse>> porEmpleado(@PathVariable Long empleadoId) {
        List<NotificacionResponse> list = notificacionService.porEmpleado(empleadoId).stream()
                .map(notificacionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/empleado/{empleadoId}/no-leidas")
    public ResponseEntity<Map<String, Long>> noLeidas(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(Map.of("count", notificacionService.noLeidas(empleadoId)));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponse> marcarLeida(@PathVariable Long id) {
        Notificacion n = notificacionService.marcarLeida(id);
        return n != null
                ? ResponseEntity.ok(notificacionMapper.toResponse(n))
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/empleado/{empleadoId}/leer-todas")
    public ResponseEntity<Void> marcarTodasLeidas(@PathVariable Long empleadoId) {
        notificacionService.marcarTodasLeidas(empleadoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponse>> todas() {
        List<NotificacionResponse> list = notificacionService.todas().stream()
                .map(notificacionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }
}
