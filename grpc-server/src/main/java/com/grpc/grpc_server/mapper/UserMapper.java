package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.entities.Role;
import com.grpc.grpc_server.MyServiceClass.AltaUsuarioRequest;
import com.grpc.grpc_server.MyServiceClass.UserProto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserMapper {

     //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------DTOS-----------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//

    // =======================
    // DTO sin relaciones
    // =======================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private String username;
        private String name;
        private String lastName;
        private String phone;
        private String email;
        private String roleName;
        private boolean activate;
    }





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


    // =======================
    // Entity -> DTO
    // ======================= 
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getName(),
                user.getLastName(),
                user.getPhone(),
                user.getEmail(),
                user.getRole().getNameRole(),
                user.getActivate()
        );
    }


    //-------------------------------------------------------------------------------------------------------//
    //-----------------------------------------MAPPERS-----------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------------//
    
    // =======================
    // DTO  -> Proto
    // =======================
    public static MyServiceClass.UserProto toProto(UserDTO dto) {
        return MyServiceClass.UserProto.newBuilder()
                .setUsername(dto.getUsername())
                .setName(dto.getName())
                .setLastName(dto.getLastName())
                .setPhone(dto.getPhone())
                .setEmail(dto.getEmail())
                .setRole(dto.getRoleName())
                .setActivated(dto.isActivate())
                .build();
    }

    // =======================
    // Métodos de conveniencia
    // =======================
    public static MyServiceClass.UserProto toProto(User u) {
        return toProto(toDTO(u)); // Básico
    }


}