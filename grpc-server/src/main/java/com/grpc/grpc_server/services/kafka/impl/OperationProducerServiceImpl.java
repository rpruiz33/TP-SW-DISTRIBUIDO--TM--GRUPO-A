

package com.grpc.grpc_server.services.kafka.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.grpc.Donation;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.OperationMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO;
import com.grpc.grpc_server.producer.OperationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.grpc.grpc_server.services.kafka.OperationServiceConsumer;
import com.grpc.grpc_server.services.kafka.OperationServiceProducer;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.grpc.grpc_server.repositories.grpc.DonationRepository;
import com.grpc.grpc_server.repositories.kafka.OperationDonationRepository;
import com.grpc.grpc_server.repositories.kafka.OperationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationProducerServiceImpl implements OperationServiceProducer{
 private final OperationRepository operationRepository;


    @Autowired
    private OperationProducer operationProducer;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private OperationDonationRepository operationDonationRepository; 

    @Autowired
    private OperationServiceConsumer operationServiceConsumer;

    @Transactional
    public String createAndSendOperation(Operation operation) {
        String result = "";

         // Persistir en DB
        Operation operationSaved = operationRepository.save(operation);

        // guardar donaciones asociadas
        if (operation.getOperationDonations() != null) {
            for (OperationDonation od : operation.getOperationDonations()) {
                if (od.getQuantity() <= 0) {
                    throw new IllegalArgumentException("La cantidad de la donación debe ser mayor a 0");
                }
                od.setOperation(operationSaved);
                operationDonationRepository.save(od);
            }
        }

        // Enviar a Kafka
        if(operationProducer.sendOperationCreated(operation)){
            result = "creado y enviado correctamente";
        }else{
            result = "no se pudo enviar";
        }

        return result;
    }


    ///validar la cantidad de la transferencia.
    ///validar que esté registrada la donacion en nuestro inventario.
    public String processTransfer(Operation operation){

        String result = "";

        // Persistir en DB
        Operation operationSaved = operationRepository.save(operation);

        // guardar donaciones asociadas
        if (operation.getOperationDonations() != null) {
            for (OperationDonation od : operation.getOperationDonations()) {
                if (od.getQuantity() <= 0) {
                    throw new IllegalArgumentException("La cantidad de la donación debe ser mayor a 0");
                }
                od.setOperation(operationSaved);
                operationDonationRepository.save(od);
            }
        }

        if (operation.getOperationDonations() != null) {

            for (OperationDonation od : operation.getOperationDonations()) {

                Donation donation = donationRepository.findByCategoryAndDescription(od.getCategory(), od.getDescription());
                
                if(donation != null){

                    donation.setAmount(donation.getAmount() - od.getQuantity());
                    donationRepository.save(donation);

                }else{
                    //acá se crearía

                }
            }
        }

        // Enviar a Kafka
        if(operationProducer.sendOperationCreated(operation)){
            result = "creado y enviado correctamente";
        }else{
            result = "no se pudo enviar";
        }

        return result;
    }

    public String processCancelRequest(CancelRequestDTO cancelRequestDTO){
        String result = "baja de solicitud";

        operationServiceConsumer.processCancelRequest(cancelRequestDTO);
        
        // Enviar a Kafka
        if(operationProducer.downRequestCreated(cancelRequestDTO)){
            result = "dado de baja y enviado";
        }else{
            result = "no se pudo enviar";
        }

        return result;
    }
    
    /**
     * Envía un mensaje cuando se crea una operación.

    public void sendOperationCreated(Operation operation) {
        try {
            String message = objectMapper.writeValueAsString(operation);
            kafkaTemplate.send(TOPIC_CREATE, message);
            log.info("📤 Evento enviado a Kafka ({}): {}", TOPIC_CREATE, message);
        } catch (JsonProcessingException e) {
            log.error("❌ Error serializando operación para Kafka", e);
        }
    }


     * Envía un mensaje de transferencia.

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


     * Envía un mensaje de baja de solicitud.

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


     * Envía un mensaje de oferta.

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
     */
}
