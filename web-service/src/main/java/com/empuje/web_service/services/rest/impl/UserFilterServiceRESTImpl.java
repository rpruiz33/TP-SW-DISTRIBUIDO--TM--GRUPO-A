package com.empuje.web_service.services.rest.impl;

import com.empuje.web_service.dto.DonationFilterDTO;
import com.empuje.web_service.dto.EventFilterDTO;
import com.empuje.web_service.dto.UserDTO;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFilterServiceRESTImpl implements UserFilterServiceREST{

    private final UserFilterRepository userFilterRepository;

    private final UserRepository userRepository;

    @Transactional
    public Boolean saveEventFilter(EventFilterDTO dto, String emailOrUsername) {

        String result = "";
        Boolean resBoolean = false;

        // buscar usuario en base al email

        String clean = emailOrUsername.trim();
        System.out.println(clean);
        Optional<User> userOptional = userRepository.findByEmailOrUsername(clean, clean);
        System.out.println(emailOrUsername);
        System.out.println("Total users: " + userRepository.count());


       
        if(userOptional.isPresent()){

            User user = userOptional.get();

            if(userFilterRepository.findByFilterNameAndUserAndFilterType(dto.getFilterName(), user, FilterType.EVENT_REPORT).isEmpty()){


                System.out.println("Pre user DTO");
                //Buscamos el id del user por el cual filtrar

                Optional<User> userDTO = userRepository.findByEmailOrUsername(dto.getFilterUser().getEmail(),dto.getFilterUser().getEmail());
                System.out.println("Post user DTO");


                UserFilter filter = UserFilter.builder()
                .filterName(dto.getFilterName())
                .filterType(FilterType.EVENT_REPORT)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .filterUserId(userDTO.get().getIdUser()) //lo usamos aca el id
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

    public Boolean updateEventFilter(EventFilterDTO dto, String emailOrUsername) {

        String result = "";
        Boolean resBoolean = false;

        // buscar usuario en base al email
        String clean = emailOrUsername.trim();

        Optional<User> userOptional = userRepository.findByEmailOrUsername(clean, clean);

        if(userOptional.isPresent()){
            
            User user = userOptional.get();

            Optional<UserFilter> optionalFilter = userFilterRepository.findByFilterNameAndUserAndFilterType(dto.getFilterName(), user, FilterType.EVENT_REPORT);

            if (optionalFilter.isPresent()) {
                
                UserFilter filter = optionalFilter.get();

                //Buscamos el id del user por el cual filtrar
                Optional<User> userDTO = userRepository.findByEmailOrUsername(dto.getFilterUser().getEmail(),dto.getFilterUser().getEmail());

                // Actualizamos los campos
                filter.setStartDate(dto.getStartDate());
                filter.setEndDate(dto.getEndDate());
                filter.setFilterUserId(userDTO.get().getIdUser());
                filter.setDistributionDonations(dto.getDistributionDonations());

                // Guardamos
                userFilterRepository.save(filter);
                return true;

            } else {

                result = "el nombre del filtro no existe para ese usuario";
            }

        }else{

            result = "no existe ese email o username";
        }

        System.out.println(result);

        return resBoolean;
    }

    @Transactional
    public List<EventFilterDTO> getListByUser (String emailOrUsername){
        System.out.println(emailOrUsername);

        String clean = emailOrUsername.trim();
        System.out.println(clean);
        Optional<User> userOptional = userRepository.findByEmailOrUsername(clean, clean);

        return userFilterRepository.findByUser(userOptional.get())
                .stream()
                .map(f->{ return  new EventFilterDTO(
                        f.getFilterName(),
                        f.getStartDate(),
                        f.getEndDate(),
                        UserDTO.toDTO(userRepository.findById(f.getFilterUserId())),
                        f.getDistributionDonations()
                );
                })
                .collect(Collectors.toList());

    }




}