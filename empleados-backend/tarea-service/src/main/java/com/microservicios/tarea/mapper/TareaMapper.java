package com.microservicios.tarea.mapper;

import com.microservicios.tarea.dto.TareaRequest;
import com.microservicios.tarea.dto.TareaResponse;
import com.microservicios.tarea.entidad.TareaEmpleado;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TareaMapper {

    TareaMapper INSTANCE = Mappers.getMapper(TareaMapper.class);

    TareaEmpleado toEntity(TareaRequest request);

    TareaResponse toResponse(TareaEmpleado entity);
}
