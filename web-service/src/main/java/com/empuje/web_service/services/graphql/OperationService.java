package com.empuje.web_service.services.graphql;

import com.empuje.web_service.entities.kafka.Operation;

import java.util.List;


public interface OperationService {

    List<Operation> getAll();

} 
