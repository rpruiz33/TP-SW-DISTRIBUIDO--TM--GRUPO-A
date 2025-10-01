package com.grpc.grpc_server.mapper.kafka;

import org.springframework.stereotype.Component;

import com.grpc.grpc_server.entities.Category;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
public class TransferMapper {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferDTO {
        private String categoria;
        private String descripcion;
        private int cantidad;
    }

    public OperationDonation toEntity(TransferDTO dto, Operation operation) {
        OperationDonation donation = new OperationDonation();
        donation.setCategory(Category.valueOf(dto.getCategoria().toUpperCase()));
        donation.setDescription(dto.getDescripcion());
        donation.setQuantity(dto.getCantidad());
        donation.setOperation(operation);
        return donation;
    }
}
