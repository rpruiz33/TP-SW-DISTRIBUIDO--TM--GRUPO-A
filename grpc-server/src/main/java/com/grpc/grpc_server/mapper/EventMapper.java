package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------DTO-----------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//

    public static class EventDTO {
        private int id;
        private String name;
        private String description;
        private LocalDateTime dateRegistration;

        public EventDTO(int id, String name, String description, LocalDateTime dateRegistration) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.dateRegistration = dateRegistration;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public LocalDateTime getDateRegistration() { return dateRegistration; }
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
