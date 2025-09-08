package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Donation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DonationMapper {

    //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------DTO-----------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//
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

    //-------------------------------------------------------------------------------------------------------//
    //--------------------------------------Entity -> DTO----------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//

    public static DonationDTO toDTO(Donation d) {
        return new DonationDTO(
                d.getIdDonation(),
                d.getCategory() != null ? d.getCategory().name() : "", // Enum a String
                d.getDescription() != null ? d.getDescription() : "",
                d.getAmount() != null ? d.getAmount() : 0,
                d.getRemoved() != null && d.getRemoved()
        );
    }

    //-------------------------------------------------------------------------------------------------------//
    //--------------------------------------DTO -> Proto----------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//

    public static MyServiceClass.DonationProto toProto(DonationDTO dto) {
        return MyServiceClass.DonationProto.newBuilder()
                .setId(dto.getId())
                .setCategory(dto.getCategory())
                .setDescription(dto.getDescription())
                .setAmount(dto.getAmount())
                .setRemoved(dto.isRemoved())
                .build();
    }

    public static MyServiceClass.DonationProto toProto(Donation d) {
        return toProto(toDTO(d));
    }
}
