package com.microservicios.empleado.mapper;

import com.microservicios.empleado.dto.EmpleadoRequest;
import com.microservicios.empleado.dto.EmpleadoResponse;
import com.microservicios.empleado.entidad.Empleado;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmpleadoMapper {

    EmpleadoMapper INSTANCE = Mappers.getMapper(EmpleadoMapper.class);

    Empleado toEntity(EmpleadoRequest request);

    EmpleadoResponse toResponse(Empleado entity);

    void updateEntity(EmpleadoRequest request, @MappingTarget Empleado entity);
}
