package com.grpc.grpc_server.consumer;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.repositories.OperationDonationRepository;
import com.grpc.grpc_server.repositories.OperationRepository;

@Service
public class TestConsumer {

    private final ObjectMapper objectMapper;
    private final OperationDonationRepository donationRepository;
    private final OperationRepository operationRepository;

    // Inyección por constructor
    @Autowired
    public TestConsumer(ObjectMapper objectMapper,
                        OperationDonationRepository donationRepository,
                        OperationRepository operationRepository) {
        this.objectMapper = objectMapper;
        this.donationRepository = donationRepository;
        this.operationRepository = operationRepository;
    }

    // Escucha de solicitudes externas
    @KafkaListener(topics = "test-solicitud-donacion", groupId = "grupo-unla")
    public void listen(String message) {
        try {
            System.out.println("Mensaje recibido: " + message);

            OperationDonation donation = objectMapper.readValue(message, OperationDonation.class);

            int operationId = 1;
            Operation operation = operationRepository.findById(operationId)
                .orElseGet(() -> {
                    Operation op = new Operation();
                    op.setIdOperationMessage(1001);
                    op.setIdOrganization(1);
                    op.setOperationType(OperationType.SOLICITUD);
                    op.setActivate(true);
                    op.setDateRegistration(LocalDateTime.now());
                    op.setDateModification(LocalDateTime.now());
                    return operationRepository.save(op);
                });

            donation.setOperation(operation);
            donationRepository.save(donation);

            System.out.println("Donación guardada en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Escucha de transferencias
   @KafkaListener(topics = "transferencia-donaciones-1", groupId = "grupo-unla")
public void listenTransferencia(String message) {
    try {
        System.out.println("📩 Mensaje recibido (TRANSFERENCIA): " + message);

        int operationId = 2;
        Operation operation = operationRepository.findById(operationId)
                .orElseGet(() -> {
                    Operation op = new Operation();
                    op.setIdOperationMessage(2001);
                    op.setIdOrganization(1);
                    op.setOperationType(OperationType.TRANSFERENCIA);
                    op.setActivate(true);
                    op.setDateRegistration(LocalDateTime.now());
                    op.setDateModification(LocalDateTime.now());
                    return operationRepository.save(op);
                });

        // Deserializar lista de donaciones
        List<OperationDonation> donations = objectMapper.readValue(
            message,
            objectMapper.getTypeFactory().constructCollectionType(List.class, OperationDonation.class)
        );

        for (OperationDonation donation : donations) {
            donation.setOperation(operation);
            donationRepository.save(donation);
        }

        System.out.println("✅ Transferencias guardadas en la base de datos.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}