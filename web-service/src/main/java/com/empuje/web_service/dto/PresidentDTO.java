package com.empuje.web_service.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresidentDTO {

    private long id;
    private String name;
    private String address;
    private String phone;
    private long organizationId;
}
