package com.grpc.grpc_server.services;

import com.grpc.grpc_server.entities.Event;
import java.util.List;

public interface EventService {
    List<Event> getAllEvents();
}
