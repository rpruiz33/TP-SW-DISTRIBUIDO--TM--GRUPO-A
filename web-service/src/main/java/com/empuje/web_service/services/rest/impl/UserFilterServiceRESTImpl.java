package com.empuje.web_service.services.rest.impl;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.dto.EventFilterDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.entities.web_service.FilterType;
import com.empuje.web_service.entities.web_service.UserFilter;
import com.empuje.web_service.repositories.UserFilterRepository;
import com.empuje.web_service.repositories.UserRepository;
import com.empuje.web_service.services.graphql.UserFilterService;
import com.empuje.web_service.services.rest.UserFilterServiceREST;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFilterServiceRESTImpl implements UserFilterServiceREST{

    private final UserFilterRepository userFilterRepository;

    private final UserRepository userRepository;

    public Boolean saveEventFilter(EventFilterDTO dto, String emailOrUsername) {

        String result = "";
        Boolean resBoolean = false;

        // buscar usuario en base al email
        Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);   
        

        System.out.println("HOLAAAAAAAAAAAAAAA");
       
        if(userOptional.isPresent()){

            User user = userOptional.get();

            if(userFilterRepository.findByFilterNameAndUserAndFilterType(dto.getFilterName(), user, FilterType.EVENT_REPORT).isEmpty()){
                UserFilter filter = UserFilter.builder()
                .filterName(dto.getFilterName())
                .filterType(FilterType.EVENT_REPORT)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .filterUserId(dto.getFilterUserId())
                .distributionDonations(dto.getDistributionDonations())
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

    @Transactional
    public Boolean deleteEventFilter(String filterName, String emailOrUsername){

        String result = "";
        Boolean resBoolean = false;

        // buscar usuario en base al email
        Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);  
        
        if(userOptional.isPresent()){
            User user = userOptional.get();

            System.out.println("ID USER");
            System.out.println(user.getIdUser());
            System.out.println(filterName);
            System.out.println(emailOrUsername);

            // Eliminás solo filtros EVENT_REPORT del usuario

            if(userFilterRepository.deleteEventFilter(filterName, user, FilterType.EVENT_REPORT) == 1){
                resBoolean = true;
            }else{
                result = "el nombre del filtro no existe para ese usuario";
            }
          
        }else{

            result = "no existe ese email o username";
        }

        System.out.println(result);
        
        return resBoolean;
        
    }
}