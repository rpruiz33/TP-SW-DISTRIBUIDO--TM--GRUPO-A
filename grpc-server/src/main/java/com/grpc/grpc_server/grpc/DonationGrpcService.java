package com.grpc.grpc_server.grpc;

import com.grpc.grpc_server.DonationServiceGrpc;
import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.services.impl.DonationServiceImpl;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class DonationGrpcService extends DonationServiceGrpc.DonationServiceImplBase {

    @Autowired
    DonationServiceImpl donationService;

    @Override
    public void getAllDonations(MyServiceClass.Empty request, StreamObserver<MyServiceClass.DonationListResponse> responseObserver){
        donationService.getAllDonations(request,responseObserver);
    }

    @Override
    public void updateDonation(MyServiceClass.UpdateDonationRequest request, StreamObserver<MyServiceClass.UpdateDonationResponse> responseObserver){
        donationService.updateDonation(request,responseObserver);
    }
}
