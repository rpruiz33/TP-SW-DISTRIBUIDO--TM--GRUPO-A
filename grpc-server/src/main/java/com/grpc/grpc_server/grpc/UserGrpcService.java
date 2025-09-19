package com.grpc.grpc_server.grpc;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.LoginResponse;
import com.grpc.grpc_server.MyServiceGrpc;
import com.grpc.grpc_server.entities.User;
<<<<<<< HEAD
=======
import com.grpc.grpc_server.exceptions.InvalidDataException;
import com.grpc.grpc_server.exceptions.UserNotFoundException;
>>>>>>> 5d600cba2bab5b3dc6e8687fc4f3550adb1d12b3
import com.grpc.grpc_server.mapper.UserMapper;
import com.grpc.grpc_server.services.impl.UserServiceImpl;

import io.grpc.stub.StreamObserver;



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
        String result = userService.login(request);

        var response = MyServiceClass.LoginResponse.newBuilder();

        if (!result.isEmpty() && !"User not found".equals(result)) {
            //  caso exitoso → tiene un rol válido
            response.setSuccess(true).setMessage("Login Completado").setRoleName(result);
        } else if ("User not found".equals(result)) {
            //  usuario no existe
            response.setSuccess(false).setMessage("Usuario no encontrado");
        } else {
            //  contraseña incorrecta
            response.setSuccess(false).setMessage("Credenciales incorrectas");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }


    @Override
    public void altaUser(MyServiceClass.AltaUsuarioRequest request, StreamObserver<MyServiceClass.AltaUsuarioResponse> responseObserver) {
    String result = userService.altaUser(request);

    var responseBuilder = MyServiceClass.AltaUsuarioResponse.newBuilder();

    if (result.equals("Usuario creado con éxito") || result.equals("Usuario creado pero error al enviar email")) {
        responseBuilder.setSuccess(true).setMessage(result);
    } else {
        responseBuilder.setSuccess(false).setMessage(result);
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}


    @Override
    public void updateUser(MyServiceClass.UpdateUsuarioRequest request, StreamObserver<MyServiceClass.AltaUsuarioResponse> responseObserver){

        String result = userService.updateUser(request);

    var responseBuilder = MyServiceClass.AltaUsuarioResponse.newBuilder();

    if (result.equals("Usuario modificado con éxito")) {
        responseBuilder.setSuccess(true).setMessage(result);
    } else {
        responseBuilder.setSuccess(false).setMessage(result);
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}

    @Override
    public void deleteUser(MyServiceClass.DeleteUsuarioRequest request,
                        StreamObserver<MyServiceClass.DeleteUsuarioResponse> responseObserver) {

        try {
            String result = userService.deleteUser(request);

            var response = MyServiceClass.DeleteUsuarioResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage(result)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (InvalidDataException e) {
            responseObserver.onError(
                    io.grpc.Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (UserNotFoundException e) {
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    io.grpc.Status.INTERNAL
                            .withDescription("Error interno en el servidor")
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }


}
