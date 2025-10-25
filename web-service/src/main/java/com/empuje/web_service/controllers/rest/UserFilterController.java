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

    @PostMapping("/save")
    public Boolean saveFilter(@RequestBody EventFilterDTO dto, @RequestParam String emailOrUsername) {
        
        boolean result = false;
        
        try {
            
            result = service.saveEventFilter(dto, emailOrUsername);
            return result;

        } catch (Exception e){     

            return result;
        }       
         
    }

    @DeleteMapping("/delete")
    public Boolean deleteFilter(@RequestParam String filterName, @RequestParam String emailOrUsername) {
        
        Boolean result = false;

        try {
            
            result = service.deleteEventFilter(filterName, emailOrUsername);

            return result;

        } catch (Exception e) {

            return result;
        }       
         
    }

    @PutMapping("/update")
    public Boolean updateFilter(@RequestBody EventFilterDTO dto, @RequestParam String emailOrUsername) {
        
        boolean result = false;
        
        try {
            
            result = service.updateEventFilter(dto, emailOrUsername);
            return result;

        } catch (Exception e){     

            return result;
        } 
        
    }

    
}