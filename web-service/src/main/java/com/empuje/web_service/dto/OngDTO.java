package com.empuje.web_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OngDTO {

    private long id;
    private String name;
    private String address;
    private String phone;
}
