package com.grpc.grpc_server.services.kafka;

import com.grpc.grpc_server.entities.kafka.Operation;


public interface OperationService {

    void createOperation(Operation operation);
    void processTransfer(String message);
}
