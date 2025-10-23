package com.empuje.web_service.dto;


import com.empuje.web_service.entities.grpc.Donation;
import com.empuje.web_service.entities.grpc.DonationsAtEvents;
import lombok.*;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationAtEventDTO {


    private DonationDTO donation;
    private int quantity;

    public static DonationAtEventDTO toDTO(DonationsAtEvents entity) {
        return new DonationAtEventDTO(
                DonationDTO.toDTO(entity.getDonation()),
                entity.getQuantityDelivered()
        );
    }
}
