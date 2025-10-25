package com.empuje.web_service.controllers.graphql;


import com.empuje.web_service.dto.UserDTO;
import com.empuje.web_service.services.graphql.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserResolver {

    @Autowired
    UserServiceImpl userService;


    @QueryMapping
    public List<UserDTO> userList(){
        return userService.getAllUsers();
    }

}
