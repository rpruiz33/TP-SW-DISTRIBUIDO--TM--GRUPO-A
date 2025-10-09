package com.grpc.grpc_server.services.kafka.impl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import com.grpc.grpc_server.entities.grpc.Donation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO;
import com.grpc.grpc_server.repositories.grpc.DonationRepository;
import com.grpc.grpc_server.repositories.kafka.OperationDonationRepository;
import com.grpc.grpc_server.repositories.kafka.OperationRepository;
import com.grpc.grpc_server.services.kafka.OperationServiceConsumer;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
    
public class OpeServiceImplConsumer implements OperationServiceConsumer{
    
    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private OperationDonationRepository operationDonationRepository; 

    ///-------------------------------------OPERACIONES--------------------------------------------------////
    @Override
    public void createOperation(Operation operation) {
        
        if( operationRepository.findByIdOperationMessageAndOperationType(operation.getIdOperationMessage(), operation.getOperationType()).isEmpty()){
            
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

        }else{
            log.info("Ya existe");
        }

    }

    ///-------------------------------------BAJA SOLICITUD--------------------------------------------------////
    @Transactional
    public void processCancelRequest(CancelRequestDTO cancelRequestDTO) {
        try {

            log.info("Mensaje recibido (BAJA SOLICITUD): {}", cancelRequestDTO);

            // Buscar operación por idOperationMessage y que sea solicitud
            Operation deleteOperation = operationRepository
                    .findByIdOperationMessageAndOperationType(cancelRequestDTO.getIdSolicitud(), OperationType.SOLICITUD)
                    .orElse(null);

                if ( deleteOperation != null) {
                    
                    if(deleteOperation.isActivate()){

                        // Marcar la operación como inactiva
                        deleteOperation.setActivate(false);
                        deleteOperation.setDateModification(LocalDateTime.now());

                    }else{
                        log.info("Ya está borrada");
                    }

                }else{
                    log.info("No existe");
                }

            //Persistir cambios
            operationRepository.save(deleteOperation);

            log.info("Solicitud de donación {} de la organización {} dada de baja correctamente",
                    cancelRequestDTO.getIdSolicitud(), cancelRequestDTO.getIdOrganizacionSolicitante());

        } catch (Exception e) {
            log.error("Error inesperado procesando baja de solicitud", e);
        }
    }

    ///-------------------------------------TRANSFERENCIAS --------------------------------------------------////
    /// 1) solo deberia consumir transferencia ajenas
    /// 2) si no existe la donation deberia crearla?
    /// 3) que pasa si no se crea la operation
    @Override
    public void processTransfer(Operation operation) {
        try {

            createOperation(operation);

            if (operation.getOperationDonations() != null) {
                for (OperationDonation od : operation.getOperationDonations()) {

                    Donation donation = donationRepository.findByCategoryAndDescription(od.getCategory(), od.getDescription());
                    
                    if(donation != null){
                        donation.setAmount(donation.getAmount() + od.getQuantity());
                        donationRepository.save(donation);
                    }
                    
                }
            }

            log.info("Transferencias guardadas en la base de datos.");

        } catch (Exception e) {
            log.error("Error procesando transferencia", e);
        }
    }


}