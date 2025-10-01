package com.grpc.grpc_server.services.kafka.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.TransferMapper;
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
    @Autowired
    private TransferMapper tranMapper;
    @Autowired
    private ObjectMapper objectMapper;

    

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

     @Override
    public void processTransfer(String message) {
        try {
            log.info("📩 Mensaje recibido (TRANSFERENCIA): {}", message);

            int operationId = 2;

            // Buscar o crear operación
            Operation operation = operationRepository.findById(operationId)
                    .orElseGet(() -> {
                        Operation op = new Operation();
                        op.setIdOperationMessage((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
                        op.setIdOrganization(1);
                        op.setOperationType(OperationType.TRANSFERENCIA);
                        op.setActivate(true);
                        op.setDateRegistration(LocalDateTime.now());
                        op.setDateModification(LocalDateTime.now());
                        return operationRepository.save(op);
                    });
            List<OperationDonation> donations = objectMapper.readValue(
                    message,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, OperationDonation.class)
            );
                    

            for (OperationDonation donation : donations) {
                donation.setOperation(operation);
                operationDonationRepository.save(donation);}

            log.info("✅ Transferencias guardadas en la base de datos.");

        } catch (Exception e) {
            log.error("❌ Error procesando transferencia", e);
        }
    }

    
}
