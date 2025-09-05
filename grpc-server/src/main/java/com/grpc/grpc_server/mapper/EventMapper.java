package com.grpc.grpc_server.mapper;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Event;

import java.time.format.DateTimeFormatter;

public class EventMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static MyServiceClass.Event toProto(Event e) {
        return MyServiceClass.Event.newBuilder()
                .setId(e.getIdEvent())
                .setNameEvent(e.getNameEvent())
                .setDescriptionEvent(e.getDescriptionEvent())
                .setDateRegistration(e.getDateRegistration().format(FORMATTER))
                .build();
    }
}
