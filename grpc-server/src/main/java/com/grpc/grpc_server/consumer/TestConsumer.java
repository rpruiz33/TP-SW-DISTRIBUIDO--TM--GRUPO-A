package com.grpc.grpc_server.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.grpc.grpc_server.entities.kafka.EventAdhesion;
import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.mapper.kafka.EventAdhesionMapper;
import com.grpc.grpc_server.services.kafka.impl.EventAdhesionConsumerServiceImpl;
import com.grpc.grpc_server.services.kafka.impl.ExternalEventConsumerServiceImpl;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper;
import com.grpc.grpc_server.mapper.kafka.EventAdhesionMapper;
import com.grpc.grpc_server.mapper.kafka.EventAdhesionMapper.EventAdhesionDTO;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper.ExternalEventDTO;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper.CancelExternalEventDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.RequestDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.TransferDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.OfferDTO;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO;
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
    private EventAdhesionConsumerServiceImpl eventAdhesionConsumerService;

    @Value("${ong.id}")
    String ownONGId;

    ///-----------------------------------METODOS--------------------------------------------------////
    public boolean isOwnMessage(String message){
        boolean result=false;

        try{
            JsonNode root = objectMapper.readTree(message);
            List<String> posiblesCampos = List.of("idOrganizacion", "idOrganizacionDonante", "idOrganizacionSolicitante");

            for (String campo : posiblesCampos) {
                String valor = root.path(campo).asText(null);
                if (ownONGId.equals(valor)) {
                    log.debug("Mensaje propio detectado por {}, ignorando...", campo);
                    return true;
                }
            }

        } catch (Exception e) {
            log.info("Error verificando la organizacion del mensaje", e);
        }


        return result;
    }

    ///-----------------------------------DONACIONES--------------------------------------------------////
    /// PUNTO 1 (publicar solicitudes)
    @KafkaListener(topics = "solicitud-donaciones", groupId = "grupo-unla")
    public void listenRequestDonations(String message) {
        
        try {

            // Validación mínima antes de enviar al service
            if (message == null || message.isBlank()) {
                log.warn("Mensaje vacío recibido en baja-evento-externo");
                return;
            }

            if (!isOwnMessage(message)){
                RequestDTO dto = objectMapper.readValue(message, RequestDTO.class);
                Operation operation = OperationMapper.toEntity(dto, OperationType.SOLICITUD);
                operationService.createOperation(operation);
            }

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
                log.warn("Mensaje vacío recibido en baja-evento-externo");
                return;
            }
            if (!isOwnMessage(message)){
        
                CancelRequestDTO cancelDTO = objectMapper.readValue(message, OperationMapper.CancelRequestDTO.class);
                operationService.processCancelRequest(cancelDTO);
            }

        } catch (Exception e) {
            log.error("Error en TestConsumer procesando mensaje de baja", e);
        }
    }

    /// PUNTO 2 (publicar transferencias)
    
    @KafkaListener(topicPattern = "transferencia-donaciones-.*", groupId = "grupo-unla") 
    public void consumirTransferencia(ConsumerRecord<String, String> record) { 
 
        String topic = record.topic(); 
        String message = record.value(); 

        // Extraer el id de la organización solicitante desde el topic 
        String idOrgSolicitante = topic.substring(topic.lastIndexOf("-") + 1); 
        System.out.println("Mensaje recibido del topic: " + topic); 
        System.out.println("ID organización solicitante: " + idOrgSolicitante); 
        System.out.println("Contenido: " + message); 

        try {

            // Validación mínima antes de enviar al service
            if (message == null || message.isBlank()) {
                log.warn("Mensaje vacío recibido en baja-evento-externo");
                return;
            }

            if (!isOwnMessage(message)){
                TransferDTO dto = objectMapper.readValue(message, TransferDTO.class); 
                Operation operation = OperationMapper.toEntity(dto, OperationType.TRANSFERENCIA);
                operationService.processTransfer(operation);
            }

        } catch (Exception e) {
            log.error("Error procesando mensaje de transferencia", e);
        }
         
    }

    /// PUNTO 3 (publicar ofertas)
    @KafkaListener(topics = "oferta-donaciones", groupId = "grupo-unla")
    public void listenOffer(String message) {
        try {

            // Validación mínima antes de enviar al service
            if (message == null || message.isBlank()) {
                log.warn("Mensaje vacío recibido en baja-evento-externo");
                return;
            }

            if (!isOwnMessage(message)){

                OfferDTO dto = objectMapper.readValue(message, OfferDTO.class);
                Operation operation = OperationMapper.toEntity(dto, OperationType.OFERTA);
                operationService.createOperation(operation);

            }

        } catch (Exception e) {
            
        }
    }

    ///-----------------------------------EVENTOS--------------------------------------------------////

    /// PUNTO 5 (publicar eventos)
    @KafkaListener(topics = "eventos-solidarios", groupId = "grupo-unla")
    public void listenExternalEvent(String message) {
        try {

            // Validación mínima antes de enviar al service
            if (message == null || message.isBlank()) {
                log.warn("Mensaje vacío recibido en baja-evento-externo");
                return;
            }
            if (!isOwnMessage(message)) {

                //FLUJO PARA MENSAJE EXTERNO
                ExternalEventDTO externalEventDTO = objectMapper.readValue(message, ExternalEventDTO.class);
                ExternalEvent externalEvent = ExternalEventMapper.toEntity(externalEventDTO);
                externalEventConsumerService.saveExternalEvent(externalEvent);
            }

        } catch (Exception e) {
            log.error("Error procesando mensaje de EVENTO", e);
        }
    }

    /// PUNTO 6 (dar de baja eventos publicados)
    @KafkaListener(topics = "baja-evento-solidario", groupId = "grupo-unla")
    public void listenDeleteExternalEvents(String message) {
        try {


            // Validación mínima antes de enviar al service
            if (message == null || message.isBlank()) {
                log.warn("Mensaje vacío recibido en baja-evento-externo");
                return;
            }
            if (!isOwnMessage(message)) {

                //FLUJO PARA MENSAJE EXTERNO
                CancelExternalEventDTO cancelExternalEventDTO = objectMapper.readValue(message, CancelExternalEventDTO.class);
                externalEventConsumerService.processCancelExternalEvent(cancelExternalEventDTO);

             }

        } catch (Exception e) {
            log.error("Error procesando mensaje de baja de EVENTO", e);
        }
    }

    /// PUNTO 7 (Adhesion de voluntarios externos a eventos)
    /// Solo escuchamos mensajes en el topico de nuestra organizacion
    @KafkaListener(topics = "adhesion-evento-1", groupId = "grupo-unla")
    public void listenExternalEventAdhesion(String message) {
        try {


            // Validación mínima antes de enviar al service
            if (message == null || message.isBlank()) {
                log.warn("Mensaje vacío recibido en baja-evento-externo");
                return;
            }
            if (!isOwnMessage(message)) {
                //FLUJO PARA MENSAJE EXTERNO
                EventAdhesionDTO eventAdhesionDTO = objectMapper.readValue(message, EventAdhesionDTO.class);

                int idExternalEvent = Integer.parseInt(eventAdhesionDTO.getIdEvento());

                ExternalEvent externalEvent = externalEventConsumerService.getExternalEventWithAdhesions(idExternalEvent);

                EventAdhesion eventAdhesion = EventAdhesionMapper.toEntity(eventAdhesionDTO, externalEvent);
                eventAdhesionConsumerService.saveEventAdhesion(eventAdhesion);
            }


        } catch (Exception e) {
            log.error("Error procesando mensaje de adhesion a EVENTO", e);
        }
    }

}
