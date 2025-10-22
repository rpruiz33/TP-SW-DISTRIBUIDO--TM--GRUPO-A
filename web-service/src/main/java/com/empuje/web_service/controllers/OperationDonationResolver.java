package com.empuje.web_service.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.entities.Category;
import com.empuje.web_service.services.OperationDonationService;

@Controller
public class OperationDonationResolver {

    private final OperationDonationService service;

    public OperationDonationResolver(OperationDonationService service) {
        this.service = service;
    }

    @QueryMapping
    public List<DonationReportDTO> donationReport(
        
        @Argument Category category,
        @Argument LocalDateTime startDate,
        @Argument LocalDateTime endDate,
        @Argument Boolean activate
    ) {
        return service.getDonationReport(category, startDate, endDate, activate);
    }
}

