package com.empuje.web_service.services.graphql;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.entities.grpc.User;

public interface UserFilterService {

    void saveDonationFilter(DonationFilterDTO dto, User user);
    boolean deleteDonationFilter(Integer idFilter, User user);
    boolean updateDonationFilter(Integer idFilter, DonationFilterDTO dto, User user);
    
}
