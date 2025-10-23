package com.empuje.web_service.controllers;


import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.dto.EventReportDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.services.EventService;
import com.empuje.web_service.services.OperationDonationService;
import com.empuje.web_service.services.impl.EventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class EventResolver {

    @Autowired
    private EventServiceImpl eventService;


    @QueryMapping
    public List<EventReportDTO> eventReport() {
        return eventService.getAllEventWithRelations();
    }
}


