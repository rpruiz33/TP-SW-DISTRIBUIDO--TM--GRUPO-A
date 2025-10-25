package com.empuje.web_service.services.graphql;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.entities.grpc.User;

public interface UserFilterService {

    Boolean saveDonationFilter(DonationFilterDTO dto, String emailOrUsername);

    boolean deleteDonationFilter(Integer idFilter, User user);


    Boolean updateDonationFilter(DonationFilterDTO dto, String emailOrUsername);
    
}
