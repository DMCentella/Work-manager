package com.alertavecinal.incident_service.socket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SocketServerManager implements CommandLineRunner {

    private static final int PORT = 9090; // Puerto independiente al HTTP (8082)

    // Lista concurrente para almacenar los flujos de salida de todos los Serenazgos conectados
    protected static final List<java.io.ObjectOutputStream> agentesConectados = new CopyOnWriteArrayList<>();

    // Pool de hilos para manejar múltiples conexiones de serenazgos de forma paralela
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public void run(String... args) throws Exception {
        // Ejecutamos el servidor de sockets en un hilo independiente
        new Thread(this::iniciarServidorSockets).start();
    }

    private void iniciarServidorSockets() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🚀 Servidor de Sockets TCP escuchando en el puerto " + PORT);

            while (true) {
                Socket socketCliente = serverSocket.accept(); // Bloqueante: Espera a que un Serenazgo se conecte por consola
                System.out.println("👮‍♂️ Nuevo Serenazgo conectado desde: " + socketCliente.getRemoteSocketAddress());

                // Asignamos un hilo del Pool para atender los mensajes de este serenazgo de forma concurrente
                threadPool.execute(new AgenteHandler(socketCliente));
            }
        } catch (Exception e) {
            System.err.println("Error en el Servidor de Sockets: " + e.getMessage());
        }
    }


}
