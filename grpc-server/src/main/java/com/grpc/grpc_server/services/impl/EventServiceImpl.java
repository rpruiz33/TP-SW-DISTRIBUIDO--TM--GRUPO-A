package com.grpc.grpc_server.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.protobuf.Int32Value;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.DeleteEventRequest;
import com.grpc.grpc_server.MyServiceClass.UpdateEventRequest;
import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.entities.MemberAtEvent;
import com.grpc.grpc_server.repositories.DonationsAtEventsRepository;
import com.grpc.grpc_server.repositories.EventRepository;
import com.grpc.grpc_server.repositories.MemberAtEventRepository;
import com.grpc.grpc_server.repositories.UserRepository;
import com.grpc.grpc_server.services.EventService;

/**
 * Implementación del servicio para la gestión de eventos solidarios.
 * Maneja la creación, modificación, eliminación y asignación de miembros a eventos.
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberAtEventRepository memberAtEventRepository;

    @Autowired
    private DonationsAtEventsRepository donationsAtEventsRepository;

    // Hora actual: 10:57 AM -03 del 12/09/2025
    private static final LocalDateTime NOW = LocalDateTime.of(2025, 9, 12, 10, 57);

    @Override
    public List<Event> getAllEvents() {
        
        try {
            return eventRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los eventos: " + e.getMessage(), e);
        }
    }
 
    @Transactional
    public boolean deleteEvent(DeleteEventRequest request){
        
        boolean result = false;
        
        Event event = eventRepository.findByIdEvent(request.getId());

        if(event == null){
            result = false;
        }else{
        
            // Borro relaciones primero
            memberAtEventRepository.deleteByEvent(event);
            donationsAtEventsRepository.deleteByEvent(event);

            // Despues borro el evento en sí
            eventRepository.delete(event);
            
            result = true;
        }

        return result;
    }
    
}