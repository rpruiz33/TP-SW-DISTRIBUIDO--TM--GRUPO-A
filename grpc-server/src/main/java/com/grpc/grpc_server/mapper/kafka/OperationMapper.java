package com.grpc.grpc_server.mapper.kafka;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.grpc.Category;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.grpc.UserMapper;
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

    ///--------------------------------------MAPEO A DTO-------------------------------------------------///
    
    
    
    ///PROTO --> OFFERDTO
    public static CancelRequestDTO toCancelRequestDTO(MyServiceClass.OperationRequest request) {
        
        CancelRequestDTO cancelRequestDTO = new CancelRequestDTO();

        cancelRequestDTO.setIdOrganizacionSolicitante(1);
        cancelRequestDTO.setIdSolicitud(request.getIdOperationMessage());

        return cancelRequestDTO;
    }
    ///PROTO --> OFFERDTO
    public static OfferDTO toOfferDTO(MyServiceClass.OperationRequest request) {
        OfferDTO OfferDTO = new OfferDTO();

        OfferDTO.setIdOrganizacionDonante("1");
        OfferDTO.setIdOferta(String.valueOf(request.getIdOperationMessage()));

        if (!request.getDonationsList().isEmpty()) {
            OfferDTO.setDonaciones(
                    request.getDonationsList().stream()
                            .map(d -> OperationDonationMapper.toDTO(d))
                            .collect(Collectors.toList())
            );
        }

        return OfferDTO;
    }

    ///ENTIDAD --> REQUESTDTO
    public static OfferDTO toOfferDTO(Operation operation) {
        OfferDTO offerDTO = new OfferDTO();

        offerDTO.setIdOrganizacionDonante(String.valueOf(operation.getIdOrganization()));
        offerDTO.setIdOferta(String.valueOf(operation.getIdOperationMessage()));

        if (!operation.getOperationDonations().isEmpty()) {
            offerDTO.setDonaciones(
                    operation.getOperationDonations().stream()
                            .map(d -> OperationDonationMapper.toDTO(d))
                            .collect(Collectors.toList())
            );
        }

        return offerDTO;
    }



    ///PROTO --> TRANSFERDTO
    public static TransferDTO toTransferDTO(MyServiceClass.OperationRequest request) {
        TransferDTO transferDTO = new TransferDTO();

        transferDTO.setIdOrganizacionDonante("1");
        transferDTO.setIdSolicitud(String.valueOf(request.getIdOperationMessage()));

        if (!request.getDonationsList().isEmpty()) {
            transferDTO.setDonaciones(
                    request.getDonationsList().stream()
                            .map(d -> OperationDonationMapper.toDTO(d))
                            .collect(Collectors.toList())
            );
        }

        return transferDTO;
    }

    ///ENTIDAD --> REQUESTDTO
    public static TransferDTO toTransferDTO(Operation operation) {
        TransferDTO transferDTO = new TransferDTO();

        transferDTO.setIdOrganizacionDonante(String.valueOf(operation.getIdOrganization()));
        transferDTO.setIdSolicitud(String.valueOf(operation.getIdOperationMessage()));

        if (!operation.getOperationDonations().isEmpty()) {
            transferDTO.setDonaciones(
                    operation.getOperationDonations().stream()
                            .map(d -> OperationDonationMapper.toDTO(d))
                            .collect(Collectors.toList())
            );
        }

        return transferDTO;
    }



    ///PROTO --> REQUESTDTO
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

    ///ENTIDAD --> REQUESTDTO
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


    ///--------------------------------------MAPEO A ENTIDADAD-------------------------------------------------///
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

    ///--------------------------------------MAPEO A Proto-------------------------------------------------///
    public static MyServiceClass.OperationResponse toProto(Operation operation) {

        MyServiceClass.OperationResponse.Builder builder = MyServiceClass.OperationResponse.newBuilder()
                .setIdOperationMessage(operation.getIdOperationMessage())
                .setOperationType(operation.getOperationType().name())
                .setIdOrganization(operation.getIdOrganization())
                .setActive(operation.isActivate())
                .addAllDonations(operation.getOperationDonations().stream()
                        .map(OperationDonationMapper::toProto)
                        .collect(Collectors.toList())
                );

        return builder.build();
    }



}
