package com.empuje.web_service.services.graphql;

import com.empuje.web_service.dto.EventPerMonthDTO;
import com.empuje.web_service.dto.EventReportDTO;


import java.time.LocalDateTime;
import java.util.List;

public interface EventService {


    List<EventReportDTO> getAllEventWithRelations();
    List<EventPerMonthDTO> getEventPerMonthWithFilters(String emailUser, LocalDateTime startDate, LocalDateTime endDate,String withDonations);

}
