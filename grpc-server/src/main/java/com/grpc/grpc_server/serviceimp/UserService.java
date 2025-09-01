package com.grpc.grpc_server.serviceimp;

import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UserService  {

    @Autowired
    private UserRepository userRepository;


    public Optional<User> validateLogin (String login, String password){

        Optional<User> useropt = userRepository.findByEmailOrUsername(login,login,password);



        return useropt;
    }
}
