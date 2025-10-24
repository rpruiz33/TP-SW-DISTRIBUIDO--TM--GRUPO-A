package com.empuje.web_service.dto;

import com.empuje.web_service.entities.grpc.Event;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private String dateRegistration;
    private List<UserDTO> members;

    private List<DonationAtEventDTO> donations;




    public static EventReportDTO toDTOWithRelations(Event e) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new EventReportDTO(
                e.getIdEvent(),
                e.getNameEvent(),
                e.getDescriptionEvent(),
                e.getDateRegistration().format(FORMATTER),
                e.getMembers().stream().map(UserDTO::toDTO).collect(Collectors.toList()),
                e.getDonations().stream().map(DonationAtEventDTO::toDTO).collect(Collectors.toList())
        );
    }
}
