package com.alertavecinal.serenazgo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.alertavecinal.serenazgo.client.IncidentClient;
import com.alertavecinal.serenazgo.dto.CambioEstadoRequest;
import com.alertavecinal.serenazgo.dto.GenericResponseDto;
import com.alertavecinal.serenazgo.dto.HistorialEstadoResponse;
import com.alertavecinal.serenazgo.dto.IncidenteDTO;
import com.alertavecinal.serenazgo.enums.EstadoIncidente;
import com.alertavecinal.serenazgo.model.HistorialEstado;
import com.alertavecinal.serenazgo.repository.HistorialEstadoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SerenazgoService {

    private final IncidentClient incidentClient;
    private final HistorialEstadoRepository historialEstadoRepository;

    public List<IncidenteDTO> listarIncidentes() {

        GenericResponseDto<List<IncidenteDTO>> response =
                incidentClient.listarIncidentes();

        return response.getResponse();
    }

    public IncidenteDTO obtenerIncidente(Long id) {

        GenericResponseDto<IncidenteDTO> response =
                incidentClient.obtenerIncidente(id);

        return response.getResponse();
    }

    public IncidenteDTO cambiarEstado(
            Long incidenteId,
            Long serenazgoId,
            CambioEstadoRequest request) {

        IncidenteDTO incidente =
                incidentClient
                        .obtenerIncidente(incidenteId)
                        .getResponse();

        EstadoIncidente estadoAnterior =
                EstadoIncidente.valueOf(
                        incidente.getEstado()
                );

        incidente.setEstado(
                request.getNuevoEstado().name()
        );

        IncidenteDTO incidenteActualizado =
                incidentClient
                        .actualizarIncidente(
                                incidenteId,
                                incidente
                        )
                        .getResponse();

        HistorialEstado historial =
                new HistorialEstado();

        historial.setIncidenteId(
                incidenteId
        );

        historial.setSerenazgoId(
                serenazgoId
        );

        historial.setEstadoAnterior(
                estadoAnterior
        );

        historial.setEstadoNuevo(
                request.getNuevoEstado()
        );

        historial.setFechaCambio(
                LocalDateTime.now()
        );

        historialEstadoRepository.save(
                historial
        );

        return incidenteActualizado;
    }

    public List<HistorialEstadoResponse>
    obtenerHistorial(Long incidenteId) {

        return historialEstadoRepository
                .findByIncidenteId(
                        incidenteId
                )
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    private HistorialEstadoResponse convertirResponse(
            HistorialEstado historial) {

        HistorialEstadoResponse response =
                new HistorialEstadoResponse();

        response.setId(
                historial.getId()
        );

        response.setIncidenteId(
                historial.getIncidenteId()
        );

        response.setSerenazgoId(
                historial.getSerenazgoId()
        );

        response.setEstadoAnterior(
                historial.getEstadoAnterior()
        );

        response.setEstadoNuevo(
                historial.getEstadoNuevo()
        );

        response.setFechaCambio(
                historial.getFechaCambio()
        );

        return response;
    }
}