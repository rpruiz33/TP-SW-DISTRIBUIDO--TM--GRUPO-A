package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.DonationsAtEvents;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DonationsAtEventsMapper {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonationEventDTO {
        private EventMapper.EventDTO event;
        private int quantityDelivered;
    }

    public static DonationEventDTO toDTO(DonationsAtEvents entity) {
        return new DonationEventDTO(
                EventMapper.toDTO(entity.getEvent()),
                entity.getQuantityDelivered()
        );
    }

    public static MyServiceClass.DonationEventProto toProto(DonationEventDTO dto) {
        return MyServiceClass.DonationEventProto.newBuilder()
                .setEvent(EventMapper.toProto(dto.getEvent()))
                .setQuantityDelivered(dto.getQuantityDelivered())
                .build();
    }
}