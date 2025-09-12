package com.grpc.grpc_server.services;

import java.time.LocalDateTime;
import java.util.List;

import com.grpc.grpc_server.entities.Event;

public interface EventService {
    List<Event> getAllEvents();

    Event createEvent(String nameEvent, String descriptionEvent, LocalDateTime dateTime, List<String> participantUsernames);
    Event updateEvent(Long id, String nameEvent, String descriptionEvent, LocalDateTime dateTime, List<String> participantUsernames);
    void deleteEvent(Long id);
    void assignMember(Long eventId, String username);
    void removeMember(Long eventId, String username);
}