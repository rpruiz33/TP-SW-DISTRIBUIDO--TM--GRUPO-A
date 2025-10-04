
package com.grpc.grpc_server.mapper.kafka;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
public class OfferDanationMapper {

    // DTO principal
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationDTO {
        private int idOperationMessage;
        private int idOrganization;
        private String operationType;
        private LocalDateTime dateRegistration;
        private List<OperationDonationDTO> donations;
    }

    // DTO anidado para las donaciones
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationDonationDTO {
        private String category;
        private String description;
        private int quantity;
    }

    // Métodos de mapeo
    public static OperationDTO toDTO(Operation operation) {
        List<OperationDonationDTO> donationDTOs = operation.getOperationDonations()
                .stream()
                .map(OfferDanationMapper::toDonationDTO)
                .collect(Collectors.toList());

        return new OperationDTO(
                operation.getIdOperationMessage(),
                operation.getIdOrganization(),
                operation.getOperationType().name(),
                operation.getDateRegistration(),
                donationDTOs
        );
    }

    private static OperationDonationDTO toDonationDTO(OperationDonation donation) {
        return new OperationDonationDTO(
                donation.getCategory().name(),
                donation.getDescription(),
                donation.getQuantity()
        );
    }
}
