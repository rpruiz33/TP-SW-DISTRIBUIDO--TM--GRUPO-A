package com.grpc.grpc_server.services.impl;

import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.repositories.EventRepository;
import com.grpc.grpc_server.services.EventService;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}
