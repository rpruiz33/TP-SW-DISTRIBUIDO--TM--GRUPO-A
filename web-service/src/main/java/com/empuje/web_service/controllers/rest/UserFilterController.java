package com.empuje.web_service.controllers.rest;

import com.empuje.web_service.dto.EventFilterDTO;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.repositories.UserRepository;
import com.empuje.web_service.services.graphql.UserFilterService;
import com.empuje.web_service.services.rest.UserFilterServiceREST;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event-filters")
@RequiredArgsConstructor
public class UserFilterController {

    private final UserFilterServiceREST service;
    private final UserRepository userRepository;

    @PostMapping("/save")
    public Boolean saveFilter(@RequestBody EventFilterDTO dto, @RequestParam String emailOrUsername) {
        
        try {
            
            // buscar usuario en base al email
            Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);   
            User user = userOptional.get();
            service.saveEventFilter(dto, user);
            return true;

        } catch (Exception e) {
            return false;
        }
        
       
         
    }

    
}