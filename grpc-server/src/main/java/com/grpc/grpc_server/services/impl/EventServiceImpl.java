package com.grpc.grpc_server.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.protobuf.Int32Value;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.CreateEventRequest;
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

    public List<Event> getAllEventsWithRelations(){

        System.out.println("HOLAAAAA");

        // 1️⃣ Traemos eventos con donaciones
        List<Event> eventsWithDonations = eventRepository.findAllWithDonations();

        // 2️⃣ Traemos eventos con miembros
        List<Event> eventsWithMembers = eventRepository.findAllWithMembers();

        // 3️⃣ Combinamos los miembros dentro de los eventos
        for (Event e : eventsWithDonations) {
            Event memberEvent = eventsWithMembers.stream()
                .filter(em -> em.getIdEvent().equals(e.getIdEvent()))
                .findFirst()
                .orElse(null);

            if (memberEvent != null) {
                e.setMembers(memberEvent.getMembers()); // List<MemberAtEvent>
            }
        }

        System.out.println("HOLAAAAA22");

        // 4️⃣ Retornamos la lista combinada
        return eventsWithDonations;
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

    public boolean createEvent(CreateEventRequest request){

        boolean result = false;

        ///valido que no exista un evento con el mismo nombre
        
        Event e = eventRepository.findByNameEvent(request.getNameEvent());
        

        if(e == null){
           
             Event event = new Event();
           
            event.setNameEvent(request.getNameEvent());
            event.setDescriptionEvent(request.getDescriptionEvent());
 
            LocalDateTime fecha = LocalDateTime.parse(request.getDateRegistration());
            event.setDateRegistration(fecha);
            
            
            eventRepository.save(event);
            result = true;
        }

        return result;
    }
    

    public Event getEventByName(String nameEvent){
        return eventRepository.findByNameEvent(nameEvent);
    }

    
}