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

    // =======================
    // DTO básico
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



    // Convierte la entidad user en un DTO intermedio | Entity -> DTO
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().getNameRole(),
                user.getActivate()
        );
    }
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