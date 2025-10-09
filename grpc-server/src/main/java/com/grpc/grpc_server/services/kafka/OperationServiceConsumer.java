package com.grpc.grpc_server.services.kafka;

import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.mapper.kafka.OperationMapper.CancelRequestDTO;


public interface OperationServiceConsumer {

    void createOperation(Operation operation);
    void processTransfer(String message);
    void processOfferMessage(String message);
    void processCancelRequest(CancelRequestDTO cancelRequestDTO);
  
}