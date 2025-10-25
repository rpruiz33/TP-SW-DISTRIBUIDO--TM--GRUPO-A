package com.empuje.web_service.services.graphql.impl;

import java.util.List;
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
    public Boolean deleteDonationFilter(String filterName, String emailOrUsername) {

        String result = "";
        Boolean resBoolean = false;

        // buscar usuario en base al email
        Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);  
        
        if(userOptional.isPresent()){
            User user = userOptional.get();

            // Elimino solo filtros DONATION_REPORT del usuario

            if(userFilterRepository.deleteEventFilter(filterName, user, FilterType.DONATION_REPORT) == 1){

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

    public List<DonationFilterDTO> getListUserFiltersByEmail(String emailOrUsername){


        List<UserFilter> filters = null;
        List<DonationFilterDTO> dtos = null;
        Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);

        if(userOptional.isPresent()){

            User user = userOptional.get();
            filters =  userFilterRepository.findByUser(user);

            if(!filters.isEmpty()){
                dtos = filters.stream()
                .filter(f -> f.getFilterType() == FilterType.DONATION_REPORT)
                .map(f -> DonationFilterDTO.builder()
                        .filterName(f.getFilterName())
                        .startDate(f.getStartDate())
                        .endDate(f.getEndDate())
                        .activate(f.getActivate())
                        .category(f.getCategory())
                        .build())
                .toList();

            }else{
                System.out.println("No tiene filtro este usuario");
            }

            
        }else{
            System.out.println("No existe ese usuario");
        }
        
        return dtos;

    }

}

