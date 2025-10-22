package com.empuje.web_service.dto;


import com.empuje.web_service.entities.grpc.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationReportDTO {
    
    private Category category;
    private Boolean  activate;
    private long totalQuantity;
}