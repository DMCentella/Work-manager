package com.alertavecinal.serenazgo.client;

import com.alertavecinal.serenazgo.config.FeignConfig;
import com.alertavecinal.serenazgo.dto.GenericResponseDto;
import com.alertavecinal.serenazgo.dto.IncidenteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "incident-service",
        url = "${incident.service.url}",
        configuration = FeignConfig.class
)
public interface IncidentClient {

    @GetMapping("/api/incidents")
    GenericResponseDto<List<IncidenteDTO>>
    listarIncidentes();

    @GetMapping("/api/incidents/{id}")
    GenericResponseDto<IncidenteDTO>
    obtenerIncidente(
            @PathVariable Long id
    );

    @PutMapping("/api/incidents/{id}")
    GenericResponseDto<IncidenteDTO>
    actualizarIncidente(
            @PathVariable Long id,
            @RequestBody IncidenteDTO incidente
    );
}