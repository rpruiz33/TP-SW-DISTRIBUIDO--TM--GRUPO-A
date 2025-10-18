package com.grpc.grpc_server.services.grpc.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.grpc.grpc_server.services.kafka.impl.ExternalEventProducerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.CreateEventRequest;
import com.grpc.grpc_server.MyServiceClass.DeleteEventRequest;
import com.grpc.grpc_server.MyServiceClass.UpdateEventRequest;
import com.grpc.grpc_server.entities.grpc.Event;
import com.grpc.grpc_server.entities.grpc.MemberAtEvent;
import com.grpc.grpc_server.entities.grpc.User;
import com.grpc.grpc_server.repositories.grpc.DonationsAtEventsRepository;
import com.grpc.grpc_server.repositories.grpc.EventRepository;
import com.grpc.grpc_server.repositories.grpc.MemberAtEventRepository;
import com.grpc.grpc_server.repositories.grpc.UserRepository;
import com.grpc.grpc_server.services.grpc.EventService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio para la gestión de eventos solidarios.
 * Maneja la creación, modificación, eliminación y asignación de miembros a eventos.
 */

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberAtEventRepository memberAtEventRepository;

    @Autowired
    private DonationsAtEventsRepository donationsAtEventsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExternalEventProducerServiceImpl externalEventProducerService;


    // Hora actual: 10:57 AM -03 del 12/09/2025
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Override
    public List<Event> getAllEvents() {
        
        try {
            return eventRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los eventos: " + e.getMessage(), e);
        }
    }

    public List<Event> getAllEventsWithRelations(){


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


        // 4️⃣ Retornamos la lista combinada
        return eventsWithDonations;
    }
 
    @Transactional
    public String deleteEvent(DeleteEventRequest request){
        
        String result ;
        
        Event event = eventRepository.findByIdEvent(request.getId());

        if(event != null ){

            if (event.getDateRegistration().isAfter(NOW)){
                // Borro relaciones primero
                memberAtEventRepository.deleteByEvent(event);
                donationsAtEventsRepository.deleteByEvent(event);

                // Despues borro el evento en sí
                eventRepository.delete(event);

                //IMPLEMENTACION CON KAFKA
                String resultKafka = externalEventProducerService.processCancelExternalEvent(event.getIdEvent());

                switch (resultKafka) {

                    case "Evento externo eliminado":
                    case "No se publico este evento en externos":
                        result = "Evento eliminado con exito-" + resultKafka;
                        break;

                    case "Error enviando mensaje de kafka":
                    default:
                        result=resultKafka;
                    break;
                }
            }else{
                result="No se puede eliminar un evento pasado";
            }

        } else {

            result="No se encontro el evento a eliminar";
        }

        return result;
    }

    public boolean createEvent(CreateEventRequest request){

        boolean result = false;

        ///valido que no exista un evento con el mismo nombre
        
        Event e = eventRepository.findByNameEvent(request.getNameEvent());
        LocalDateTime fecha = LocalDateTime.parse(request.getDateRegistration());

        if(e == null && fecha.isAfter(NOW)){
           
            Event event = new Event();
           
            event.setNameEvent(request.getNameEvent());
            event.setDescriptionEvent(request.getDescriptionEvent());
            event.setDateRegistration(fecha);
            
            eventRepository.save(event);
            result = true;
        }

        return result;
    }

    @Transactional
    public boolean toggleMemberToEvent(MyServiceClass.ToggleMemberRequest request){

        boolean result =false;
        Event mainEvent = eventRepository.findByIdEvent(request.getEventId());
        User member = userRepository.findByUsername(request.getUsername()).orElse(null);

        //Si no se encontró alguna de las entidadees
        if (mainEvent == null || member == null){
            return result;
        }


        //Logica en caso que ya este asignado el usuario al evento
        if (request.getAlreadyAssigned()){

            MemberAtEvent existing = memberAtEventRepository.findByEventAndUser(mainEvent, member);

            if(existing != null){
                memberAtEventRepository.delete(existing);

                // Actualizar listas
                mainEvent.getMembers().remove(existing);
                member.getEvents().remove(existing);
            }

            result=true;

        }else{ //Logica en caso que se tenga que asignar el usuario

            MemberAtEvent newMember = new MemberAtEvent();
            newMember.setEvent(mainEvent);
            newMember.setUser(member);
            memberAtEventRepository.save(newMember);
            // Actualizar listas
            mainEvent.getMembers().add(newMember);
            member.getEvents().add(newMember);

            result=true;
        }

        return result;
    }

    public Event getEventByName(String nameEvent){
        return eventRepository.findByNameEvent(nameEvent);
    }

    public Event getEventIdEvent(int idEvent){
        return eventRepository.findByIdEvent(idEvent);
    }


    public String updateEvent(UpdateEventRequest request) {

        Event event = getEventIdEvent(request.getId());
        if (event == null) {
            return "Evento no encontrado";
        }

        // Validar nombre duplicado solo si el nuevo nombre es distinto
        if (request.getNameEvent() != null && !request.getNameEvent().isEmpty() &&
                !request.getNameEvent().equals(event.getNameEvent())) {

            Event existing = getEventByName(request.getNameEvent());
            if (existing != null && !existing.getIdEvent().equals(event.getIdEvent())) {
                return "Otro evento ya tiene registrado este nombre";
            }
            event.setNameEvent(request.getNameEvent());
        }

        // Actualizar descripción si se envió
        if (request.getDescriptionEvent() != null && !request.getDescriptionEvent().isEmpty()) {
            event.setDescriptionEvent(request.getDescriptionEvent());
        }

        // Actualizar fecha si se envió
        if (request.getDateRegistration() != null && !request.getDateRegistration().isEmpty()) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime nuevaDateEvent = LocalDateTime.parse(request.getDateRegistration(), formatter);
            LocalDateTime currentDateEvent = event.getDateRegistration();
            LocalDateTime today = LocalDateTime.now();

            if (currentDateEvent.isBefore(today) && !nuevaDateEvent.isBefore(today)) {
                return "La nueva fecha del evento debe mantenerse en el pasado";
            } else if (currentDateEvent.isAfter(today) && !nuevaDateEvent.isAfter(today)) {
                return "La nueva fecha del evento debe mantenerse en el futuro";
            }

            event.setDateRegistration(nuevaDateEvent);
        }

        eventRepository.save(event);
        return "Evento actualizado con éxito";
    }


}

