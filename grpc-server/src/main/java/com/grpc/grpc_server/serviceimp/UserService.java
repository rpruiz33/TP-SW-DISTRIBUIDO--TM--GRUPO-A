package com.grpc.grpc_server.serviceimp;

import com.grpc.grpc_server.entities.Role;
import com.grpc.grpc_server.repositories.RoleRepository;
import com.grpc.grpc_server.util.PasswordUtils;

import org.checkerframework.checker.units.qual.A;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import com.grpc.grpc_server.MyServiceClass.LoginRequest;
import com.grpc.grpc_server.MyServiceClass.LoginResponse;
import com.grpc.grpc_server.MyServiceClass.AltaUsuarioResponse;
import com.grpc.grpc_server.MyServiceClass.AltaUsuarioRequest;
import com.grpc.grpc_server.MyServiceGrpc;
import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.repositories.UserRepository;

import io.grpc.stub.StreamObserver;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@GrpcService
public class UserService extends MyServiceGrpc.MyServiceImplBase {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;



    //Libreria que permite la encriptacion de contraseñas
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        var responseBuilder = LoginResponse.newBuilder();

        userRepository.findByEmailOrUsername(request.getUsername(), request.getUsername())
            .ifPresentOrElse(user -> {
                if (passwordEncoder.matches( request.getPassword(), user.getPassword())) {
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
    public void altaUser(AltaUsuarioRequest request, StreamObserver<AltaUsuarioResponse> responseObserver) {
        var responseBuilder = AltaUsuarioResponse.newBuilder();

        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
            responseBuilder.setSuccess(false).setMessage("User already exists");
        } else {

            if (request.getEmail().isEmpty() || request.getUsername().isEmpty() || request.getName().isEmpty() || request.getLastName().isEmpty() || request.getRole().isEmpty()   ) {
                responseBuilder.setSuccess(false).setMessage("Faltan completar campos");
            } else {

                User newUser = new User();
                newUser.setUsername(request.getUsername());
                newUser.setEmail(request.getEmail());
                newUser.setName(request.getName());
                newUser.setLastName(request.getLastName());
                newUser.setPhone(request.getPhone());

                Role rol = roleRepository.findByNameRole(request.getRole());


                if (rol == null) {
                    responseBuilder.setSuccess(false);
                    responseBuilder.setMessage("Rol no encontrado: " + request.getRole());
                    responseObserver.onNext(responseBuilder.build());
                    responseObserver.onCompleted();
                    return;
                }
                newUser.setRole(rol);

                String passPlana = PasswordUtils.generateRandomPassword();
                //LOGICA PARA ENVIAR POR MAIL LA PASSWORD GENERADA

                newUser.setPassword(PasswordUtils.encryptPassword(passPlana));

                userRepository.save(newUser);

                responseBuilder.setSuccess(true).setMessage("User registered successfully");
            }
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
     }
}
