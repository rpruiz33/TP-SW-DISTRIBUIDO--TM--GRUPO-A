package com.empuje.web_service.services.graphql;

import java.util.List;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.dto.EventFilterDTO;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.UserFilter;

public interface UserFilterService {

    Boolean saveDonationFilter(DonationFilterDTO dto, String emailOrUsername);

    Boolean deleteDonationFilter(String filterName, String emailOrUsername);

    Boolean updateDonationFilter(DonationFilterDTO dto, String emailOrUsername);

    List<DonationFilterDTO> getListUserFiltersByEmail(String emailOrUsername);
    
}
