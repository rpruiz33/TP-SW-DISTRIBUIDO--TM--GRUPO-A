package com.grpc.grpc_server.grpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import com.grpc.grpc_server.DonationsAtEventsServiceGrpc;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.CreateDonationAtEventRequest;
import com.grpc.grpc_server.MyServiceClass.DeleteEventRequest;
import com.grpc.grpc_server.MyServiceClass.DeleteEventResponse;
import com.grpc.grpc_server.MyServiceClass.GenericResponse;
import com.grpc.grpc_server.entities.DonationsAtEvents;
import com.grpc.grpc_server.services.DonationsAtEventsService;
import com.grpc.grpc_server.services.EventService;


import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class DonationsAtEventsGrpcService extends  DonationsAtEventsServiceGrpc.DonationsAtEventsServiceImplBase{
    
    @Autowired
    private DonationsAtEventsService donationsAtEventsService;

    @Override
    public void createDonationAtEvent(CreateDonationAtEventRequest request, StreamObserver<GenericResponse> responseObserver){

        boolean result = donationsAtEventsService.registerDonationAtEvent(request);

        var responseBuilder = GenericResponse.newBuilder();

        if (result){
            responseBuilder.setSuccess(true).setMessage("Agregado Correctamente");
        }else{
            responseBuilder.setSuccess(false).setMessage("No se pudo Agregar");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
