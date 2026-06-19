package com.microservicios.asistencia.mapper;

import com.microservicios.asistencia.dto.AsistenciaRequest;
import com.microservicios.asistencia.dto.AsistenciaResponse;
import com.microservicios.asistencia.entidad.Asistencia;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AsistenciaMapper {

    AsistenciaMapper INSTANCE = Mappers.getMapper(AsistenciaMapper.class);

    Asistencia toEntity(AsistenciaRequest request);

    AsistenciaResponse toResponse(Asistencia entity);
}
