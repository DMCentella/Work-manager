package com.microservicios.notification.listener;

import com.microservicios.notification.dto.EmpleadoCesadoEvent;
import com.microservicios.notification.entity.Notificacion;
import com.microservicios.notification.service.NotificacionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoListener {

    private final NotificacionService notificacionService;
    private final SimpMessagingTemplate messagingTemplate;

    public EmpleadoListener(NotificacionService notificacionService,
                            SimpMessagingTemplate messagingTemplate) {
        this.notificacionService = notificacionService;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "empleado.cesado.queue")
    public void onEmpleadoCesado(EmpleadoCesadoEvent event) {
        Notificacion n = notificacionService.crear(event.getEmpleadoId(),
                "Empleado cesado: " + event.getNombre());
        messagingTemplate.convertAndSend("/topic/notificaciones/" + event.getEmpleadoId(), n);
        messagingTemplate.convertAndSend("/topic/dashboard", "Empleado cesado correctamente");
    }
}
