package com.grpc.grpc_server.grpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;


import com.grpc.grpc_server.DonationsAtEventsServiceGrpc;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.DonationAtEventRequest;
import com.grpc.grpc_server.MyServiceClass.GenericResponse;
import com.grpc.grpc_server.MyServiceClass.GetAllDonationsAtEventRequest;
import com.grpc.grpc_server.entities.DonationsAtEvents;
import com.grpc.grpc_server.mapper.DonationsAtEventsMapper;
import com.grpc.grpc_server.services.DonationsAtEventsService;


import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class DonationsAtEventsGrpcService extends  DonationsAtEventsServiceGrpc.DonationsAtEventsServiceImplBase{
    
    @Autowired
    private DonationsAtEventsService donationsAtEventsService;

    @Override
    public void createDonationAtEvent(DonationAtEventRequest request, StreamObserver<GenericResponse> responseObserver){

        boolean result = donationsAtEventsService.registerDonationAtEvent(request);

        var responseBuilder = GenericResponse.newBuilder();

        if (result){
            responseBuilder.setSuccess(true).setMessage("Donacion Agregada al Evento Correctamente");
        }else{
            responseBuilder.setSuccess(false).setMessage("No se pudo Agregar Donacion al Evento");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateDonationAtEvent(DonationAtEventRequest request, StreamObserver<GenericResponse> responseObserver){

        boolean result = donationsAtEventsService.updateDonationAtEvent(request);

        var responseBuilder = GenericResponse.newBuilder();

        if (result){
            responseBuilder.setSuccess(true).setMessage("Donacion Modificada al Evento Correctamente");
        }else{
            responseBuilder.setSuccess(false).setMessage("No se pudo Modificar Donacion al Evento");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }


    @Override
    public void getAllDonationsAtEvent(GetAllDonationsAtEventRequest request, StreamObserver<MyServiceClass.GetAllDonationsAtEventResponse> responseObserver) {

         // 1️⃣ Obtener entidades desde la capa service
        List<DonationsAtEvents> donationsAtEvents = donationsAtEventsService.getAllDonationsAtEvent(request);

        // 2️⃣ Mapear a Proto usando Mapper
        List<MyServiceClass.EventDonationProto> grpcDonationsAtEvents = donationsAtEvents.stream()
                .map(DonationsAtEventsMapper::toProto)
                .collect(Collectors.toList());

        // 3️⃣ Construir y enviar la respuesta
        MyServiceClass.GetAllDonationsAtEventResponse response = MyServiceClass.GetAllDonationsAtEventResponse.newBuilder()
                .addAllDonations(grpcDonationsAtEvents)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
