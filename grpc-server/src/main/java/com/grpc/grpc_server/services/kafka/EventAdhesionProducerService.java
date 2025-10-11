package com.grpc.grpc_server.services.kafka;

import com.grpc.grpc_server.entities.kafka.EventAdhesion;

public interface EventAdhesionProducerService {

    String saveEventAdhesion (int idEvent, String emailVolunteer);

}
