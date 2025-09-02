package com.grpc.grpc_server.serviceimp;

import com.grpc.grpc_server.MyServiceClass;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import com.grpc.grpc_server.EventServiceGrpc;
import io.grpc.stub.StreamObserver;

import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.repositories.EventRepository;

@GrpcService
public class EventServiceImplementation extends EventServiceGrpc.EventServiceImplBase{ 
        
    @Autowired
    private EventRepository eventRepository;

    @Override
    public void getAllEvents(MyServiceClass.Empty request, StreamObserver<MyServiceClass.EventListResponse> responseObserver) {

        // Obtener todos los eventos de la DB
        List<Event> events = eventRepository.findAll();

        MyServiceClass.EventListResponse.Builder responseBuilder = MyServiceClass.EventListResponse.newBuilder();

        for (Event e : events) {
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");   // conversion a string (mejorables a protocolo nativo)
            MyServiceClass.Event grpcEvent = MyServiceClass.Event.newBuilder()
                    .setId(e.getIdEvent())
                    .setNameEvent(e.getNameEvent())
                    .setDescriptionEvent(e.getDescriptionEvent())
                    .setDateRegistration(e.getDateRegistration().format(formatter))
                    .build();

            responseBuilder.addEvents(grpcEvent);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
