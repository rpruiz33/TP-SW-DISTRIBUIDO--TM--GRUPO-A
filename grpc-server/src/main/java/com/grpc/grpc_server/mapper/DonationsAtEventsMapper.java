package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.DonationsAtEvents;
import com.grpc.grpc_server.entities.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DonationsAtEventsMapper {


    //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------DTOS-----------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonationEventDTO {
        private EventMapper.EventDTO event;
        private int quantityDelivered;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventDonationDTO {
        private DonationMapper.DonationDTO donationDTO;
        private int quantityDelivered;
    }

    
    public static DonationEventDTO withEventToDTO(DonationsAtEvents entity) {
        return new DonationEventDTO(
                EventMapper.toDTO(entity.getEvent()),
                entity.getQuantityDelivered()
        );
    }

    public static EventDonationDTO withDonationToDTO(DonationsAtEvents entity) {
        return new EventDonationDTO(
                DonationMapper.toDTO(entity.getDonation()),
                entity.getQuantityDelivered()
        );
    }


    //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------MAPPERS-----------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//
    public static MyServiceClass.DonationEventProto toProto(DonationEventDTO dto) {
        return MyServiceClass.DonationEventProto.newBuilder()
                .setEvent(EventMapper.toProto(dto.getEvent()))
                .setQuantityDelivered(dto.getQuantityDelivered())
                .build();
    }

    public static MyServiceClass.EventDonationProto toProto(EventDonationDTO dto) {
        return MyServiceClass.EventDonationProto.newBuilder()
                .setDonation(DonationMapper.toProto(dto.getDonationDTO()))
                .setQuantityDelivered(dto.getQuantityDelivered())
                .build();
    }

    

}