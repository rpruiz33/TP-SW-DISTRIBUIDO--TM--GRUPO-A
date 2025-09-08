package com.grpc.grpc_server.services.impl;

import com.grpc.grpc_server.DonationServiceGrpc;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceGrpc;
import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.mapper.DonationMapper;
import com.grpc.grpc_server.repositories.DonationRepository;
import com.grpc.grpc_server.services.DonationService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@GrpcService
public class DonationServiceImpl extends DonationServiceGrpc.DonationServiceImplBase implements DonationService {

    @Autowired
    private DonationRepository donationRepository;


    @Override
    public void getAllDonations(MyServiceClass.Empty request, StreamObserver<MyServiceClass.DonationListResponse> responseObserver){

        log.debug("Entrando al grpc server");
        List<Donation> lstDonation = donationRepository.findAllWithEvents();
        log.debug("Salimos con las donaciones");

        MyServiceClass.DonationListResponse dl = MyServiceClass.DonationListResponse.newBuilder()
                .addAllDonations(lstDonation.stream().map(x ->DonationMapper.toProto(x))
                        .collect(Collectors.toList())).build();
        responseObserver.onNext(dl);
        responseObserver.onCompleted();
    }



}
