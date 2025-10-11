package com.grpc.grpc_server.services.kafka;

import com.grpc.grpc_server.entities.grpc.Event;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper;

public interface ExternalEventProducerService {


    String createExternalEvent(int idExternalEvent);
    String processCancelExternalEvent(int idEvent);

}
