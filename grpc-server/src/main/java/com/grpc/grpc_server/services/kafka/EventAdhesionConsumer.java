package com.grpc.grpc_server.services.kafka;

import com.grpc.grpc_server.entities.kafka.EventAdhesion;

public interface EventAdhesionConsumer {

    void saveEventAdhesion (EventAdhesion eventAdhesion);
}
