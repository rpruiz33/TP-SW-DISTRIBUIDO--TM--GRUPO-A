package com.empuje.web_service.dto;


import java.util.List;

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

    private List<DonationDetailDTO> details; // los registros individuales


    // Constructor específico para JPQL
    public DonationReportDTO(Category category, Boolean activate, long totalQuantity) {
        this.category = category;
        this.activate = activate;
        this.totalQuantity = totalQuantity;
    }
}