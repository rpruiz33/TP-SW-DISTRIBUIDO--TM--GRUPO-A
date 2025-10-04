package com.grpc.grpc_server.services.kafka.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.CancelRequestMapper;
import com.grpc.grpc_server.mapper.kafka.OfferDanationMapper;
import com.grpc.grpc_server.mapper.kafka.TransferMapper;
import com.grpc.grpc_server.repositories.OperationDonationRepository;
import com.grpc.grpc_server.repositories.OperationRepository;
import com.grpc.grpc_server.services.kafka.OperationService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class OperationServiceImpl implements OperationService{
    
    @Autowired
    private OperationRepository operationRepository;

     @Autowired
    private OperationDonationRepository operationDonationRepository; 
    @Autowired
    private TransferMapper tranMapper;
    @Autowired
    private ObjectMapper objectMapper;

    
@Override
public void createOperation(Operation operation) {
    if (operation == null) {
        throw new IllegalArgumentException("La operación no puede ser nula");
    }
    if (operation.getOperationType() == null) {
        throw new IllegalArgumentException("El tipo de operación es obligatorio");
    }
    if (operation.getIdOrganization() <= 0) {
        throw new IllegalArgumentException("La organización es obligatoria y debe ser válida");
    }

    // fechas
    if (operation.getDateRegistration() == null) {
        operation.setDateRegistration(LocalDateTime.now());
    }
    operation.setDateModification(LocalDateTime.now());

    // guardar operación
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
}


    @Override
    public void processTransfer(String message) {
        try {
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("El mensaje de transferencia está vacío");
            }

            log.info("📩 Mensaje recibido (TRANSFERENCIA): {}", message);

            int operationId = 2;

            Operation operation = operationRepository.findById(operationId)
                    .orElseGet(() -> {
                        Operation op = new Operation();
                        op.setIdOperationMessage((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
                        op.setIdOrganization(1);
                        op.setOperationType(OperationType.TRANSFERENCIA);
                        op.setActivate(true);
                        op.setDateRegistration(LocalDateTime.now());
                        op.setDateModification(LocalDateTime.now());
                        return operationRepository.save(op);
                    });

            List<OperationDonation> donations = objectMapper.readValue(
                    message,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, OperationDonation.class)
            );

            for (OperationDonation donation : donations) {
                // Validaciones
                if (donation.getQuantity() <= 0) {
                    log.warn("⚠️ Donación inválida (monto <= 0), descartada: {}", donation);
                    continue; // no guardar
                }
            

                // asociar y guardar
                donation.setOperation(operation);
                operationDonationRepository.save(donation);
            }

            log.info("✅ Transferencias guardadas en la base de datos.");
        } catch (Exception e) {
            log.error("❌ Error procesando transferencia", e);
        }
    }
    @Override
    public void processOfferMessage(String message) {
    try {
        // Validación básica del mensaje
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("El mensaje de oferta está vacío");
        }

        log.info("📩 Mensaje recibido (OFERTA): {}", message);

        // Deserializar JSON a Map
        var offerMap = objectMapper.readValue(message, java.util.Map.class);

        // Validar campos principales
        if (!offerMap.containsKey("idOffer") || !offerMap.containsKey("idOrganizationDonante") ||
            !offerMap.containsKey("donations")) {
            throw new IllegalArgumentException("El mensaje de oferta no tiene los campos obligatorios");
        }

        int idOffer = (Integer) offerMap.get("idOffer");
        int idOrg = (Integer) offerMap.get("idOrganizationDonante");

        if (idOffer <= 0) {
            throw new IllegalArgumentException("El ID de la oferta debe ser mayor a 0");
        }

        if (idOrg <= 0) {
            throw new IllegalArgumentException("El ID de la organización debe ser mayor a 0");
        }

        // Crear o recuperar operación
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

        // Obtener lista de donaciones
        List<java.util.Map<String, Object>> donationsList =
                (List<java.util.Map<String, Object>>) offerMap.get("donations");

        if (donationsList == null || donationsList.isEmpty()) {
            log.warn("⚠️ La oferta no contiene donaciones");
        } else {
            for (java.util.Map<String, Object> donationMap : donationsList) {

                // Validaciones de cada donación
                if (!donationMap.containsKey("category") || !donationMap.containsKey("description") ||
                    !donationMap.containsKey("quantity")) {
                    log.warn("⚠️ Donación incompleta, descartada: {}", donationMap);
                    continue;
                }

                int quantity = (Integer) donationMap.get("quantity");
                if (quantity <= 0) {
                    log.warn("⚠️ Donación con cantidad inválida, descartada: {}", donationMap);
                    continue;
                }

                String categoryStr = (String) donationMap.get("category");
                String description = (String) donationMap.get("description");

                if (categoryStr == null || categoryStr.isBlank()) {
                    log.warn("⚠️ Donación sin categoría, descartada: {}", donationMap);
                    continue;
                }

                if (description == null || description.isBlank()) {
                    log.warn("⚠️ Donación sin descripción, descartada: {}", donationMap);
                    continue;
                }

                // Crear entidad OperationDonation
                var donation = new OperationDonation();
                try {
                    donation.setCategory(Enum.valueOf(com.grpc.grpc_server.entities.Category.class, categoryStr));
                } catch (IllegalArgumentException ex) {
                    log.warn("⚠️ Categoría inválida, descartada: {}", categoryStr);
                    continue;
                }
                donation.setDescription(description);
                donation.setQuantity(quantity);
                donation.setOperation(operation);

                // Guardar en DB
                operationDonationRepository.save(donation);
            }
        }

        // Actualizar fechas de la operación
        operation.setDateModification(LocalDateTime.now());
        operationRepository.save(operation);

        // Mapear a DTO usando el mapper
        var dto = OfferDanationMapper.toDTO(operation);
        log.info("✅ Oferta procesada y DTO generado: {}", dto);

    } catch (Exception e) {
        log.error("❌ Error procesando oferta", e);
    }
}


  public void processCancelRequest(String message) {
    try {
        // 1️⃣ Validación de mensaje vacío
        if (message == null || message.isBlank()) {
            log.warn("Mensaje vacío recibido en baja-solicitud-donaciones");
            return;
        }

        log.info("📩 Mensaje recibido (BAJA SOLICITUD): {}", message);

        // 2️⃣ Deserializar JSON a DTO
        CancelRequestMapper.CancelRequestDTO cancelDTO =
                objectMapper.readValue(message, CancelRequestMapper.CancelRequestDTO.class);

        // 3️⃣ Validar DTO
        try {
            cancelDTO.validate();
        } catch (IllegalArgumentException ex) {
            log.error("❌ Error de validación en DTO: {}", ex.getMessage());
            return;
        }

        // 4️⃣ Buscar operación por idOperationMessage
        Operation existingOperation = operationRepository
                .findByIdOperationMessage(cancelDTO.getIdOffer())
                .orElse(null);

        if (existingOperation == null) {
            log.warn("⚠️ Solicitud {} no encontrada para baja", cancelDTO.getIdOffer());
            return;
        }

        // 5️⃣ Mapear con CancelRequestMapper (aplica validaciones de org y estado activo)
        try {
            CancelRequestMapper.toEntity(cancelDTO, existingOperation);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            log.error("❌ No se pudo dar de baja la solicitud: {}", ex.getMessage());
            return;
        }

        // 6️⃣ Persistir cambios
        operationRepository.save(existingOperation);

        log.info("✅ Solicitud de donación {} de la organización {} dada de baja correctamente",
                cancelDTO.getIdOffer(), cancelDTO.getIdOrganization());

    } catch (Exception e) {
        log.error("❌ Error inesperado procesando baja de solicitud", e);
    }
}
}