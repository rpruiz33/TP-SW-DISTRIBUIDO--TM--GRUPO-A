package com.empuje.web_service.services;

import java.time.LocalDateTime;
import java.util.List;

import com.empuje.web_service.entities.Operation;
import com.empuje.web_service.mappers.DonationReportMapper.DonationReportDTO;

public interface OperationService {

    List<Operation> getAll();

    /* 
    List<DonationReportDTO> getDonationReport(
            String category,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean removed); */
} 
