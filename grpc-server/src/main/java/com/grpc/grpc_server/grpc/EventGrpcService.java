package com.grpc.grpc_server.grpc;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.EventServiceGrpc;
import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.mapper.EventMapper;
import com.grpc.grpc_server.services.EventService;

import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;


import java.util.List;

@GrpcService
public class EventGrpcService extends EventServiceGrpc.EventServiceImplBase {

    private final EventService eventService;

    public EventGrpcService(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void getAllEvents(MyServiceClass.Empty request, StreamObserver<MyServiceClass.EventListResponse> responseObserver) {

        List<Event> events = eventService.getAllEvents();

        MyServiceClass.EventListResponse.Builder responseBuilder = MyServiceClass.EventListResponse.newBuilder();

        for (Event e : events) {
            responseBuilder.addEvents(EventMapper.toProto(e));
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
