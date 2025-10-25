package com.empuje.web_service.dto;

import java.time.LocalDateTime;
import com.empuje.web_service.entities.grpc.Category;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationFilterDTO {

    private String filterName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean activate;
    private Category category;
    
}
