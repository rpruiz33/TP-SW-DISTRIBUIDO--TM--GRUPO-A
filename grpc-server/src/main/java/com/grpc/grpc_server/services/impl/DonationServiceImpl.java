package com.grpc.grpc_server.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import com.grpc.grpc_server.DonationServiceGrpc;
import com.grpc.grpc_server.MyServiceClass.*;
import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.mapper.DonationMapper;
import com.grpc.grpc_server.repositories.DonationRepository;
import com.grpc.grpc_server.services.DonationService;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class DonationServiceImpl implements DonationService {

    @Autowired
    private DonationRepository donationRepository;



    public void getAllDonations(Empty request, StreamObserver<DonationListResponse> responseObserver){

        List<Donation> lstDonation = donationRepository.findAllWithEvents();

        DonationListResponse dl = DonationListResponse.newBuilder()
                .addAllDonations(lstDonation.stream().map(x ->DonationMapper.toProtoWithEvents(x))
                        .collect(Collectors.toList())).build();

        responseObserver.onNext(dl);
        responseObserver.onCompleted();
    }


    public void updateDonation(UpdateDonationRequest request, StreamObserver<UpdateDonationResponse> responseObserver){
        var responseBuilder = UpdateDonationResponse.newBuilder();

        Donation d = donationRepository.findById(request.getId());

        if (d == null) {

            responseBuilder.setSuccess(false).setMessage("Donacion no encontrada");
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } else {
            d.setDescription(request.getDescription());
            d.setAmount(request.getAmount());

            donationRepository.save(d);
            responseBuilder.setSuccess(true).setMessage("Donacion actualizada");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
