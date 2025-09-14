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



    public List<Donation> getAllDonations(){

        return donationRepository.findAllWithEvents();

    }


    public boolean updateDonation(UpdateDonationRequest request){
        
        boolean result;

        Donation d = donationRepository.findById(request.getId());

        if (d == null) {
            result = false;
        } else {
            d.setDescription(request.getDescription());
            d.setAmount(request.getAmount());

            donationRepository.save(d);
            result = true;
        }

        return result;
    }

}
