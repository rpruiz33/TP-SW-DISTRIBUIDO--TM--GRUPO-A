package com.empuje.web_service.services.rest;

import com.empuje.web_service.dto.EventFilterDTO;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.FilterType;
import com.empuje.web_service.entities.web_service.UserFilter;

import java.util.List;

public interface UserFilterServiceREST {

    Boolean saveEventFilter(EventFilterDTO dto, String emailOrUsername);

    Boolean deleteEventFilter(String filterName, String emailOrUsername);

    Boolean updateEventFilter(EventFilterDTO dto, String emailOrUsername);

    List<EventFilterDTO> getListByUser (String emailOrUsername);


}
