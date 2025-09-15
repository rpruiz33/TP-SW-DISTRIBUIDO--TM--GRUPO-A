package com.grpc.grpc_server.grpc;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.CreateEventRequest;
import com.grpc.grpc_server.MyServiceClass.CreateEventResponse;
import com.grpc.grpc_server.MyServiceClass.DeleteEventRequest;
import com.grpc.grpc_server.MyServiceClass.DeleteEventResponse;
import com.grpc.grpc_server.MyServiceClass.EventProto;
import com.grpc.grpc_server.MyServiceClass.UpdateDonationResponse;
import com.grpc.grpc_server.EventServiceGrpc;
import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.mapper.EventMapper;
import com.grpc.grpc_server.services.EventService;

import com.grpc.grpc_server.services.impl.EventServiceImpl;
import io.grpc.stub.StreamObserver;

import org.hibernate.event.spi.DeleteEvent;
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

    @Override
    public void getAllEventsWithRelations(MyServiceClass.Empty request, StreamObserver<MyServiceClass.EventListResponse> responseObserver) {

         // 1️⃣ Obtener entidades desde la capa service
        List<Event> events = eventService.getAllEventsWithRelations();

        // 2️⃣ Mapear a Proto usando Mapper
        List<MyServiceClass.EventProto> grpcEvents = events.stream()
                .map(EventMapper::toProtoWithRelations)
                .collect(Collectors.toList());

        // 3️⃣ Construir y enviar la respuesta
        MyServiceClass.EventListResponse response = MyServiceClass.EventListResponse.newBuilder()
                .addAllEvents(grpcEvents)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteEvent(DeleteEventRequest request, StreamObserver<DeleteEventResponse> responseObserver){

        boolean result = eventService.deleteEvent(request);
        var responseBuilder = DeleteEventResponse.newBuilder();

        if (result){
            responseBuilder.setSuccess(true).setMessage("Evento Eliminado");
        }else{
            responseBuilder.setSuccess(false).setMessage("No se pudo eliminar el Evento");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void createEvent(CreateEventRequest request, StreamObserver<CreateEventResponse> responseObserver){
       
        boolean result = eventService.createEvent(request);
        var responseBuilder = CreateEventResponse.newBuilder();

        if (result){
            responseBuilder.setSuccess(true).setMessage("Evento Agregado");
        }else{
            responseBuilder.setSuccess(false).setMessage("No se pudo agregar el Evento");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void toggleMemberToEvent(MyServiceClass.ToggleMemberRequest request, StreamObserver<MyServiceClass.GenericResponse> responseObserver){

        boolean result =eventService.toggleMemberToEvent(request);
        var responseBuilder = MyServiceClass.GenericResponse.newBuilder();


        if (result && request.getAlreadyAssigned()){
            responseBuilder.setSuccess(true).setMessage("Miembro Desafectado con Éxito!");

        }else if(result && !request.getAlreadyAssigned()){
            responseBuilder.setSuccess(true).setMessage("Miembro Asignado con Éxito!");

        }else {
            responseBuilder.setSuccess(false).setMessage("Error al Asignar/Desafectar el Usuario!");

        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();

    }
}
