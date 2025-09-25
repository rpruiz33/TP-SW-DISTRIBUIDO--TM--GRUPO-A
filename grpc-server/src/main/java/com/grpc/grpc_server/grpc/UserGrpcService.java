package com.grpc.grpc_server.grpc;

import java.util.List;
import java.util.stream.Collectors;

import com.grpc.grpc_server.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.LoginResponse;
import com.grpc.grpc_server.MyServiceGrpc;
import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.mapper.UserMapper;
import com.grpc.grpc_server.services.impl.UserServiceImpl;
import org.springframework.grpc.server.service.GrpcService;
import io.grpc.stub.StreamObserver;


@Slf4j
@GrpcService

public class UserGrpcService extends MyServiceGrpc.MyServiceImplBase {

    @Autowired
    UserServiceImpl userService;


   
    @Override
    public void getAllUsers(MyServiceClass.Empty request, StreamObserver<MyServiceClass.UserListResponse> responseObserver){
        
        // 1️⃣ Obtener entidades desde la capa service
        List<User> users = userService.getAllUsers();

        // 2️⃣ Mapear a Proto usando Mapper
        List<MyServiceClass.UserProto> grpcUsers = users.stream()
                .map(UserMapper::toProto)
                .collect(Collectors.toList());

        // 3️⃣ Construir y enviar la respuesta
        MyServiceClass.UserListResponse response = MyServiceClass.UserListResponse.newBuilder()
                .addAllUsers(grpcUsers)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getActiveUsers(MyServiceClass.Empty request, StreamObserver<MyServiceClass.UserListResponse> responseObserver){

        // 1️⃣ Obtener entidades desde la capa service
        List<User> users = userService.getActiveUsers();

        // 2️⃣ Mapear a Proto usando Mapper
        List<MyServiceClass.UserProto> grpcUsers = users.stream()
                .map(UserMapper::toProto)
                .collect(Collectors.toList());

        // 3️⃣ Construir y enviar la respuesta
        MyServiceClass.UserListResponse response = MyServiceClass.UserListResponse.newBuilder()
                .addAllUsers(grpcUsers)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }



    @Override
    public void login(MyServiceClass.LoginRequest request, StreamObserver<LoginResponse> responseObserver) {

        String token = userService.login(request);

        var responseBuilder = MyServiceClass.LoginResponse.newBuilder();

        String message = JwtUtil.getMessage(token);
        String role = JwtUtil.getRole(token);

        switch (message) {
            case "Credenciales invalidas":
            case "Usuario no encontrado":
                responseBuilder.setSuccess(false).setMessage(message);
                break;

            case "Usuario Valido":
                //Construimos el mensaje y enviamos el token
                responseBuilder.setSuccess(true).setMessage(message).setRoleName(role).setToken(token);
                break;

            default:
                responseBuilder.setSuccess(false).setMessage("Error en el back");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }


    @Override
    public void altaUser(MyServiceClass.AltaUsuarioRequest request, StreamObserver<MyServiceClass.AltaUsuarioResponse> responseObserver) {
    String result = userService.altaUser(request);

    var responseBuilder = MyServiceClass.AltaUsuarioResponse.newBuilder();

        switch (result) {
            case "Usuario creado con éxito":
                responseBuilder.setSuccess(true).setMessage(result);
                break;

            case "Email o username ya registrado":
            case "Rol inválido":
            case "Datos incompletos":
            case "Usuario creado pero error al enviar email ":
            default:
                responseBuilder.setSuccess(false).setMessage(result);
                break;
        }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}


    @Override
    public void updateUser(MyServiceClass.UpdateUsuarioRequest request, StreamObserver<MyServiceClass.AltaUsuarioResponse> responseObserver){

        log.debug("LLEGAMOS AL UPDATE con: " + request.getAllFields());
        String result = userService.updateUser(request);
        log.debug("Salimos con el resultado: "+ result);
        var responseBuilder = MyServiceClass.AltaUsuarioResponse.newBuilder();

        switch (result) {
            case "Usuario modificado con éxito":
                responseBuilder.setSuccess(true).setMessage(result);
                break;

            case "Datos incompletos":
            case "Usuario no encontrado":
            case "Rol inválido":
            case "Email ya está siendo utilizado":
            case "Username ya está siendo utilizado":

            default:
                responseBuilder.setSuccess(false).setMessage(result);
                break;
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

     @Override
    public void deleteUser(MyServiceClass.DeleteUsuarioRequest request,
                           StreamObserver<MyServiceClass.DeleteUsuarioResponse> responseObserver) {

        String result = userService.deleteUser(request);

        var responseBuilder = MyServiceClass.DeleteUsuarioResponse.newBuilder();

        switch (result) {
            case "Usuario dado de baja":
            case "Usuario dado de alta":
                responseBuilder.setSuccess(true).setMessage(result);
                break;

            case "Error datos inválidos":
            case "Error usuario no encontrado":
            default:
                responseBuilder.setSuccess(false).setMessage(result);
                break;
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}



