package com.grpc.grpc_server.producer;

import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.mapper.kafka.EventAdhesionMapper;
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
public class EventAdhesionProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    private static final String ADHESIONTOPIC = "adhesion-evento-";


    public boolean sendEventAdhhesion(EventAdhesionMapper.EventAdhesionDTO eventAdhesionDTO, int idOrganizacionExterna) {
        boolean result =false;
        try {
            // Serializar la entidad a JSON
            String message = objectMapper.writeValueAsString(eventAdhesionDTO);

            // Construir el nombre del tópico dinámicamente con el id de la organizacion que publico el evento, para que lo reciba en su topic
            String topicName = ADHESIONTOPIC + idOrganizacionExterna;

            kafkaTemplate.send(topicName, message);
            log.info("📤 Evento enviado a Kafka ({}): {}", topicName, message);

            result = true;

        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando operación de crear evento externo para Kafka", e);
        }

        return result;
    }
}