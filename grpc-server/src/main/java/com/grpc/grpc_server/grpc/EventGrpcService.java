package com.grpc.grpc_server.grpc;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.EventProto;
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
        private EventService eventService;

    @Override
    public void getAllEvents(MyServiceClass.Empty request, StreamObserver<MyServiceClass.EventListResponse> responseObserver) {

         // 1️⃣ Obtener entidades desde la capa service
        List<Event> events = eventService.getAllEvents();

        // 2️⃣ Mapear a Proto usando Mapper
        List<MyServiceClass.EventProto> grpcEvents = events.stream()
                .map(EventMapper::toProto)
                .collect(Collectors.toList());

        // 3️⃣ Construir y enviar la respuesta
        MyServiceClass.EventListResponse response = MyServiceClass.EventListResponse.newBuilder()
                .addAllEvents(grpcEvents)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
