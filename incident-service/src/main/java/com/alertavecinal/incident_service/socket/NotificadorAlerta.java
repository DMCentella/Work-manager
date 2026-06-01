package com.alertavecinal.incident_service.socket;

import com.alertavecinal.incident_service.entity.Incidente;
import org.springframework.stereotype.Component;

import java.io.ObjectOutputStream;



@Component
public class NotificadorAlerta {

    public void notificarAgentesEnTiempoReal(Incidente nuevoIncidente) {
        // Usamos streams paralelos de Java para recorrer los hilos de los agentes concurrentemente
        SocketServerManager.agentesConectados.parallelStream().forEach(outAgent -> {
            try {
                // Serialización del objeto incidente y envío inmediato por la red de bajo nivel
                outAgent.writeObject(nuevoIncidente);
                outAgent.flush();
            } catch (Exception e) {
                System.err.println("Error enviando objeto serializado a un agente: " + e.getMessage());
            }
        });
    }












}
