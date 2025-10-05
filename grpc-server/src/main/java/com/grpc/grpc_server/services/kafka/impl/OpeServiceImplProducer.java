

package com.grpc.grpc_server.services.kafka.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.grpc.grpc_server.services.kafka.OperationServiceProducer;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.grpc.grpc_server.repositories.OperationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpeServiceImplProducer implements OperationServiceProducer{

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OperationRepository operationRepository;

    private static final String TOPIC_CREATE = "alta-solicitud-donaciones";
    private static final String TOPIC_TRANSFER = "transferencia";
    private static final String TOPIC_CANCEL = "baja-solicitud-donaciones";
    private static final String TOPIC_OFFER = "oferta";
    @Transactional
    public void createAndSendOperation(Operation operation) {
        try {
            // Persistir en DB
            operationRepository.save(operation);

            // Enviar a Kafka
            String message = objectMapper.writeValueAsString(operation);
            kafkaTemplate.send(TOPIC_CREATE, message);

            log.info("📥 Operación guardada y enviada a Kafka: {}", message);

        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando operación para Kafka", e);
        } catch (Exception e) {
            log.error("❌ Error guardando operación en DB", e);
        }
    }
    /**
     * Envía un mensaje cuando se crea una operación.
     */
    public void sendOperationCreated(Operation operation) {
        try {
            String message = objectMapper.writeValueAsString(operation);
            kafkaTemplate.send(TOPIC_CREATE, message);
            log.info("📤 Evento enviado a Kafka ({}): {}", TOPIC_CREATE, message);
        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando operación para Kafka", e);
        }
    }

    /**
     * Envía un mensaje de transferencia.
     */
    public void sendTransfer(Operation operation, List<OperationDonation> donations) {
        try {
            String message = objectMapper.writeValueAsString(donations);
            kafkaTemplate.send(TOPIC_TRANSFER, message);
            log.info("📤 Transferencia enviada a Kafka ({}): OperationId={}, donations={}", 
                      TOPIC_TRANSFER, operation.getIdOperationMessage(), message);
        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando transferencias para Kafka", e);
        }
    }

    /**
     * Envía un mensaje de baja de solicitud.
     */
    public void sendCancelRequest(int idOffer, int idOrganization) {
        try {
            var dto = new com.grpc.grpc_server.mapper.kafka.CancelRequestMapper.CancelRequestDTO(idOrganization, idOffer);
            String message = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(TOPIC_CANCEL, message);
            log.info("📤 Solicitud de baja enviada a Kafka ({}): {}", TOPIC_CANCEL, message);
        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando baja de solicitud para Kafka", e);
        }
    }

    /**
     * Envía un mensaje de oferta.
     */
    public void sendOffer(Operation operation, List<OperationDonation> donations) {
        try {
            // Crear estructura simplificada para la oferta
            var offerMessage = new java.util.HashMap<String, Object>();
            offerMessage.put("idOffer", operation.getIdOperationMessage());
            offerMessage.put("idOrganizationDonante", operation.getIdOrganization());
            offerMessage.put("donations", donations);

            String message = objectMapper.writeValueAsString(offerMessage);
            kafkaTemplate.send(TOPIC_OFFER, message);
            log.info("📤 Oferta enviada a Kafka ({}): {}", TOPIC_OFFER, message);
        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando oferta para Kafka", e);
        }
    }
}
