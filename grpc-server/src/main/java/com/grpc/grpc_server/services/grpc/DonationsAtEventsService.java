package com.grpc.grpc_server.services.grpc;

import java.util.List;

import com.grpc.grpc_server.MyServiceClass.DonationAtEventRequest;
import com.grpc.grpc_server.MyServiceClass.GetAllDonationsAtEventRequest;
import com.grpc.grpc_server.entities.grpc.Donation;
import com.grpc.grpc_server.entities.grpc.DonationsAtEvents;
import com.grpc.grpc_server.entities.grpc.Event;

public interface DonationsAtEventsService {
    
    boolean registerDonationAtEvent(DonationAtEventRequest request);
    boolean updateDonationAtEvent(DonationAtEventRequest request);
    List<DonationsAtEvents> getAllDonationsAtEvent(GetAllDonationsAtEventRequest request);
    DonationsAtEvents getDonationsAtEvents(Event event, Donation donation);
} 