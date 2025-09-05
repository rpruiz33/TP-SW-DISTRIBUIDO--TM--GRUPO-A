package com.grpc.grpc_server.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.grpc.grpc_server.MyServiceClass.AltaUsuarioRequest;
import com.grpc.grpc_server.MyServiceClass.AltaUsuarioResponse;
import com.grpc.grpc_server.MyServiceClass.Empty;
import com.grpc.grpc_server.MyServiceClass.LoginRequest;
import com.grpc.grpc_server.MyServiceClass.LoginResponse;
import com.grpc.grpc_server.MyServiceClass.UserListResponse;
import com.grpc.grpc_server.MyServiceGrpc;
import com.grpc.grpc_server.entities.Role;
import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.mapper.UserMapper;
import com.grpc.grpc_server.repositories.RoleRepository;
import com.grpc.grpc_server.repositories.UserRepository;
import com.grpc.grpc_server.util.PasswordUtils;

import io.grpc.stub.StreamObserver;

@GrpcService
public class UserService extends MyServiceGrpc.MyServiceImplBase {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EmailService emailService;



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
        if (request.getEmail().isEmpty() || request.getUsername().isEmpty() || 
            request.getName().isEmpty() || request.getLastName().isEmpty() || 
            request.getRole().isEmpty()) {

            responseBuilder.setSuccess(false).setMessage("Faltan completar campos");
        } else {
            Role rol = roleRepository.findByNameRole(request.getRole());

            if (rol == null) {
                responseBuilder.setSuccess(false);
                responseBuilder.setMessage("Rol no encontrado: " + request.getRole());
                responseObserver.onNext(responseBuilder.build());
                responseObserver.onCompleted();
                return;
            }

            User newUser = UserMapper.toEntity(request, rol);

            String passPlana = PasswordUtils.generateRandomPassword();
            newUser.setPassword(PasswordUtils.encryptPassword(passPlana));

            userRepository.save(newUser);

            // 🚀 Enviar mail con la contraseña
            try {
                emailService.sendEmail(
                    newUser.getEmail(),
                    "Registro exitoso",
                    "Hola " + newUser.getName() + ",\n\nTu usuario fue creado exitosamente.\n" +
                    "Tu contraseña es: " + passPlana + "\n\n."
                );
                responseBuilder.setSuccess(true).setMessage("User registered successfully. Email sent.");
            } catch (Exception e) {
                responseBuilder.setSuccess(true).setMessage("User registered, but failed to send email: " + e.getMessage());
            }
        }
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
    }
     @Override
     public void getAllUsers (Empty response, StreamObserver<UserListResponse> responseObserver){

             List<User> lstUser = userRepository.findAll();

             UserListResponse ul = UserListResponse.newBuilder()
                     .addAllUsers(lstUser.stream().map(x -> UserMapper.toDTO(x))
                             .collect(Collectors.toList())).build();
             responseObserver.onNext(ul);
             responseObserver.onCompleted();

     }
}
