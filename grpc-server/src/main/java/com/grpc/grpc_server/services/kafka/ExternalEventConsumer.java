package com.grpc.grpc_server.services.kafka;

import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import com.grpc.grpc_server.mapper.kafka.ExternalEventMapper;

public interface ExternalEventConsumer {

    void saveExternalEvent(ExternalEvent e);
    void processCancelExternalEvent(ExternalEventMapper.CancelExternalEventDTO dto);

}
