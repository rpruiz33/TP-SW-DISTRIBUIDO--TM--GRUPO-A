package com.grpc.grpc_server.services;

import java.util.List;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Donation;

public interface DonationService {

    List<Donation> getAllDonations();
    boolean updateDonation(MyServiceClass.UpdateDonationRequest request);
    boolean deleteDonation(MyServiceClass.DeleteDonationRequest request);
}
