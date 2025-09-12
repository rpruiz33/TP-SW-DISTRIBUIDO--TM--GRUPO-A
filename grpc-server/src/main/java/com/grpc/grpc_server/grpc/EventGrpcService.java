package com.grpc.grpc_server.grpc;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.EventServiceGrpc;
import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.mapper.EventMapper;
import com.grpc.grpc_server.services.EventService;

import com.grpc.grpc_server.services.impl.EventServiceImpl;
import io.grpc.stub.StreamObserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;


import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class EventGrpcService extends EventServiceGrpc.EventServiceImplBase {

        @Autowired
        private EventServiceImpl eventService;

    @Override
    public void getAllEvents(MyServiceClass.Empty request, StreamObserver<MyServiceClass.EventListResponse> responseObserver) {
        eventService.getAllEvents();
    }
}
