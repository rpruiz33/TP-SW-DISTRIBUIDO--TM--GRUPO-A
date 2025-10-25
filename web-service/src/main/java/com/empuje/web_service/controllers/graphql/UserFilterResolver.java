package com.empuje.web_service.controllers.graphql;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.UserFilter;
import com.empuje.web_service.repositories.UserFilterRepository;
import com.empuje.web_service.repositories.UserRepository;
import com.empuje.web_service.services.graphql.UserFilterService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserFilterResolver {

    private static final Logger logger = LoggerFactory.getLogger(UserFilterResolver.class);

    private final UserFilterService userFilterService;
    private final UserRepository userRepository;
    private final UserFilterRepository userFilterRepository;

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
    public Boolean deleteDonationFilter(@Argument Integer idFilter, @Argument String emailOrUsername) {

        try {

            // buscar usuario en base al email
            Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);
            User user = userOptional.get();
            return userFilterService.deleteDonationFilter(idFilter, user);
        } catch (Exception e) {

            return false;
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
    public List<DonationFilterDTO> getListUserFiltersByEmail(@Argument String emailOrUsername) {

        return userFilterService.getListUserFiltersByEmail(emailOrUsername);
    }
}
