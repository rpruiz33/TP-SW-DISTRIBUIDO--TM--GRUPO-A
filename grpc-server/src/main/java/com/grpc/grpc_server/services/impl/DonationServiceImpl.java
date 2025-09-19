package com.grpc.grpc_server.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.mapper.DonationMapper;
import com.grpc.grpc_server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grpc.grpc_server.MyServiceClass;
import com.grpc.grpc_server.MyServiceClass.AltaDonationRequest;
import com.grpc.grpc_server.MyServiceClass.DeleteDonationRequest;
import com.grpc.grpc_server.MyServiceClass.UpdateDonationRequest;
import com.grpc.grpc_server.entities.Category;
import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.repositories.DonationRepository;
import com.grpc.grpc_server.services.DonationService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class DonationServiceImpl implements DonationService {

    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private UserRepository userRepository;


    public List<Donation> getAllDonations(){

        return donationRepository.findAllWithEvents();

    }

    public List<Donation> getActiveDonations(){

        return donationRepository.findByRemoved(false);

    }

    public boolean altaDonation(MyServiceClass.AltaDonationRequest request) {

        boolean result =false;
        Donation d = new Donation();

        //Usuario que da el alta
        User u = userRepository.findByEmailOrUsername(request.getUsername(),request.getUsername()).orElse(null);

        if ( u != null) {
            d = DonationMapper.toEntity(request,u);
            donationRepository.save(d);
            result =true;
        }

        return result;
    }

    public boolean updateDonation(UpdateDonationRequest request){
        
        boolean result = false;

        Donation d = donationRepository.findById(request.getId());
        User u = userRepository.findByEmailOrUsername(request.getUsername(),request.getUsername()).orElse(null);

        if (d != null && u != null){
            d.setDescription(request.getDescription());
            d.setAmount(request.getAmount());
            d.setUserModification(u);
            d.setDateModification(LocalDateTime.now());
            donationRepository.save(d);
            result = true;
        }

        return result;
    }


    public boolean deleteDonation(DeleteDonationRequest request){

        boolean result=false;

        Donation d = donationRepository.findById(request.getId());
        User u  = userRepository.findByEmailOrUsername(request.getUsername(),request.getUsername()).orElse(null);

        if (d != null && u !=  null) {

            d.setRemoved(!d.getRemoved());
            d.setUserModification(u);
            d.setDateModification(LocalDateTime.now());
            donationRepository.save(d);
            result = true;
        }
        return result;

    }

    public Donation getDonationByDescription(String description){
        return donationRepository.findByDescription(description);
    }
}