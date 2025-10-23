package com.empuje.web_service.dto;


import com.empuje.web_service.entities.grpc.Donation;
import lombok.*;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationDTO {

    private String category;
    private String description;


    public static DonationDTO toDTO(Donation d) {
        return new DonationDTO(
                d.getCategory().name(),
                d.getDescription()
        );
    }
}
