package com.grpc.grpc_server.services;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.entities.Event;

import io.grpc.stub.StreamObserver;

import java.util.List;

public interface DonationService {

    List<Donation> getAllDonations();
    boolean updateDonation(MyServiceClass.UpdateDonationRequest request);

}
