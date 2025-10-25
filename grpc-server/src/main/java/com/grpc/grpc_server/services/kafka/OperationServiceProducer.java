package com.grpc.grpc_server.services.kafka;

import java.util.List;

import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;
import com.grpc.grpc_server.entities.kafka.OperationType;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO;

public interface OperationServiceProducer {




    String createAndSendOperation(Operation operation);
    String processTransfer(Operation operation, String idOrganizacionSolicitante);
    String processCancelRequest(CancelRequestDTO cancelRequestDTO);
    List<Operation> getRequestList(boolean isExternal);
    List<Operation> getOfferList();

    //void sendTransfer(Operation operation, List<OperationDonation> donations);
    //void sendCancelRequest(int idOffer, int idOrganization);
    //void sendOffer(Operation operation, List<OperationDonation> donations);
}
