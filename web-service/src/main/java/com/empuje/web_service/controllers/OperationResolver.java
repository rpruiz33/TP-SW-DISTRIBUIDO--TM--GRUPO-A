// src/main/java/com/ejemplo/controllers/OperationResolver.java
package com.empuje.web_service.controllers;


import com.empuje.web_service.entities.kafka.Operation;
import com.empuje.web_service.services.OperationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class OperationResolver {

    @Autowired
    private OperationService operationService;

    @QueryMapping
    public List<Operation> operations() {
        return operationService.getAll();
    }
}

