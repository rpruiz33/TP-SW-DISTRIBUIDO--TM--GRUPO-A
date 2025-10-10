package com.grpc.grpc_server.services.kafka;

import java.util.List;

import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationDonation;

public interface OperationServiceProducer {




    void sendOperationCreated(Operation operation);
    void sendTransfer(Operation operation, List<OperationDonation> donations);
    void sendCancelRequest(int idOffer, int idOrganization);
    void sendOffer(Operation operation, List<OperationDonation> donations);
}
