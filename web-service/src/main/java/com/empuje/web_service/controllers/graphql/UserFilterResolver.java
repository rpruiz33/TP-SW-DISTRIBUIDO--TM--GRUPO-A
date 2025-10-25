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
    /*NO ANDA */
    /* 
    @MutationMapping
    public Boolean updateDonationFilter(@Argument Integer idFilter, @Argument DonationFilterDTO input, @Argument String emailOrUsername) {
        logger.debug("updateDonationFilter called idFilter={} emailOrUsername={} input={}", idFilter, emailOrUsername, input);

        try {
            // buscar usuario en base al email
            Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);
            if (userOptional.isEmpty()) {
                logger.warn("User not found for emailOrUsername={}", emailOrUsername);
                return false;
            }

            User user = userOptional.get();
            boolean result = userFilterService.updateDonationFilter(idFilter, input, user);
            logger.debug("updateDonationFilter result={} for idFilter={} userId={}", result, idFilter, user.getIdUser());
            return result;
        } catch (Exception e) {
            logger.error("Exception in updateDonationFilter", e);
            return false;
        }
    }
*/
    @QueryMapping
    public List<UserFilter> getUserFiltersByEmail(@Argument String emailOrUsername) {
        try {
            return userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername)
                    .map((com.empuje.web_service.entities.grpc.User u) -> userFilterRepository.findByUser_IdUser(u.getIdUser()))
                    .orElse(Collections.emptyList());
        } catch (Exception e) {
            logger.error("Error fetching user filters for {}", emailOrUsername, e);
            return Collections.emptyList();
        }
    }
}
