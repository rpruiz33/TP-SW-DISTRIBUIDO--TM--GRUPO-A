package com.grpc.grpc_server.services.kafka;

import com.grpc.grpc_server.entities.kafka.ExternalEvent;

public interface ExternalEventConsumer {

    void saveExternalEvent(ExternalEvent e);

}
