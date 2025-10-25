package com.empuje.web_service.services.graphql.impl;


import com.empuje.web_service.dto.UserDTO;
import com.empuje.web_service.entities.grpc.User;

import com.empuje.web_service.repositories.UserRepository;
import com.empuje.web_service.services.graphql.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    public List<UserDTO> getAllUsers(){

        List<User> users = userRepository.findAll();


        return users.stream()
                .map(UserDTO::toDTO)
                .collect(Collectors.toList());


    }

}
