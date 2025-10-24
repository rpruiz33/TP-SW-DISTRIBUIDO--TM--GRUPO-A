package com.empuje.web_service.controllers;


import com.empuje.web_service.dto.EventPerMonthDTO;
import com.empuje.web_service.dto.EventReportDTO;
import com.empuje.web_service.services.impl.EventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
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

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd['T'HH:mm[:ss]]") // acepta yyyy-MM-dd o yyyy-MM-ddTHH:mm o yyyy-MM-ddTHH:mm:ss
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0) // si no viene hora, pone 0
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();

        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate, formatter) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate, formatter) : null;

        return eventService.getEventPerMonthWithFilters(emailUser,start,end,withDonations);
    }
}


