package com.grpc.grpc_server.mapper.kafka;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.OperationDonationMapper.OperationDonationDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OperationMapper {


    // ---------------- DTOs ----------------
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationDTO {
        private String idSolicitud;
        private String idOrganizacion;
        private List<OperationDonationDTO> donaciones;

        // getters/setters/constructores
    }

    public static Operation toEntity(OperationDTO dto) {
        Operation operation = new Operation();
        
        operation.setIdOperationMessage(Integer.parseInt(dto.getIdSolicitud().replaceAll("\\D", "")));
        operation.setIdOrganization(Integer.parseInt(dto.getIdOrganizacion().replaceAll("\\D", "")));
        operation.setOperationType(OperationType.SOLICITUD);
        operation.setActivate(true);
        operation.setDateRegistration(LocalDateTime.now());
        operation.setDateModification(LocalDateTime.now());

        if (dto.getDonaciones() != null) {
            operation.setOperationDonations(
                dto.getDonaciones().stream()
                   .map(d -> OperationDonationMapper.toEntity(d, operation))
                   .collect(Collectors.toList())
            );
        }

        return operation;
    }

}
