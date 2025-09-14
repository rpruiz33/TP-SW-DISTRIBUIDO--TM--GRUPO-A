package com.grpc.grpc_server.services;

import java.util.List;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Role;
import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.mapper.UserMapper;
import com.grpc.grpc_server.util.PasswordUtils;
import io.grpc.stub.StreamObserver;

public interface UserService {



    List<User> getAllUsers();
    ///---------------------------------------------------------------------------------------------------------------------

    void login(MyServiceClass.LoginRequest request, StreamObserver<MyServiceClass.LoginResponse> responseObserver);
    void altaUser(MyServiceClass.AltaUsuarioRequest request, StreamObserver<MyServiceClass.AltaUsuarioResponse> responseObserver);
    void updateUser(MyServiceClass.UpdateUsuarioRequest request, StreamObserver<MyServiceClass.AltaUsuarioResponse> responseObserver);
    void deleteUser(MyServiceClass.DeleteUsuarioRequest request, StreamObserver<MyServiceClass.DeleteUsuarioResponse> responseObserver);
}
