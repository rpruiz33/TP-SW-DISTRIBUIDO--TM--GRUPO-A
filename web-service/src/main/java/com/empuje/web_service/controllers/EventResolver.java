package com.empuje.web_service.controllers;


import com.empuje.web_service.dto.EventPerMonthDTO;
import com.empuje.web_service.dto.EventReportDTO;
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
    public List<EventReportDTO> eventReportAll() {
        return eventService.getAllEventWithRelations();
    }

    @QueryMapping
    public List<EventPerMonthDTO> eventReport(
            @Argument String emailUser,
            @Argument String startDate,
            @Argument String endDate,
            @Argument String withDonations)
    {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;

        return eventService.getEventPerMonthWithFilters(emailUser,start,end,withDonations);
    }
}


