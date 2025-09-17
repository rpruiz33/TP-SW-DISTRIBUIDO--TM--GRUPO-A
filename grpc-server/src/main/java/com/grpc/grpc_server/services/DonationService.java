package com.grpc.grpc_server.services;

import java.util.List;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.entities.Donation;

public interface DonationService {

    List<Donation> getAllDonations();
    List<Donation> getActiveDonations();
    boolean updateDonation(MyServiceClass.UpdateDonationRequest request);
    boolean deleteDonation(MyServiceClass.DeleteDonationRequest request);
    boolean altaDonation(MyServiceClass.AltaDonationRequest request);
    
    Donation getDonationByDescription(String description);
}
