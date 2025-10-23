package com.empuje.web_service.services;

import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.entities.grpc.Category;

import java.time.LocalDateTime;
import java.util.List;


public interface OperationDonationService {

    List<DonationReportDTO> getDonationReport(
        Category category,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean activate,
        Boolean isExternal
    );
} 
