package com.grpc.grpc_server.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "alta-solicitud-donaciones";

    public void sendOperationCreated(Operation operation) {
        try {
            // Serializar la entidad a JSON
            String message = objectMapper.writeValueAsString(operation);

            kafkaTemplate.send(TOPIC, message);
            log.info("📤 Evento enviado a Kafka ({}): {}", TOPIC, message);

        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando operación para Kafka", e);
        }
    }
}
