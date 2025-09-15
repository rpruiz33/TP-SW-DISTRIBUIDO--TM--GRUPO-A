package com.grpc.grpc_server.grpc;

import com.grpc.grpc_server.DonationServiceGrpc;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceGrpc;
import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.mapper.EventMapper;
import com.grpc.grpc_server.mapper.UserMapper;
import com.grpc.grpc_server.services.impl.UserServiceImpl;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import com.grpc.grpc_server.MyServiceClass.*;



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
    public void login(MyServiceClass.LoginRequest request, StreamObserver<LoginResponse> responseObserver){
        userService.login(request,responseObserver);
    }

    @Override
    public void altaUser(MyServiceClass.AltaUsuarioRequest request, StreamObserver<MyServiceClass.AltaUsuarioResponse> responseObserver){
        userService.altaUser(request,responseObserver);
    }

    @Override
    public void updateUser(MyServiceClass.UpdateUsuarioRequest request, StreamObserver<MyServiceClass.AltaUsuarioResponse> responseObserver){
        userService.updateUser(request,responseObserver);
    }

    @Override
    public void deleteUser(MyServiceClass.DeleteUsuarioRequest request, StreamObserver<MyServiceClass.DeleteUsuarioResponse> responseObserver){
        userService.deleteUser(request,responseObserver);
    }

}
