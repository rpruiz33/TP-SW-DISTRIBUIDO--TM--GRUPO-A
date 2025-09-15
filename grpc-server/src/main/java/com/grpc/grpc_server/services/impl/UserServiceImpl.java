package com.grpc.grpc_server.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.services.UserService;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.grpc.grpc_server.MyServiceClass.*;
import com.grpc.grpc_server.entities.Role;
import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.mapper.UserMapper;
import com.grpc.grpc_server.repositories.RoleRepository;
import com.grpc.grpc_server.repositories.UserRepository;
import com.grpc.grpc_server.util.PasswordUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EmailService emailService;


    public List<User> getAllUsers (){

        return  userRepository.findAll();

    }

    public List<User> getActiveUsers (){

        return  userRepository.findByActivate(true);

    }

    ///---------------------------------------------------------------------------------------------------------------------

    //Libreria que permite la encriptacion de contraseñas
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        var responseBuilder = LoginResponse.newBuilder();

        userRepository.findByEmailOrUsername(request.getUsername(), request.getUsername())
            .ifPresentOrElse(user -> {
                if (passwordEncoder.matches( request.getPassword(), user.getPassword())) {
                    responseBuilder.setSuccess(true).setMessage("Login successful").setRoleName(user.getRole().getNameRole());
                } else {
                    responseBuilder.setSuccess(false).setMessage("Invalid password");
                }
            }, () -> {
                responseBuilder.setSuccess(false).setMessage("User not found");
            });

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

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
    

    public void updateUser(UpdateUsuarioRequest request, StreamObserver<AltaUsuarioResponse> responseObserver) {
    var responseBuilder = AltaUsuarioResponse.newBuilder();
    User user = userRepository.findByUsername(request.getUsername()).orElse(null);

    if (user == null) {

        responseBuilder.setSuccess(false).setMessage("Usuario no encontrado");
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();

        } else {

        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());

        Role rol = roleRepository.findByNameRole(request.getRole());

        if (rol == null) {
         responseBuilder.setSuccess(false).setMessage("Usuario no encontrado");
         responseObserver.onNext(responseBuilder.build());
         responseObserver.onCompleted();
        }

        user.setRole(rol);

        userRepository.save(user);
        responseBuilder.setSuccess(true).setMessage("Usuario actualizado");
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}


    @Transactional
    public void deleteUser(DeleteUsuarioRequest request, StreamObserver<DeleteUsuarioResponse> responseObserver) {
    var responseBuilder = DeleteUsuarioResponse.newBuilder();

    if (request == null || request.getUsername() == null || request.getUsername().isEmpty()) {
        responseBuilder.setSuccess(false).setMessage("El nombre de usuario no puede estar vacío");

        responseObserver.onCompleted();
        return;
    }

     User u = userRepository.findByUsername(request.getUsername()).orElse(null);

    if (u!= null && u.getActivate()){
        u.setActivate(false);
        userRepository.save(u);
        responseBuilder.setSuccess(true).setMessage("Usuario dado de baja");
    }else if (u!= null && u.getActivate()==false){
        u.setActivate(true);
        userRepository.save(u);
        responseBuilder.setSuccess(true).setMessage("Usuario dado de alta");
    }else {
        responseBuilder.setSuccess(false).setMessage("Usuario no encontrado");

    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}



}
