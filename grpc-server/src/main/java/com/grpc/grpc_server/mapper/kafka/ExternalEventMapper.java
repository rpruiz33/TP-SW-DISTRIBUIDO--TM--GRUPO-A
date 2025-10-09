package com.grpc.grpc_server.mapper.kafka;

import com.grpc.grpc_server.entities.kafka.EventAdhesion;
import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExternalEventMapper {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /// ---------------------------------------------- DTOs ---------------------------------------------///
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalEventDTO {

        private String idOrganizacion;
        private String idEvento;
        private String nombre;
        private String descripcion;
        private String fecha;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelExternalEventDTO {

        private String idOrganizacion;
        private String idEvento;
    }

    ///--------------------------------------MAPEO A ENTIDADAD-------------------------------------------------///
    public static ExternalEvent toEntity(ExternalEventDTO dto) {

        ExternalEvent externalEvent = new ExternalEvent();

        externalEvent.setIdExternalEventMessage(Integer.parseInt(dto.getIdEvento().replaceAll("\\D", "")));
        externalEvent.setIdOrganization(Integer.parseInt(dto.getIdOrganizacion()));
        externalEvent.setNameExternalEvent(dto.getNombre());
        externalEvent.setDescription(dto.getDescripcion());
        externalEvent.setDateAndTime(LocalDateTime.parse(dto.getFecha(), formatter));
        externalEvent.setActive(true);
        externalEvent.setAdhesions(new ArrayList<EventAdhesion>());

        return externalEvent;
    }

}
