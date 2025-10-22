package com.empuje.web_service.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.services.OperationDonationService;

@Controller
public class OperationDonationResolver {

    private final OperationDonationService service;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public OperationDonationResolver(OperationDonationService service) {
        this.service = service;
    }

    @QueryMapping
    public List<DonationReportDTO> donationReport(
        @Argument Category category,
        @Argument String startDate,
        @Argument String endDate,
        @Argument Boolean activate
    ) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate, formatter) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate, formatter) : null;

        return service.getDonationReport(category, start, end, activate);
    }
}
