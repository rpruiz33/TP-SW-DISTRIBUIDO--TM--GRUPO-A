package com.empuje.web_service.services.graphql.impl;

import com.empuje.web_service.entities.kafka.Operation;
import com.empuje.web_service.repositories.OperationRepository;
import com.empuje.web_service.services.graphql.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OperationServiceImpl implements OperationService{
    
    @Autowired  
    private OperationRepository operationRepository;

    @Override
    public List<Operation> getAll() {
        return operationRepository.findAll();
    }

        
}
