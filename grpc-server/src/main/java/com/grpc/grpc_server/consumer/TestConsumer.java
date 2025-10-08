package com.grpc.grpc_server.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.mapper.kafka.CancelRequestMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.OperationDTO;
import com.grpc.grpc_server.repositories.OperationDonationRepository;
import com.grpc.grpc_server.repositories.OperationRepository;
import com.grpc.grpc_server.services.kafka.OperationServiceConsumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TestConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OperationServiceConsumer operationService;

    @Autowired
    private OperationDonationRepository donationRepository;

    @Autowired
    private OperationRepository operationRepository;


    ///-----------------------------------SOLICITUDES--------------------------------------------------////
    /// PUNTO 1 (publicar solicitudes)
    @KafkaListener(topics = "solicitud-donaciones", groupId = "grupo-unla")
    public void listenRequestDonations(String message) {
        
        try {

            OperationDTO dto = objectMapper.readValue(message, OperationDTO.class);
            Operation operation = OperationMapper.toEntity(dto);

            operationService.createOperation(operation);

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    /// PUNTO 4 (dar de baja solicitud)
    @KafkaListener(topics = "baja-solicitud-donaciones", groupId = "grupo-unla")
    public void listenDeleteRequestDonation(String message) {
        try {

            // Validación mínima antes de enviar al service
            if (message == null || message.isBlank()) {
                log.warn("Mensaje vacío recibido en baja-solicitud-donaciones");
                return;
            }

            //Deserializar JSON a DTO
            CancelRequestMapper.CancelRequestDTO cancelDTO =
                objectMapper.readValue(message, CancelRequestMapper.CancelRequestDTO.class);

            // Llamada al service que contiene toda la lógica de procesamiento
            operationService.processCancelRequest(cancelDTO);

        } catch (Exception e) {
            log.error("Error en TestConsumer procesando mensaje de baja", e);
        }
    }

    ///-----------------------------------OTROS--------------------------------------------------////

   // Listener para transferencias
    @KafkaListener(topics = "transferencia-donaciones-1", groupId = "grupo-unla")
    public void listenTransfer(String message) {
        log.info("📩 Mensaje recibido en 'transferencia-donaciones-1': {}", message);
        operationService.processTransfer(message); // llama a tu método
    }

    @KafkaListener(topics = "oferta-donaciones", groupId = "grupo-unla")
    public void listenOffer(String message) {
        try {
            log.info("📩 Mensaje recibido en topic 'oferta-donaciones': {}", message);

            // Llamamos al servicio que procesa el mensaje y guarda en la DB
            operationService.processOfferMessage(message);

            log.info("✅ Mensaje de oferta procesado correctamente");

        } catch (Exception e) {
            log.error("❌ Error procesando mensaje de oferta", e);
        }
    }

    @KafkaListener(topics = "alta-solicitud-donaciones", groupId = "grupo-ong")
    public void consumirOperacion(String message) {
        log.info("📥 Operación recibida: {}", message);
        // Parsear JSON y guardar en la DB local
    }
}
