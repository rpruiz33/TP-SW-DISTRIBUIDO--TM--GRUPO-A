package com.empuje.web_service.services.rest;

import java.util.List;

import com.empuje.web_service.dto.EventFilterDTO;

public interface UserFilterServiceREST {

    Boolean saveEventFilter(EventFilterDTO dto, String emailOrUsername);

    Boolean deleteEventFilter(String filterName, String emailOrUsername);

    // originalFilterName optional: if provided, will be used to find existing filter to rename
    Boolean updateEventFilter(EventFilterDTO dto, String emailOrUsername, String originalFilterName);

    List<EventFilterDTO> getListByUser (String emailOrUsername);


}
