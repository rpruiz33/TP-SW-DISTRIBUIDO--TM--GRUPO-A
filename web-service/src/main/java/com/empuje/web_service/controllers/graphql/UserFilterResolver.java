package com.empuje.web_service.controllers.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.services.graphql.UserFilterService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserFilterResolver {

    private final UserFilterService userFilterService;

    @MutationMapping
    public Boolean saveDonationFilter(@Argument DonationFilterDTO input, @Argument String emailOrUsername) {

        boolean result = false;

        try {

            result = userFilterService.saveDonationFilter(input, emailOrUsername);
            return result;

        } catch (Exception e) {

            return result;
        }
        
    
    }

    @MutationMapping
    public Boolean deleteDonationFilter(@Argument String filterName, @Argument String emailOrUsername) {

        boolean result=false;

        try {

            result = userFilterService.deleteDonationFilter(filterName, emailOrUsername);
            return result;
            
        } catch (Exception e) {

            return result;
        }
    }
    

    @MutationMapping
    public Boolean updateDonationFilter(@Argument DonationFilterDTO input, @Argument String emailOrUsername) {
        
        Boolean result = false;

        try {
            
            result = userFilterService.updateDonationFilter(input, emailOrUsername);
            return result;

        } catch (Exception e) {

            return result;
        }
    }


    @QueryMapping
    public List<DonationFilterDTO> getListUserFiltersByEmail(@Argument String emailOrUsername, @Argument Boolean isExternal) {

        return userFilterService.getListUserFiltersByEmail(emailOrUsername, isExternal);
    }
}
