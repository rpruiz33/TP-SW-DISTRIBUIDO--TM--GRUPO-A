package com.grpc.grpc_server.services.grpc;

import java.util.List;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.CreateEventRequest;
import com.grpc.grpc_server.MyServiceClass.DeleteEventRequest;
import com.grpc.grpc_server.MyServiceClass.UpdateEventRequest;
import com.grpc.grpc_server.entities.grpc.Event;

public interface EventService {
    
    List<Event> getAllEvents();
    List<Event> getAllEventsWithRelations();
    String deleteEvent(DeleteEventRequest request);
    boolean createEvent(CreateEventRequest request);
    boolean toggleMemberToEvent(MyServiceClass.ToggleMemberRequest request);

    String updateEvent(UpdateEventRequest request);

    /* 
    void assignMember(Integer eventId, String username);
    void removeMember(Integer eventId, String username);
    */

    Event getEventByName(String nameEvent);
    Event getEventIdEvent(int idEvent);
}