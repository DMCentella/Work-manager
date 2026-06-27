package com.microservicios.notification.listener;

import com.microservicios.notification.dto.TareaCreadaEvent;
import com.microservicios.notification.entity.Notificacion;
import com.microservicios.notification.service.NotificacionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class TareaListener {

    private final NotificacionService notificacionService;
    private final SimpMessagingTemplate messagingTemplate;

    public TareaListener(NotificacionService notificacionService,
                         SimpMessagingTemplate messagingTemplate) {
        this.notificacionService = notificacionService;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "tarea.creada.queue")
    public void onTareaCreada(TareaCreadaEvent event) {
        Notificacion n = notificacionService.crear(event.getEmpleadoId(),
                "Nueva tarea asignada: " + event.getDescripcion());
        messagingTemplate.convertAndSend("/topic/notificaciones/" + event.getEmpleadoId(), n);
    }

    @RabbitListener(queues = "tarea.completada.queue")
    public void onTareaCompletada(TareaCreadaEvent event) {
        System.out.println(">>> ENTRE A onTareaCompletada");
        System.out.println("Empleado: " + event.getEmpleadoId());

        Notificacion n = notificacionService.crear(
                null,
                "El empleado completó la tarea: " + event.getDescripcion());


        messagingTemplate.convertAndSend(
                "/topic/notificaciones/admin",
                n
        );
    }
}
