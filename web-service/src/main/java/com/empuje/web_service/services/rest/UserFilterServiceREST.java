package com.empuje.web_service.services.rest;

import com.empuje.web_service.dto.EventFilterDTO;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.FilterType;
import com.empuje.web_service.entities.web_service.UserFilter;

public interface UserFilterServiceREST {

    void saveEventFilter(EventFilterDTO dto, User user);
    
}
