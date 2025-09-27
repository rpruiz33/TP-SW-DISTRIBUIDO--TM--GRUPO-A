package com.grpc.grpc_server.services.kafka.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.repositories.OperationDonationRepository;
import com.grpc.grpc_server.repositories.OperationRepository;
import com.grpc.grpc_server.services.kafka.OperationService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class OperationServiceImpl implements OperationService{
    
    @Autowired
    private OperationRepository operationRepository;

     @Autowired
    private OperationDonationRepository operationDonationRepository; 

    @Override
    public void createOperation(Operation operation) {

        
        Operation operationSaved= operationRepository.save(operation);

        //Guardar las donaciones
        if (operation.getOperationDonations() != null) {
            
            for (OperationDonation od : operation.getOperationDonations()) {
                od.setOperation(operationSaved); 
                operationDonationRepository.save(od);
            }
        }
        
    }
    
}
