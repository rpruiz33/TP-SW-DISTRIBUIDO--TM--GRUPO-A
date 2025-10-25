package com.empuje.web_service.controllers.graphql;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.UserFilter;
import com.empuje.web_service.repositories.UserRepository;
import com.empuje.web_service.services.graphql.UserFilterService;
import com.empuje.web_service.services.graphql.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserFilterResolver {

    private final UserFilterService userFilterService;
    private final UserRepository userRepository;

    @MutationMapping
    public Boolean saveDonationFilter(@Argument DonationFilterDTO input, @Argument String emailOrUsername) {

        try {

            // buscar usuario en base al email
            Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);   
            User user = userOptional.get();
            userFilterService.saveDonationFilter(input, user);

            return true;
        } catch (Exception e) {

            return false;
        }
        
    
    }
}
