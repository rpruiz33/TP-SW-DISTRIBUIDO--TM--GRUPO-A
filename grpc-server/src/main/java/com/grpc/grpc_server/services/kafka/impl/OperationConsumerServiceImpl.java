package com.grpc.grpc_server.services.kafka.impl;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grpc.grpc_server.entities.grpc.Donation;
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
    
public class OperationConsumerServiceImpl implements OperationServiceConsumer{
    
    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private OperationDonationRepository operationDonationRepository; 

    ///PERSISTIR UNA OPERACION 
    @Override
    public boolean createOperation(Operation operation) {

        boolean result = false;
        
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

            result = true;

        }else{
            log.info("Ya existe");
        }

        return result;
    }

    ///ELIMINAR UNA SOLICITUD
    @Transactional
    public void processCancelRequest(CancelRequestDTO cancelRequestDTO) {
        try {

            log.info("Mensaje recibido (BAJA SOLICITUD): {}", cancelRequestDTO);

            // Buscar operación por idOperationMessage y que sea solicitud
            Operation deleteOperation = operationRepository
                    .findByIdOperationMessageAndOperationType(cancelRequestDTO.getIdSolicitud(), OperationType.SOLICITUD)
                    .orElse(null);

                if ( deleteOperation != null) {
                    
                    if(deleteOperation.getIdOrganization() == cancelRequestDTO.getIdOrganizacionSolicitante()){

                        if(deleteOperation.isActivate()){

                            // Marcar la operación como inactiva
                            deleteOperation.setActivate(false);
                            deleteOperation.setDateModification(LocalDateTime.now());

                            if (deleteOperation.getOperationDonations() != null) {
                                for (OperationDonation od : deleteOperation.getOperationDonations()) {
                                
                                    od.setActivate(false);
                                    operationDonationRepository.save(od);
                                }
                            }

                            //Persistir cambio
                            operationRepository.save(deleteOperation);

                        }else{
                            log.info("Ya está borrada");
                        }

                    }else{
                        log.info("No podés eliminar esta solicitud, no te corresponde");
                    }

                }else{
                    log.info("No existe");
                }

        } catch (Exception e) {
            log.error("Error inesperado procesando baja de solicitud", e);
        }
    }

    ///CONSUMIR TRANSFERENCIA
    /// 1) ¿si no existe la donation deberia crearla?
    @Override
    public void processTransfer(Operation operation) {
        try {

            if(createOperation(operation)){

                if (operation.getOperationDonations() != null) {

                    for (OperationDonation od : operation.getOperationDonations()) {

                        Donation donation = donationRepository.findByCategoryAndDescription(od.getCategory(), od.getDescription());
                        
                        if(donation != null){

                            donation.setAmount(donation.getAmount() + od.getQuantity());
                            donationRepository.save(donation);

                        }else{
                            // Crear nueva donación
                            Donation newDonation = new Donation();
                            newDonation.setCategory(od.getCategory());
                            newDonation.setDescription(od.getDescription());
                            newDonation.setAmount(od.getQuantity());
                            newDonation.setDateRegistration(LocalDateTime.now());
                            newDonation.setRemoved(false);


                            donationRepository.save(newDonation);
                            log.info("Donación creada: {} - {} (cantidad: {})", od.getCategory(), od.getDescription(), od.getQuantity());
                        }
                    }
                }

                log.info("Transferencias guardadas en la base de datos.");

            }
    
        } catch (Exception e) {
            log.error("Error procesando transferencia", e);
        }
    }


}