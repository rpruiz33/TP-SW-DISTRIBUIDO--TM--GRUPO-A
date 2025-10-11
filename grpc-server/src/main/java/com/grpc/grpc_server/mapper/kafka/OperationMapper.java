package com.grpc.grpc_server.mapper.kafka;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.grpc.Category;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.OperationDonationMapper.OperationDonationDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;

public class OperationMapper {

    @Value("${ong.id}")
    static String ownONGId;


    /// ---------------------------------------------- DTOs ---------------------------------------------///
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDTO {

        private String idSolicitud;
        private String idOrganizacionSolicitante;
        private List<OperationDonationDTO> donaciones;

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfferDTO {

        private String idOferta;
        private String idOrganizacionDonante;
        private List<OperationDonationDTO> donaciones;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelRequestDTO {
        private int idOrganizacionSolicitante;
        private int idSolicitud;

        // Validación rápida del DTO
        public void validate() {
            if (idSolicitud <= 0) {
                throw new IllegalArgumentException("ID de solicitud inválido: " + idSolicitud);
            }
            if (idOrganizacionSolicitante <= 0) {
                throw new IllegalArgumentException("ID de organización inválido: " + idOrganizacionSolicitante);
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferDTO {

        private String idSolicitud;
        private String idOrganizacionDonante;
        private List<OperationDonationDTO> donaciones;
    }

    ///--------------------------------------MAPEO A ENTIDADAD-------------------------------------------------///
    

    ///request
    public static RequestDTO toDTO(MyServiceClass.OperationRequest request) {
        RequestDTO requestDTO = new RequestDTO();

        requestDTO.setIdOrganizacionSolicitante("1");
        requestDTO.setIdSolicitud(String.valueOf(request.getIdOperationMessage()));

        if (!request.getDonationsList().isEmpty()) {
            requestDTO.setDonaciones(
                    request.getDonationsList().stream()
                            .map(d -> OperationDonationMapper.toDTO(d))
                            .collect(Collectors.toList())
            );
        }

        return requestDTO;
    }

    ///request
    public static RequestDTO toDTO(Operation operation) {
        RequestDTO requestDTO = new RequestDTO();

        requestDTO.setIdOrganizacionSolicitante(String.valueOf(operation.getIdOrganization()));
        requestDTO.setIdSolicitud(String.valueOf(operation.getIdOperationMessage()));

        if (!operation.getOperationDonations().isEmpty()) {
            requestDTO.setDonaciones(
                    operation.getOperationDonations().stream()
                            .map(d -> OperationDonationMapper.toDTO(d))
                            .collect(Collectors.toList())
            );
        }

        return requestDTO;
    }

    public static Operation toEntity(RequestDTO dto, OperationType operationType) {
        Operation operation = new Operation();
        
    
        operation.setIdOperationMessage(Integer.parseInt(dto.getIdSolicitud().replaceAll("\\D", "")));
        operation.setIdOrganization(Integer.parseInt(dto.getIdOrganizacionSolicitante().replaceAll("\\D", "")));
        operation.setOperationType(operationType);
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


    ///offer
    public static Operation toEntity(OfferDTO dto, OperationType operationType) {

        Operation operation = new Operation();
        
        operation.setIdOperationMessage(Integer.parseInt(dto.getIdOferta().replaceAll("\\D", "")));
        operation.setIdOrganization(Integer.parseInt(dto.getIdOrganizacionDonante().replaceAll("\\D", "")));
        operation.setOperationType(operationType);
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

    //transfer
    public static Operation toEntity(TransferDTO dto, OperationType operationType) {

        Operation operation = new Operation();
        
        operation.setIdOperationMessage(Integer.parseInt(dto.getIdSolicitud().replaceAll("\\D", "")));
        operation.setIdOrganization(Integer.parseInt(dto.getIdOrganizacionDonante().replaceAll("\\D", "")));
        operation.setOperationType(operationType);
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
