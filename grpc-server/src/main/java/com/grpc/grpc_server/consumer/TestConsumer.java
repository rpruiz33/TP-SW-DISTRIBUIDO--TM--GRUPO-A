package com.grpc.grpc_server.consumer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
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

    // Escucha de ofertas (sin clase externa)
    @KafkaListener(topics = "oferta-donaciones", groupId = "grupo-unla")
    public void listenOffer(String message) {
        try {
            System.out.println("📩 Mensaje recibido (OFERTA): " + message);

            // Map para deserializar JSON directamente
            Map<String, Object> offerMap = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});

            int idOffer = (Integer) offerMap.get("idOffer");
            int idOrg = (Integer) offerMap.get("idOrganizationDonante");

            // Crear o recuperar operación tipo OFERTA
            Operation operation = operationRepository.findById(idOffer)
                    .orElseGet(() -> {
                        Operation op = new Operation();
                        op.setIdOperationMessage(idOffer);
                        op.setIdOrganization(idOrg);
                        op.setOperationType(OperationType.OFERTA);
                        op.setActivate(true);
                        op.setDateRegistration(LocalDateTime.now());
                        op.setDateModification(LocalDateTime.now());
                        return operationRepository.save(op);
                    });

            // Obtener lista de donaciones y convertir cada Map en OperationDonation
            List<Map<String, Object>> donationsList = (List<Map<String, Object>>) offerMap.get("donations");
            for (Map<String, Object> donationMap : donationsList) {
                OperationDonation donation = new OperationDonation();
                donation.setCategory(Enum.valueOf(com.grpc.grpc_server.entities.Category.class,
                        (String) donationMap.get("category")));
                donation.setDescription((String) donationMap.get("description"));
                donation.setQuantity((Integer) donationMap.get("quantity"));
                donation.setOperation(operation);
                donationRepository.save(donation);
            }

            System.out.println("✅ Donaciones de oferta guardadas en la base de datos.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
