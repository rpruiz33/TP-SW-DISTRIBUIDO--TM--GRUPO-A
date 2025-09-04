package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.entities.Role;
import com.grpc.grpc_server.MyServiceClass.AltaUsuarioRequest;
import com.grpc.grpc_server.MyServiceClass.UserDTO;

public class UserMapper {

    // Convierte el request en una entidad User | Request (gRPC) -> Entity
    public static User toEntity(AltaUsuarioRequest request, Role role) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setActivate(true);
        return user;
    }

    // Convierte la entidad user en un DTO para la respuesta gRPC| Entity -> DTO
    public static UserDTO toDTO(User user) {
        return UserDTO.newBuilder()
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPhone(user.getPhone())
                .setRole(user.getRole().getNameRole())
                .setActivated(user.getActivate())
                .build();
    }

    // GENERAR METODO QUE PERMITA CONVERTIR A DTO UN USER QUE YA TENGA UN ID DEFINIDO
    //EN CASO DE QUERER HACER UNA MODIFICACION DE ALGUNO DE SUS ATRIBUTOS.

}