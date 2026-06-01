package com.alertavecinal.incident_service.service;

import com.alertavecinal.incident_service.entity.EstadoIncidente;
import com.alertavecinal.incident_service.entity.Incidente;
import com.alertavecinal.incident_service.repository.IncidenteRepository;
import com.alertavecinal.incident_service.socket.NotificadorAlerta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IncidenteService {

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private NotificadorAlerta notificadorAlerta;

    // 1. POST: Crear Incidente (Usado por Ciudadano)
    @Transactional
    public Incidente registrarIncidente(Incidente incidente) {
        // Guardamos de forma síncrona en MySQL
        Incidente incidenteGuardado = incidenteRepository.save(incidente);

        // DISPARO EN TIEMPO REAL: Enviamos el objeto serializado a los Sockets concurrentes
        // Se envuelve en un try-catch para que un fallo en la notificación no revierta la transacción de la DB.
        try {
            notificadorAlerta.notificarAgentesEnTiempoReal(incidenteGuardado);
        } catch (Exception e) {
            System.err.println("Error al notificar a los agentes en tiempo real: " + e.getMessage());
            // Opcional: Podrías loggear el error con un logger más robusto (ej. SLF4J)
            // y/o implementar una lógica de reintento o compensación si la notificación es crítica.
        }

        return incidenteGuardado;
    }

    // 2. GET: Listar todo (Usado por Serenazgo y Administrador)
    @Transactional(readOnly = true)
    public List<Incidente> obtenerTodos() {
        return incidenteRepository.findAll();
    }

    // 3. GET: Buscar por ID (Para ver detalles o seguimiento del Ciudadano)
    @Transactional(readOnly = true)
    public Optional<Incidente> obtenerPorId(Long id) {
        return incidenteRepository.findById(id);
    }

    // 4. PUT: Cambiar estado y agregar comentario (Usado por el Serenazgo)
    @Transactional
    public Optional<Incidente> actualizarEstadoYComentario(Long id, EstadoIncidente nuevoEstado, String comentario) {
        return incidenteRepository.findById(id).map(incidente -> {
            incidente.setEstado(nuevoEstado);
            if (comentario != null && !comentario.trim().isEmpty()) {
                incidente.setComentarioSerenazgo(comentario);
            }
            return incidenteRepository.save(incidente);
        });
    }

    // 5. DELETE: Eliminar incidente (Usado exclusivamente por el Administrador para purgar falsos)
    @Transactional
    public boolean eliminarIncidente(Long id) {
        if (incidenteRepository.existsById(id)) {
            incidenteRepository.deleteById(id);
            return true;
        }
        return false;
    }













}