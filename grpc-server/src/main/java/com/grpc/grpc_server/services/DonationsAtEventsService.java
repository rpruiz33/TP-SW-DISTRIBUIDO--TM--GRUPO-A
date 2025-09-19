package com.grpc.grpc_server.services;

import java.util.List;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.DonationAtEventRequest;
import com.grpc.grpc_server.MyServiceClass.GetAllDonationsAtEventRequest;
import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.entities.DonationsAtEvents;
import com.grpc.grpc_server.entities.Event;

public interface DonationsAtEventsService {
    
    boolean registerDonationAtEvent(DonationAtEventRequest request);
    boolean updateDonationAtEvent(DonationAtEventRequest request);
    List<DonationsAtEvents> getAllDonationsAtEvent(GetAllDonationsAtEventRequest request);
    DonationsAtEvents getDonationsAtEvents(Event event, Donation donation);
} 