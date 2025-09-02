package com.grpc.grpc_server.serviceimp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import com.grpc.grpc_server.MyServiceClass.LoginRequest;
import com.grpc.grpc_server.MyServiceClass.LoginResponse;
import com.grpc.grpc_server.MyServiceClass.RegisterRequest;
import com.grpc.grpc_server.MyServiceClass.RegisterResponse;
import com.grpc.grpc_server.MyServiceGrpc;
import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.repositories.UserRepository;

import io.grpc.stub.StreamObserver;

@GrpcService
public class UserService extends MyServiceGrpc.MyServiceImplBase {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        var responseBuilder = LoginResponse.newBuilder();

        userRepository.findByEmailOrUsername(request.getUsername(), request.getUsername(), request.getPassword())
            .ifPresentOrElse(user -> {
                if (user.getPassword().equals(request.getPassword())) {
                    responseBuilder.setSuccess(true).setMessage("Login successful");
                } else {
                    responseBuilder.setSuccess(false).setMessage("Invalid password");
                }
            }, () -> {
                responseBuilder.setSuccess(false).setMessage("User not found");
            });

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
    var responseBuilder = RegisterResponse.newBuilder();

    if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
        responseBuilder.setSuccess(false).setMessage("User already exists");
    } else {
       
        if (request.getEmail().isEmpty() || request.getUsername().isEmpty() || request.getPassword().isEmpty()) {
            responseBuilder.setSuccess(false).setMessage("Email, username, and password are required");
        } else {
        
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
          
            newUser.setPassword(request.getPassword());

            userRepository.save(newUser);
            responseBuilder.setSuccess(true).setMessage("User registered successfully");
        }
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}
}
