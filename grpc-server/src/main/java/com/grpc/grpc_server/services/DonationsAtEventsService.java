package com.grpc.grpc_server.services;

import java.util.List;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.CreateDonationAtEventRequest;
import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.entities.DonationsAtEvents;
import com.grpc.grpc_server.entities.Event;

public interface DonationsAtEventsService {
    
    boolean registerDonationAtEvent(CreateDonationAtEventRequest request);
    DonationsAtEvents getDonationsAtEvents(Event event, Donation donation);
} 