package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Donation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class DonationMapper {

    //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------DTOS-----------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//

    // =======================
    // DTO sin relaciones
    // =======================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonationDTO {
        private int id;
        private String category;
        private String description;
        private int amount;
        private boolean removed;
    }

    // =======================
    // DTO con eventos
    // =======================
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonationWithEventsDTO {
        private int id;
        private String category;
        private String description;
        private int amount;
        private boolean removed;
        private List<DonationsAtEventsMapper.DonationEventDTO> events;
    }

    // =======================
    // Entity -> DTO sin relaciones
    // =======================
    public static DonationDTO toDTO(Donation d) {
        return new DonationDTO(
                d.getIdDonation(),
                d.getCategory().name(),
                d.getDescription(),
                d.getAmount(),
                d.getRemoved()
        );
    }

    // =======================
    // Entity -> DTO con evento
    // =======================
    public static DonationWithEventsDTO toDTOWithEvents(Donation d) {
        return new DonationWithEventsDTO(
                d.getIdDonation(),
                d.getCategory().name(),
                d.getDescription(),
                d.getAmount(),
                d.getRemoved(),
                d.getEvents() != null
                        ? d.getEvents().stream()
                        .map(DonationsAtEventsMapper::withEventToDTO)
                        .collect(Collectors.toList())
                        : null
        );
    }


    //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------MAPPERS-----------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//

    // =======================
    // DTO básico -> Proto
    // =======================
    public static MyServiceClass.DonationProto toProto(DonationDTO dto) {
        return MyServiceClass.DonationProto.newBuilder()
                .setId(dto.getId())
                .setCategory(dto.getCategory())
                .setDescription(dto.getDescription())
                .setAmount(dto.getAmount())
                .setRemoved(dto.isRemoved())
                .build();
    }

    // =======================
    // DTO con evetos -> Proto con eventos
    // =======================
    public static MyServiceClass.DonationProto toProto(DonationWithEventsDTO dto) {
        MyServiceClass.DonationProto.Builder builder = MyServiceClass.DonationProto.newBuilder()
                .setId(dto.getId())
                .setCategory(dto.getCategory())
                .setDescription(dto.getDescription())
                .setAmount(dto.getAmount())
                .setRemoved(dto.isRemoved());

        if (dto.getEvents() != null) {
            builder.addAllEvents(
                    dto.getEvents().stream()
                            .map(DonationsAtEventsMapper::toProto)
                            .collect(Collectors.toList())
            );
        }

        return builder.build();
    }

    // =======================
    // Métodos de conveniencia
    // =======================
    public static MyServiceClass.DonationProto toProto(Donation d) {
        return toProto(toDTO(d)); // Básico
    }

    public static MyServiceClass.DonationProto toProtoWithEvents(Donation d) {
        return toProto(toDTOWithEvents(d)); // Con eventos
    }
}