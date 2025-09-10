package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------DTO-----------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventDTO {
        private int id;
        private String name;
        private String description;
        private LocalDateTime dateRegistration;


    }

    public static EventDTO toDTO(Event e) {
        return new EventDTO(
            e.getIdEvent(),
            e.getNameEvent(),
            e.getDescriptionEvent(),
            e.getDateRegistration()
        );
    }
    //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------MAPPER--------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//

    public static MyServiceClass.Event toProto(EventDTO dto) {
        return MyServiceClass.Event.newBuilder()
                .setId(dto.getId())
                .setNameEvent(dto.getName())
                .setDescriptionEvent(dto.getDescription())
                .setDateRegistration(dto.getDateRegistration().format(FORMATTER))
                .build();
    }

    public static MyServiceClass.Event toProto(Event e) {
        return toProto(toDTO(e));
    }

}
