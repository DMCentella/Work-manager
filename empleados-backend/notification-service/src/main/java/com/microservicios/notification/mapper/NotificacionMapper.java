package com.microservicios.notification.mapper;

import com.microservicios.notification.dto.NotificacionResponse;
import com.microservicios.notification.entity.Notificacion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificacionMapper {

    NotificacionMapper INSTANCE = Mappers.getMapper(NotificacionMapper.class);

    NotificacionResponse toResponse(Notificacion entity);
}
