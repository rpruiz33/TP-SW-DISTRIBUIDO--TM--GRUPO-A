package com.empuje.web_service.services.graphql.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.FilterType;
import com.empuje.web_service.entities.web_service.UserFilter;
import com.empuje.web_service.repositories.UserFilterRepository;
import com.empuje.web_service.repositories.UserRepository;
import com.empuje.web_service.services.graphql.UserFilterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFilterServiceImpl implements UserFilterService{

    private static final Logger logger = LoggerFactory.getLogger(UserFilterServiceImpl.class);

    private final UserFilterRepository userFilterRepository;

    private final UserRepository userRepository;

    @Override
    public Boolean saveDonationFilter(DonationFilterDTO dto, String emailOrUsername) {

        Boolean resBoolean = false;
        String result = "";

        // buscar usuario en base al email
        Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);   

        if(userOptional.isPresent()){

            User user = userOptional.get();

            if(userFilterRepository.findByFilterNameAndUserAndFilterType(dto.getFilterName(), user, FilterType.DONATION_REPORT).isEmpty()){
                UserFilter filter = UserFilter.builder()
                .filterName(dto.getFilterName())
                .filterType(FilterType.DONATION_REPORT)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .activate(dto.getActivate())
                .category(dto.getCategory())
                .user(user)
                .build();

                userFilterRepository.save(filter);

                resBoolean = true;

            }else{

                result = "el usuario ya tiene un filtro con ese nombre";
            }
            
        }else{

            result = "no existe ese email o username";
        }

        System.out.println(result);
        return resBoolean;
        
    }

    @Override
    @Transactional
    public boolean deleteDonationFilter(Integer idFilter, User user) {
        try {
            return userFilterRepository.findById(idFilter).map(f -> {
                // comprobar que el filtro pertenece al usuario
                if (f.getUser() != null && f.getUser().getIdUser() != null
                        && f.getUser().getIdUser().equals(user.getIdUser())) {
                    userFilterRepository.delete(f);
                    logger.debug("Deleted filter id={} by userId={}", idFilter, user.getIdUser());
                    return true;
                }
                logger.warn("Delete denied: filter id={} ownerId={} requestUserId={}", idFilter,
                        f.getUser() != null ? f.getUser().getIdUser() : null, user.getIdUser());
                return false;
            }).orElseGet(() -> {
                logger.warn("Delete failed: filter id={} not found", idFilter);
                return false;
            });
        } catch (Exception e) {
            logger.error("Exception while deleting filter id={}", idFilter, e);
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean updateDonationFilter(DonationFilterDTO dto, String emailOrUsername) {

        Boolean resBoolean = false;
        String result = "";

       // buscar usuario en base al email
        Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);   

        if(userOptional.isPresent()){

            User user = userOptional.get();

            Optional<UserFilter> optionalFilter = userFilterRepository.findByFilterNameAndUserAndFilterType(dto.getFilterName(), user, FilterType.DONATION_REPORT);

            if(optionalFilter.isPresent()){

                UserFilter filter = optionalFilter.get();

                // Actualizamos los campos
                filter.setStartDate(dto.getStartDate());
                filter.setEndDate(dto.getEndDate());
                filter.setActivate(dto.getActivate());
                filter.setCategory(dto.getCategory());

                userFilterRepository.save(filter);

                resBoolean = true;

            }else{

                result = "el usuario ya tiene un filtro con ese nombre";
            }
            
        }else{

            result = "no existe ese email o username";
        }

        System.out.println(result);
        return resBoolean;
    }

}

