package com.grpc.grpc_server.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.mapper.kafka.OperationMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPICSOLICITUD = "solicitud-donaciones";
    private static final String TOPICOFERTA = "oferta-donaciones";
    private static final String TOPICOBAJA = "baja-solicitud-donaciones";

    public boolean sendOperationCreated(Operation operation) {
        boolean result =false;
        try {

            String message = ""; // loque se manda por kafka
            
            switch (String.valueOf(operation.getOperationType())){

                case "SOLICITUD":
                //transformar a dto
                OperationMapper.RequestDTO dto = OperationMapper.toDTO(operation);
                message = objectMapper.writeValueAsString(dto);
                kafkaTemplate.send(TOPICSOLICITUD, message);
                log.info("📤 Evento enviado a Kafka ({}): {}", TOPICSOLICITUD, message);
                break;

                case "TRANSFERENCIA": ///HACER QUE PUBLIQUE EN EL TOPICO
                //transformar a dto
                OperationMapper.TransferDTO dto2 = OperationMapper.toTransferDTO(operation);
                message = objectMapper.writeValueAsString(dto2);
                break;

                case "OFERTA": 
                //transformar a dto
                OperationMapper.OfferDTO dto3 = OperationMapper.toOfferDTO(operation);
                message = objectMapper.writeValueAsString(dto3);
                kafkaTemplate.send(TOPICOFERTA, message);
                log.info("📤 Evento enviado a Kafka ({}): {}", TOPICOFERTA, message);
                break;

    
            }
            
            result=true;

        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando operación para Kafka", e);
        }

        return result;
    }

    public boolean downRequestCreated(CancelRequestDTO cancelRequestDTO){

        boolean result =false;
        try {

            String message = objectMapper.writeValueAsString(cancelRequestDTO);
            kafkaTemplate.send(TOPICOBAJA, message);
            log.info("📤 Evento enviado a Kafka ({}): {}", TOPICOFERTA, message);
            result = true;
            
        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando operación para Kafka", e);
        }

        return result;

     }
}
