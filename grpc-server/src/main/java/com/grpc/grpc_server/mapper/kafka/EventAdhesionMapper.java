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

public class EventAdhesionMapper {


    /// ---------------------------------------------- DTOs ---------------------------------------------///
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventAdhesionDTO {

        private String idEvento;
        private String idOrganizacion;
        private String idVoluntario;
        private String emailVoluntario;
        private String nombreVoluntario;
        private String apellidoVoluntario;
        private String telefonoVoluntario;

    }



    ///--------------------------------------MAPEO A ENTIDADAD-------------------------------------------------///
    public static EventAdhesion toEntity(EventAdhesionDTO dto, ExternalEvent event) {

        EventAdhesion eventAdhesion = new EventAdhesion();
        eventAdhesion.setIdOrganization(Integer.parseInt(dto.getIdOrganizacion()));
        eventAdhesion.setIdVolunteer(Integer.parseInt(dto.getIdVoluntario()));
        eventAdhesion.setEmailVolunteer(dto.getEmailVoluntario());
        eventAdhesion.setNameVolunteer(dto.getNombreVoluntario());
        eventAdhesion.setLastNameVolunteer(dto.getApellidoVoluntario());
        eventAdhesion.setPhoneVolunteer(dto.getTelefonoVoluntario());
        eventAdhesion.setExternalEvent(event);


        return eventAdhesion;
    }

}
