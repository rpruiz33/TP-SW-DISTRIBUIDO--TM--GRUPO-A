package com.grpc.grpc_server.services;

import java.time.LocalDateTime;
import java.util.List;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.DeleteEventRequest;
import com.grpc.grpc_server.entities.Event;

public interface EventService {
    List<Event> getAllEvents();
    boolean deleteEvent(DeleteEventRequest request);

    /* 
    Event createEvent(String nameEvent, String descriptionEvent, LocalDateTime dateTime, List<String> participantUsernames);
    Event updateEvent(Integer id, String nameEvent, String descriptionEvent, LocalDateTime dateTime, List<String> participantUsernames);
    void assignMember(Integer eventId, String username);
    void removeMember(Integer eventId, String username);
    */
}