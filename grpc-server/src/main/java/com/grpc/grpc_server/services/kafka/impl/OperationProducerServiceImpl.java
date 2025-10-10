

package com.grpc.grpc_server.services.kafka.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;

import com.grpc.grpc_server.producer.OperationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.grpc.grpc_server.services.kafka.OperationServiceProducer;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.grpc.grpc_server.repositories.kafka.OperationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationProducerServiceImpl implements OperationServiceProducer{
 private final OperationRepository operationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private OperationProducer operationProducer;

    // Definición de los tópicos
    private static final String TOPIC_CREATE = "operation-create";
    private static final String TOPIC_TRANSFER = "operation-transfer";
    private static final String TOPIC_CANCEL = "operation-cancel";
    private static final String TOPIC_OFFER = "operation-offer";


    @Transactional
    public String createAndSendOperation(Operation operation) {
        String result = "error";

        // Enviar a Kafka
        if (operationProducer.sendOperationCreated(operation)){
            // Persistir en DB
            operationRepository.save(operation);

            result = "Solicitud enviada y persistida";
            log.info(result);
        }

        return result;
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
            var dto = new com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO(idOrganization, idOffer);
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
