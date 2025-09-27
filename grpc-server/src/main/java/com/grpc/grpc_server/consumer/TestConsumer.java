package com.grpc.grpc_server.consumer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.repositories.DonationRequestRepository;
import com.grpc.grpc_server.repositories.OperationRepository;

@Service
public class TestConsumer {

    private final ObjectMapper objectMapper;
    private final DonationRequestRepository solicitudRepository;
    private final OperationRepository operationRepository;

    // Inyección por constructor
    @Autowired
    public TestConsumer(ObjectMapper objectMapper,
                        DonationRequestRepository solicitudRepository,
                        OperationRepository operationRepository) {
        this.objectMapper = objectMapper;
        this.solicitudRepository = solicitudRepository;
        this.operationRepository = operationRepository;
    }

    @KafkaListener(topics = "test-solicitud-donacion", groupId = "grupo-unla")
    public void listen(String message) {
        try {
            System.out.println("Mensaje recibido: " + message);

            // Convertir JSON a objeto
            OperationDonation donation = objectMapper.readValue(message, OperationDonation.class);

            // ID de operación que queremos asociar
            int operationId = 1;

            // Buscar operación existente
            Optional<Operation> existingOperation = operationRepository.findById(operationId);
            Operation operation;

            if (existingOperation.isPresent()) {
                operation = existingOperation.get();
            } else {
                // Crear operación si no existe
                operation = new Operation();
                // ⚠️ No se setea manualmente el ID porque está con @GeneratedValue
                // operation.setIdOperation(operationId);
                operation.setIdOperationMessage(1001); // ejemplo, id del mensaje externo
                operation.setIdOrganization(1);        // ejemplo, organización asociada
                operation.setOperationType(OperationType.SOLICITUD);
                operation.setActivate(true);
                operation.setDateRegistration(LocalDateTime.now());
                operation.setDateModification(LocalDateTime.now());

                operation = operationRepository.save(operation);
                System.out.println("Nueva operación creada con id: " + operation.getIdOperation());
            }

            // Asignar operación a la donación
            donation.setOperation(operation);

            // Guardar donación
            solicitudRepository.save(donation);

            System.out.println("Donación guardada en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
