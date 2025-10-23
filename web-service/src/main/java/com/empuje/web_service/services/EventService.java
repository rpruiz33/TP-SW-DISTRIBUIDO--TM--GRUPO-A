package com.empuje.web_service.services;

import com.empuje.web_service.dto.EventReportDTO;
import com.empuje.web_service.entities.grpc.Event;

import java.util.List;

public interface EventService {


    List<EventReportDTO> getAllEventWithRelations();
}
