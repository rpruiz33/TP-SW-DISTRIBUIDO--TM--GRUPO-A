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

    public static UserDTO toDTO(MemberAtEvent m) {
        return new UserDTO(
                (m.getUser().getName()+m.getUser().getLastName()),
                m.getUser().getRole().getNameRole()
        );
    }
}