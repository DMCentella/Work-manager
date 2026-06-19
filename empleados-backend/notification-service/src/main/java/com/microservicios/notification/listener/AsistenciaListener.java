package com.microservicios.notification.listener;

import com.microservicios.notification.dto.AsistenciaRegistradaEvent;
import com.microservicios.notification.entity.Notificacion;
import com.microservicios.notification.service.NotificacionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class AsistenciaListener {

    private final NotificacionService notificacionService;
    private final SimpMessagingTemplate messagingTemplate;

    public AsistenciaListener(NotificacionService notificacionService,
                              SimpMessagingTemplate messagingTemplate) {
        this.notificacionService = notificacionService;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "asistencia.registrada.queue")
    public void onAsistenciaRegistrada(AsistenciaRegistradaEvent event) {
        if ("TARDANZA".equalsIgnoreCase(event.getEstado())) {
            Notificacion n = notificacionService.crear(event.getEmpleadoId(),
                    "Empleado con tardanza registrada");
            messagingTemplate.convertAndSend("/topic/notificaciones/" + event.getEmpleadoId(), n);
        }
    }
}
