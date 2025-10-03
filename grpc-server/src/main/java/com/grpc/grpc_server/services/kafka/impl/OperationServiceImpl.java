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
    if (operation == null) {
        throw new IllegalArgumentException("La operación no puede ser nula");
    }
    if (operation.getOperationType() == null) {
        throw new IllegalArgumentException("El tipo de operación es obligatorio");
    }
    if (operation.getIdOrganization() <= 0) {
        throw new IllegalArgumentException("La organización es obligatoria y debe ser válida");
    }

    // fechas
    if (operation.getDateRegistration() == null) {
        operation.setDateRegistration(LocalDateTime.now());
    }
    operation.setDateModification(LocalDateTime.now());

    // guardar operación
    Operation operationSaved = operationRepository.save(operation);

    // guardar donaciones asociadas
    if (operation.getOperationDonations() != null) {
        for (OperationDonation od : operation.getOperationDonations()) {
            if (od.getQuantity() <= 0) {
                throw new IllegalArgumentException("La cantidad de la donación debe ser mayor a 0");
            }
            od.setOperation(operationSaved);
            operationDonationRepository.save(od);
        }
    }
}


    @Override
    public void processTransfer(String message) {
        try {
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("El mensaje de transferencia está vacío");
            }

            log.info("📩 Mensaje recibido (TRANSFERENCIA): {}", message);

            int operationId = 2;

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
                // Validaciones
                if (donation.getQuantity() <= 0) {
                    log.warn("⚠️ Donación inválida (monto <= 0), descartada: {}", donation);
                    continue; // no guardar
                }
            

                // asociar y guardar
                donation.setOperation(operation);
                operationDonationRepository.save(donation);
            }

            log.info("✅ Transferencias guardadas en la base de datos.");
        } catch (Exception e) {
            log.error("❌ Error procesando transferencia", e);
        }
    }
}