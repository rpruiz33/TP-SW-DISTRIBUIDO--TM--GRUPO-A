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
    List<User> getActiveUsers();

    ///---------------------------------------------------------------------------------------------------------------------

    String login(MyServiceClass.LoginRequest request);
    boolean altaUser(MyServiceClass.AltaUsuarioRequest request);
    boolean updateUser(MyServiceClass.UpdateUsuarioRequest request);
    String deleteUser(MyServiceClass.DeleteUsuarioRequest request);
}
