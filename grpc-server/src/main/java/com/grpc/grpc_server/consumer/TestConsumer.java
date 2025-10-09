package com.grpc.grpc_server.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.services.kafka.impl.ExternalEventConsumerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper.ExternalEventDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.OfferDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO;
import com.grpc.grpc_server.repositories.kafka.OperationDonationRepository;
import com.grpc.grpc_server.repositories.kafka.OperationRepository;
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
    private ExternalEventConsumerServiceImpl externalEventConsumerService;

    @Autowired
    private OperationDonationRepository donationRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Value("${ong.id}")
    String ownONGId;

    ///-----------------------------------SOLICITUDES--------------------------------------------------////
    /// PUNTO 1 (publicar solicitudes)
    @KafkaListener(topics = "solicitud-donaciones", groupId = "grupo-unla")
    public void listenRequestDonations(String message) {
        
        try {

            RequestDTO dto = objectMapper.readValue(message, RequestDTO.class);
            Operation operation = OperationMapper.toEntity(dto, OperationType.SOLICITUD);

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
            CancelRequestDTO cancelDTO =
                objectMapper.readValue(message, OperationMapper.CancelRequestDTO.class);

            // Llamada al service que contiene toda la lógica de procesamiento
            operationService.processCancelRequest(cancelDTO);

        } catch (Exception e) {
            log.error("Error en TestConsumer procesando mensaje de baja", e);
        }
    }

    /// PUNTO 3 (publicar ofertas)
    @KafkaListener(topics = "oferta-donaciones", groupId = "grupo-unla")
    public void listenOffer(String message) {
        try {

            OfferDTO dto = objectMapper.readValue(message, OfferDTO.class);
            Operation operation = OperationMapper.toEntity(dto, OperationType.TRANSFERENCIA);

            operationService.createOperation(operation);

        } catch (Exception e) {
            log.error("Error procesando mensaje de oferta", e);
        }
    }

    /// PUNTO 5 (publicar eventos)
    @KafkaListener(topics = "eventos-solidarios", groupId = "grupo-unla")
    public void listenEvents(String message) {
        try {

            //LOGICA DE VERIFICACION DE ID DE ORGANIZACION
            JsonNode root = objectMapper.readTree(message);
            String idOrganizacion = root.path("idOrganizacion").asText();

            log.info(idOrganizacion);
            log.info("Nuestro");
            log.info(ownONGId);
            //SI EL MENSAJE ES NUESTRO, LO IGNORAMOS
            if (ownONGId.equals(idOrganizacion)) {
                log.debug("Mensaje propio detectado, ignorando...");
                return;
            }

            //FLUJO PARA MENSAJE EXTERNO
            ExternalEventDTO externalEventDTO = objectMapper.readValue(message,ExternalEventDTO.class);
            ExternalEvent externalEvent = ExternalEventMapper.toEntity(externalEventDTO);
            externalEventConsumerService.saveExternalEvent(externalEvent);


        } catch (Exception e) {
            log.error("Error procesando mensaje de oferta", e);
        }
    }

    ///-----------------------------------OTROS--------------------------------------------------////

    // Listener para transferencias
    @KafkaListener(topics = "transferencia-donaciones-1", groupId = "grupo-unla")
    public void listenTransfer(String message) {
        log.info("📩 Mensaje recibido en 'transferencia-donaciones-1': {}", message);
        operationService.processTransfer(message); // llama a tu método
    }

}
