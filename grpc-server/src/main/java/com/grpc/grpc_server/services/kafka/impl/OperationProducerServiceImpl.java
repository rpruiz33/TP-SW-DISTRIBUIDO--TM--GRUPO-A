

package com.grpc.grpc_server.services.kafka.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grpc.grpc_server.entities.grpc.Donation;
import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.OperationMapper;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO;
import com.grpc.grpc_server.producer.OperationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.common.quota.ClientQuotaAlteration.Op;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.grpc.grpc_server.services.kafka.OperationServiceConsumer;
import com.grpc.grpc_server.services.kafka.OperationServiceProducer;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.grpc.grpc_server.repositories.grpc.DonationRepository;
import com.grpc.grpc_server.repositories.kafka.OperationDonationRepository;
import com.grpc.grpc_server.repositories.kafka.OperationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationProducerServiceImpl implements OperationServiceProducer{

    @Autowired
    private  OperationRepository operationRepository;

    @Autowired
    private OperationProducer operationProducer;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private OperationDonationRepository operationDonationRepository; 

    @Autowired
    private OperationServiceConsumer operationServiceConsumer;

    @Transactional
    public String createAndSendOperation(Operation operation) {
        String result = "";


        // reutilizo el codigo del consumer para persistir un operation
        // envío el mensaje por kafka al topico correspondiente
        if(operationServiceConsumer.createOperation(operation) && operationProducer.sendOperationCreated(operation)){

            result = "creado y enviado correctamente";
        }else{

            result = "no se pudo crear y enviar";
        }

        return result;
    }

    ///valida la cantidad de la transferencia.
    ///valida que esté registrada la donacion en nuestro inventario.
    ///valida que exista una solicitud que responda a la transferencia
    public String processTransfer(Operation operation, String idOrganizacionSolicitante){

        String result = "";

        operationServiceConsumer.createOperation(operation);

        boolean flag = true;

        if (operation.getOperationDonations() != null) {

            List<OperationDonation> od = operation.getOperationDonations();
            int size = od.size();
            int i = 0;
            

            while (flag == true && i<size ) {

                Donation donation = donationRepository.findByCategoryAndDescription(od.get(i).getCategory(), od.get(i).getDescription());

                if( (donation == null) || (donation.getAmount() < od.get(i).getQuantity())){
                    result = "No existe esa donacion en el inventario o bien la cantidad supera el stock disponible";
                    flag = false;
                }
                
                i++;
            }

            if(flag != false){
                
                for (OperationDonation operationDonation : operation.getOperationDonations()) {

                    Donation donation = donationRepository.findByCategoryAndDescription(operationDonation.getCategory(), operationDonation.getDescription());
                    
                    donation.setAmount(donation.getAmount() - operationDonation.getQuantity());
                    donationRepository.save(donation);
                    
                }
                
            }
        
        }


        OperationMapper.TransferDTO dto = OperationMapper.toTransferDTO(operation);

        // Enviar a Kafka
        if(flag == true){

            if(operationProducer.sendTransferCreated(dto, idOrganizacionSolicitante) ){
                result = "creado y enviado correctamente";
            }else{
                result = "no se pudo enviar" + result;
            }
        }

        return result;
    }

    public String processCancelRequest(CancelRequestDTO cancelRequestDTO){
        
        String result = "baja de solicitud";

        operationServiceConsumer.processCancelRequest(cancelRequestDTO);
        
        // Enviar a Kafka
        if(operationProducer.downRequestCreated(cancelRequestDTO)){
            result = "dado de baja y enviado";
        }else{
            result = "no se pudo enviar";
        }

        return result;
    }

    public int existsRequest(Operation operation){
        int idOrganizacionSolicitante = 0;  

        return idOrganizacionSolicitante;
    }

    public List<Operation> getRequestList(boolean isExternal) {
        List<Operation> requests;

        if (isExternal){
            requests= operationRepository.findAllByOperationTypeAndIdOrganizationNotWithDonations(OperationType.SOLICITUD,1);
        }else {
            requests = operationRepository.findAllByOperationTypeAndIdOrganizationWithDonations(OperationType.SOLICITUD,1);
        }

        return  requests;
    }



}
