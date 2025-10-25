package com.empuje.web_service.dto;

import com.empuje.web_service.entities.grpc.MemberAtEvent;
import com.empuje.web_service.entities.grpc.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String fullName;
    private String roleName;
    private String email;

    public static UserDTO toDTO(MemberAtEvent m) {
         UserDTO userDTO= new UserDTO();

         userDTO.setFullName(m.getUser().getName()+ " " + m.getUser().getLastName() );
         userDTO.setRoleName(m.getUser().getRole().getNameRole());

        return userDTO;
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(
                (user.getName()+" "+user.getLastName()),
                user.getRole().getNameRole(),
                user.getEmail()

        );
    }
}