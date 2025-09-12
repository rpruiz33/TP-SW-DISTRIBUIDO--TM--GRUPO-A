package com.grpc.grpc_server.grpc;

import com.grpc.grpc_server.DonationServiceGrpc;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceGrpc;
import com.grpc.grpc_server.services.impl.UserServiceImpl;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import com.grpc.grpc_server.MyServiceClass.*;



@GrpcService
public class UserGrpcService extends MyServiceGrpc.MyServiceImplBase {

    @Autowired
    UserServiceImpl userService;
    @Override
    public void login(MyServiceClass.LoginRequest request, StreamObserver<LoginResponse> responseObserver){
        userService.login(request,responseObserver);
    }

    @Override
    public void altaUser(MyServiceClass.AltaUsuarioRequest request, StreamObserver<MyServiceClass.AltaUsuarioResponse> responseObserver){
        userService.altaUser(request,responseObserver);
    }

    @Override
    public void getAllUsers(MyServiceClass.Empty request, StreamObserver<MyServiceClass.UserListResponse> responseObserver){
        userService.getAllUsers(request,responseObserver);
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
