package com.grpc.grpc_server.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.mapper.kafka.OperationMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "solicitud-donaciones";

    public boolean sendOperationCreated(OperationMapper.RequestDTO requestDTO) {
        boolean result =false;
        try {
            // Serializar la entidad a JSON
            String message = objectMapper.writeValueAsString(requestDTO);

            kafkaTemplate.send(TOPIC, message);
            log.info("📤 Evento enviado a Kafka ({}): {}", TOPIC, message);

            result=true;

        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando operación para Kafka", e);
        }

        return result;
    }
}
