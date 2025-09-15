package com.grpc.grpc_server.grpc;

import com.grpc.grpc_server.DonationServiceGrpc;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.DeleteDonationRequest;
import com.grpc.grpc_server.MyServiceClass.DeleteDonationResponse;
import com.grpc.grpc_server.MyServiceClass.UpdateDonationResponse;
import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.mapper.DonationMapper;
import com.grpc.grpc_server.services.impl.DonationServiceImpl;

import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.sql.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class DonationGrpcService extends DonationServiceGrpc.DonationServiceImplBase {

    @Autowired
    DonationServiceImpl donationService;

    @Override
    public void getAllDonations(MyServiceClass.Empty request, StreamObserver<MyServiceClass.DonationListResponse> responseObserver){

         // 1️⃣ Obtener entidades desde la capa service
        List<Donation> donations = donationService.getAllDonations();

        // 2️⃣ Mapear a Proto usando Mapper
        List<MyServiceClass.DonationProto> grpcDonations = donations.stream()
                .map(DonationMapper::toProtoWithEvents)
                .collect(Collectors.toList());

        // 3️⃣ Construir y enviar la respuesta
        MyServiceClass.DonationListResponse response = MyServiceClass.DonationListResponse.newBuilder()
                .addAllDonations(grpcDonations)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();
        
    }

    @Override
    public void updateDonation(MyServiceClass.UpdateDonationRequest request, StreamObserver<MyServiceClass.UpdateDonationResponse> responseObserver){
        
        boolean result = donationService.updateDonation(request);
        var responseBuilder = UpdateDonationResponse.newBuilder();

        if (result){
            responseBuilder.setSuccess(true).setMessage("Donacion actualizada");
        }else{
            responseBuilder.setSuccess(false).setMessage("No se pudo actualizar la Donacioón");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

     @Override
    public void deleteDonation(MyServiceClass.DeleteDonationRequest request, StreamObserver<MyServiceClass.DeleteDonationResponse> responseObserver){
         boolean result = donationService.deleteDonation(request);
        var responseBuilder = DeleteDonationResponse.newBuilder();

        if (result){
            responseBuilder.setSuccess(true).setMessage("Donacion eliminada");
        }else{
            responseBuilder.setSuccess(false).setMessage("No se pudo eliminar la Donacioón");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    }

