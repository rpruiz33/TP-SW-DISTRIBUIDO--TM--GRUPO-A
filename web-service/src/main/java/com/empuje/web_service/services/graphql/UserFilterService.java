package com.empuje.web_service.services.graphql;

import java.util.List;

import com.empuje.web_service.dto.DonationFilterDTO;

public interface UserFilterService {

    Boolean saveDonationFilter(DonationFilterDTO dto, String emailOrUsername);

    Boolean deleteDonationFilter(String filterName, String emailOrUsername);

    // originalFilterName: optional. If provided, the service will look up the existing
    // filter by that name and then rename it to dto.filterName if different.
    Boolean updateDonationFilter(DonationFilterDTO dto, String emailOrUsername, String originalFilterName);

    List<DonationFilterDTO> getListUserFiltersByEmail(String emailOrUsername, Boolean isExternal);
    
}
