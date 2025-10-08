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
        private int idOrganizacion;
        private int idSolicitud;

        // Validación rápida del DTO
        public void validate() {
            if (idSolicitud <= 0) {
                throw new IllegalArgumentException("ID de solicitud inválido: " + idSolicitud);
            }
            if (idOrganizacion <= 0) {
                throw new IllegalArgumentException("ID de organización inválido: " + idOrganizacion);
            }
        }
    }
}
