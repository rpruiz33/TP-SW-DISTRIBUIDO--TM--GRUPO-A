package com.grpc.grpc_server.serviceimp;

import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.repositories.UserRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.LoginRequest;
import com.grpc.grpc_server.MyServiceClass.LoginResponse;
import com.grpc.grpc_server.MyServiceGrpc;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Optional;

@GrpcService
public class UserService extends MyServiceGrpc.MyServiceImplBase {

    @Autowired
    private UserRepository userRepository;


    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        var responseBuilder = LoginResponse.newBuilder();

        userRepository.findByEmailOrUsername(request.getUsername(), request.getUsername(), request.getPassword())            .ifPresentOrElse(user -> {
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
}
