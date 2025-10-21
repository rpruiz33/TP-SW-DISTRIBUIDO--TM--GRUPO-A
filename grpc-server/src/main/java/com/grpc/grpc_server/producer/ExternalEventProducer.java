package com.grpc.grpc_server.producer;

import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String CREATETOPIC = "eventos-solidarios";
    private static final String DELETETOPIC = "baja-evento-solidario";


    public boolean sendExternalEventCreated(ExternalEventMapper.ExternalEventDTO externalEventDTO) {
        boolean result =false;
        try {
            // Serializar la entidad a JSON
            String message = objectMapper.writeValueAsString(externalEventDTO);

            kafkaTemplate.send(CREATETOPIC, message);
            log.info("📤 Evento enviado a Kafka ({}): {}", CREATETOPIC, message);

            result=true;

        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando operación de crear evento externo para Kafka", e);
        }

        return result;
    }

    public boolean sendExternalEventDeleted(ExternalEventMapper.CancelExternalEventDTO cancelExternalEventDTO) {
        boolean result =false;
        try {
            // Serializar la entidad a JSON
            String message = objectMapper.writeValueAsString(cancelExternalEventDTO);

            kafkaTemplate.send(DELETETOPIC, message);
            log.info("📤 Evento deleted enviado a Kafka ({}): {}", DELETETOPIC, message);

            result=true;

        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando operación de borrar evento externo para Kafka", e);
        }

        return result;
    }
}