package com.grpc.grpc_server.mapper.kafka;

import java.time.LocalDateTime;

import com.grpc.grpc_server.entities.kafka.Operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CancelRequestMapper {

    // DTO para la baja de solicitud
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelRequestDTO {
        private int idOrganization;
        private int idOffer;

        // Validación rápida del DTO
        public void validate() {
            if (idOffer <= 0) {
                throw new IllegalArgumentException("ID de solicitud inválido: " + idOffer);
            }
            if (idOrganization <= 0) {
                throw new IllegalArgumentException("ID de organización inválido: " + idOrganization);
            }
        }
    }

    /**
     * Mapea el DTO a la entidad Operation para dar de baja.
     * @param dto DTO de cancelación
     * @param existingOperation Operación existente en DB
     * @return Operación marcada como inactiva
     */
    public static Operation toEntity(CancelRequestDTO dto, Operation existingOperation) {
        // Validar DTO
        dto.validate();

        if (existingOperation == null) {
            throw new IllegalArgumentException("La operación no puede ser nula para dar de baja");
        }

        // Validar que la organización coincide
        if (existingOperation.getIdOrganization() != dto.getIdOrganization()) {
            throw new IllegalArgumentException(
                "La organización " + dto.getIdOrganization() + " no coincide con la operación " + existingOperation.getIdOperation()
            );
        }

        // Validar que la operación esté activa
        if (!existingOperation.isActivate()) {
            throw new IllegalStateException(
                "La solicitud " + existingOperation.getIdOperation() + " ya estaba dada de baja"
            );
        }

        // Marcar la operación como inactiva
        existingOperation.setActivate(false);
        existingOperation.setDateModification(LocalDateTime.now());

        return existingOperation;
    }
}
