package com.empuje.web_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFilterDTO {

    private String filterName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int filterUserId;        // Usuario participante
    private Boolean distributionDonations; // true = sí, false = no, null = ambos
    
}