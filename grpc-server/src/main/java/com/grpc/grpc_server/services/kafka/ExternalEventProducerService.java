package com.grpc.grpc_server.services.kafka;

import com.grpc.grpc_server.entities.grpc.Event;
import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper;

import java.util.List;

public interface ExternalEventProducerService {


    String createExternalEvent(int idExternalEvent);
    String processCancelExternalEvent(int idEvent);
    List<ExternalEvent> getExternalEventList();
}
