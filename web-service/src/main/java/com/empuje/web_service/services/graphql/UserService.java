package com.empuje.web_service.services.graphql;

import com.empuje.web_service.dto.UserDTO;

import java.util.List;

public interface UserService {



    List<UserDTO> getAllUsers();
}
