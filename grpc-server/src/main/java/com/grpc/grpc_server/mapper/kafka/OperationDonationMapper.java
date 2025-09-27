package com.grpc.grpc_server.mapper.kafka;

import com.grpc.grpc_server.entities.Category;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.mapper.kafka.OperationDonationMapper.OperationDonationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OperationDonationMapper {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationDonationDTO {
        private String categoria;
        private String descripcion;
        private int cantidad;
    }

    public static OperationDonation toEntity(OperationDonationDTO d, Operation operation) {
        OperationDonation donation = new OperationDonation();
        donation.setCategory(Category.valueOf(d.getCategoria().toUpperCase()));
        donation.setDescription(d.getDescripcion());
        donation.setQuantity(d.getCantidad());
        donation.setOperation(operation);
        return donation;
    }

}
