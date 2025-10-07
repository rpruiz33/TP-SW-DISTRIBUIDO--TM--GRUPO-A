package com.grpc.grpc_server.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
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

    @KafkaListener(topics = "test-solicitud-donacion", groupId = "grupo-unla")
    public void consume(String message) {
        
        try {

            System.out.println("HOLLAAAAAA");
            OperationDTO dto = objectMapper.readValue(message, OperationDTO.class);
            Operation operation = OperationMapper.toEntity(dto);

            operationService.createOperation(operation);

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
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

    @KafkaListener(topics = "baja-solicitud-donaciones", groupId = "grupo-unla")
    public void listenCancelRequest(String message) {
        try {
            // Validación mínima antes de enviar al service
            if (message == null || message.isBlank()) {
                log.warn("Mensaje vacío recibido en baja-solicitud-donaciones");
                return;
            }

            // Llamada al service que contiene toda la lógica de procesamiento
            operationService.processCancelRequest(message);

        } catch (Exception e) {
            log.error("❌ Error en TestConsumer procesando mensaje de baja", e);
        }
    }
    @KafkaListener(topics = "alta-solicitud-donaciones", groupId = "grupo-ong")
    public void consumirOperacion(String message) {
        log.info("📥 Operación recibida: {}", message);
        // Parsear JSON y guardar en la DB local
    }
    
    /* 
    // Escucha de solicitudes externas
    @KafkaListener(topics = "test-solicitud-donacion", groupId = "grupo-unla")
    public void listen(String message) {
        try {
            System.out.println("Mensaje recibido: " + message);

            OperationDonation donation = objectMapper.readValue(message, OperationDonation.class);

            int operationId = 1;
            Operation operation = operationRepository.findById(operationId)
                .orElseGet(() -> {
                    Operation op = new Operation();
                    op.setIdOperationMessage(1001);
                    op.setIdOrganization(1);
                    op.setOperationType(OperationType.SOLICITUD);
                    op.setActivate(true);
                    op.setDateRegistration(LocalDateTime.now());
                    op.setDateModification(LocalDateTime.now());
                    return operationRepository.save(op);
                });

            donation.setOperation(operation);
            donationRepository.save(donation);

            System.out.println("Donación guardada en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */


    // Escucha de transferencias
    /*@KafkaListener(topics = "transferencia-donaciones-1", groupId = "grupo-unla")
    public void listenTransferencia(String message) {
        try {
            System.out.println("📩 Mensaje recibido (TRANSFERENCIA): " + message);

            int operationId = 2;
            Operation operation = operationRepository.findById(operationId)
                    .orElseGet(() -> {
                        Operation op = new Operation();
                        op.setIdOperationMessage(2001);
                        op.setIdOrganization(1);
                        op.setOperationType(OperationType.TRANSFERENCIA);
                        op.setActivate(true);
                        op.setDateRegistration(LocalDateTime.now());
                        op.setDateModification(LocalDateTime.now());
                        return operationRepository.save(op);
                    });

            // Deserializar lista de donaciones
            List<OperationDonation> donations = objectMapper.readValue(
                message,
                objectMapper.getTypeFactory().constructCollectionType(List.class, OperationDonation.class)
            );

            for (OperationDonation donation : donations) {
                donation.setOperation(operation);
                donationRepository.save(donation);
            }

            System.out.println("✅ Transferencias guardadas en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    // Escucha de ofertas (sin clase externa)
    /*@KafkaListener(topics = "oferta-donaciones", groupId = "grupo-unla")
    public void listenOffer(String message) {
        try {
            System.out.println("📩 Mensaje recibido (OFERTA): " + message);

            // Map para deserializar JSON directamente
            Map<String, Object> offerMap = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});

            int idOffer = (Integer) offerMap.get("idOffer");
            int idOrg = (Integer) offerMap.get("idOrganizationDonante");

            // Crear o recuperar operación tipo OFERTA
            Operation operation = operationRepository.findById(idOffer)
                    .orElseGet(() -> {
                        Operation op = new Operation();
                        op.setIdOperationMessage(idOffer);
                        op.setIdOrganization(idOrg);
                        op.setOperationType(OperationType.OFERTA);
                        op.setActivate(true);
                        op.setDateRegistration(LocalDateTime.now());
                        op.setDateModification(LocalDateTime.now());
                        return operationRepository.save(op);
                    });

            // Obtener lista de donaciones y convertir cada Map en OperationDonation
            List<Map<String, Object>> donationsList = (List<Map<String, Object>>) offerMap.get("donations");
            for (Map<String, Object> donationMap : donationsList) {
                OperationDonation donation = new OperationDonation();
                donation.setCategory(Enum.valueOf(com.grpc.grpc_server.entities.Category.class,
                        (String) donationMap.get("category")));
                donation.setDescription((String) donationMap.get("description"));
                donation.setQuantity((Integer) donationMap.get("quantity"));
                donation.setOperation(operation);
                donationRepository.save(donation);
            }

            System.out.println("✅ Donaciones de oferta guardadas en la base de datos.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    

}
