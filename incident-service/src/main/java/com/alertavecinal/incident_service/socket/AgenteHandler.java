package com.alertavecinal.incident_service.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AgenteHandler implements Runnable {

    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public AgenteHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Inicializamos primero el canal de salida para serialización
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            // Registramos el canal de este agente en la lista global para poder enviarle alertas en paralelo
            SocketServerManager.agentesConectados.add(this.out);

            // Mantiene el hilo vivo escuchando por si el agente envía confirmaciones por consola
            while (true) {
                // Lógica de lectura si fuera necesaria
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            System.out.println("👮‍♂️ Un Serenazgo se ha desconectado.");
        } finally {
            if (out != null) {
                SocketServerManager.agentesConectados.remove(out);
            }
            try { socket.close(); } catch (Exception ignored) {}
        }
    }







}
