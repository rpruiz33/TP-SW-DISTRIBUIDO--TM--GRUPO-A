package com.grpc.grpc_server.services;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Donation;
import io.grpc.stub.StreamObserver;

import java.util.List;

public interface DonationService {

    void getAllDonations(MyServiceClass.Empty request, StreamObserver<MyServiceClass.DonationListResponse> responseObserver);
}
