package com.grpc.grpc_server.mapper.kafka;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.grpc.Category;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;

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

    public static OperationDonationDTO toDTO (MyServiceClass.OperationDonationProto request){
        OperationDonationDTO dto = new OperationDonationDTO();

        dto.setCategoria(request.getCategory());
        dto.setDescripcion(request.getDescription());
        dto.setCantidad(request.getQuantity());

        return dto;
    }

    public static OperationDonation toEntity(OperationDonationDTO d, Operation operation) {
        OperationDonation donation = new OperationDonation();
        donation.setCategory(Category.valueOf(d.getCategoria().toUpperCase()));
        donation.setDescription(d.getDescripcion());
        donation.setQuantity(d.getCantidad());
        donation.setOperation(operation);
        donation.setActivate(true);
        return donation;
    }

}
