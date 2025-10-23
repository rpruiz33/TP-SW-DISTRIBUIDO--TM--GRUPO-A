package com.empuje.web_service.controllers;

import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.services.OperationDonationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        @Argument Boolean activate,
        @Argument Boolean isExternal
    ) {
        return service.getDonationReport(category, startDate, endDate, activate, isExternal);
    }
}
