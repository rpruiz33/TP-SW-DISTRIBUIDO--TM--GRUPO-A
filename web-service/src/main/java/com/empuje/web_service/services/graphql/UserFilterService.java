package com.empuje.web_service.services.graphql;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.UserFilter;

public interface UserFilterService {

    void saveDonationFilter(DonationFilterDTO dto, User user);
    
}
