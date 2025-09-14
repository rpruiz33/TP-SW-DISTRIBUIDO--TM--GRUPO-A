package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.entities.MemberAtEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------DTO CON RELACIONES--------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventWithRelationsDTO {
        private int id;
        private String name;
        private String description;
        private LocalDateTime dateRegistration;
        private List<UserMapper.UserDTO> users;
        private List<DonationsAtEventsMapper.EventDonationDTO> donations;

    }

    // =======================
    // Entity -> DTO básico
    // =======================
    public static EventDTO toDTO(Event e) {
        return new EventDTO(
            e.getIdEvent(),
            e.getNameEvent(),
            e.getDescriptionEvent(),
            e.getDateRegistration()
        );
    }

    // =======================
    // Entity -> DTO extendido
    // =======================

    public static EventWithRelationsDTO toDTOWithRelations(Event e) {
        return new EventWithRelationsDTO(
                e.getIdEvent(),
                e.getNameEvent(),
                e.getDescriptionEvent(),
                e.getDateRegistration(),
                e.getMembers().stream().map(MemberAtEventMapper::toUserDTO).collect(Collectors.toList()),
                e.getDonations().stream().map(DonationsAtEventsMapper::withDonationToDTO).collect(Collectors.toList())
        );
    }

    // =======================
    // DTO básico -> Proto
    // =======================
    public static MyServiceClass.EventProto toProto(EventDTO dto) {
        return MyServiceClass.EventProto.newBuilder()
                .setId(dto.getId())
                .setNameEvent(dto.getName())
                .setDescriptionEvent(dto.getDescription())
                .setDateRegistration(dto.getDateRegistration().toString())
                .build();
    }

    // =======================
    // DTO extendido -> Proto Extendido
    // =======================
    public static MyServiceClass.EventProto toProto(EventWithRelationsDTO dto) {
        MyServiceClass.EventProto.Builder builder= MyServiceClass.EventProto.newBuilder()
                                            .setId(dto.getId())
                                            .setNameEvent(dto.getName())
                                            .setDescriptionEvent(dto.getDescription())
                                            .setDateRegistration(dto.getDateRegistration().toString());

        if (dto.getUsers() != null) {
            builder.addAllUsers(
                    dto.getUsers().stream()
                            .map(UserMapper::toProto)
                            .collect(Collectors.toList())
            );
        }

        if (dto.getDonations() != null) {
            builder.addAllDonations(
                    dto.getDonations().stream()
                            .map(DonationsAtEventsMapper::toProto)
                            .collect(Collectors.toList())
            );
        }

        return builder.build();
    }

    public static MyServiceClass.EventProto toProto(Event e) {
        return toProto(toDTO(e));
    }
}
