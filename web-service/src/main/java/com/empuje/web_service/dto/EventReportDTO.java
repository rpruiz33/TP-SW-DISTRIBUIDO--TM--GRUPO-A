package com.empuje.web_service.dto;

import com.empuje.web_service.entities.grpc.Event;
import com.empuje.web_service.entities.grpc.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventReportDTO {

    private Integer idEvent;
    private String nameEvent;
    private String descriptionEvent;
    private LocalDateTime dateRegistration;
    private List<UserDTO> members;

    private List<DonationAtEventDTO> donations;



    public static EventReportDTO toDTOWithRelations(Event e) {
        return new EventReportDTO(
                e.getIdEvent(),
                e.getNameEvent(),
                e.getDescriptionEvent(),
                e.getDateRegistration(),
                e.getMembers().stream().map(UserDTO::toDTO).collect(Collectors.toList()),
                e.getDonations().stream().map(DonationAtEventDTO::toDTO).collect(Collectors.toList())
        );
    }
}
