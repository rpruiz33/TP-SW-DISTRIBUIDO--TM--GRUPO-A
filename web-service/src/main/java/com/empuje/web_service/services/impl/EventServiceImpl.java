package com.empuje.web_service.services.impl;

import com.empuje.web_service.dto.EventReportDTO;
import com.empuje.web_service.entities.grpc.Event;
import com.empuje.web_service.repositories.EventRepository;
import com.empuje.web_service.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    EventRepository eventRepository;


    public List<EventReportDTO> getAllEventWithRelations() {
        List<Event> events = eventRepository.findAllWithRelations();

        return events.stream()
                .map(EventReportDTO::toDTOWithRelations)
                .collect(Collectors.toList());
    }
}
