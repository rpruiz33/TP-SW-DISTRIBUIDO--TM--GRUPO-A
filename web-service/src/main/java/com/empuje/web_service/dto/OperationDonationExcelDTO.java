package com.empuje.web_service.dto;

import java.time.LocalDateTime;

import com.empuje.web_service.entities.grpc.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationDonationExcelDTO {
    
    private Category category;
    private String description;
    private int quantity;
    private Boolean activate;
    private LocalDateTime dateRegistration;
}
