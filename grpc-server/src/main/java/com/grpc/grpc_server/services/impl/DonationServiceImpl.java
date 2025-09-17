package com.grpc.grpc_server.services.impl;

import java.time.LocalDateTime;
import java.util.List;

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


    public boolean deleteDonation(DeleteDonationRequest request){

        boolean result;

        Donation d = donationRepository.findById(request.getId());
        if (d == null) {
            result = false;
        } else {
            d.setRemoved(true);
            donationRepository.save(d);
            result = true;
        }
        return result;

}
public boolean altaDonation(MyServiceClass.AltaDonationRequest request) {

        Donation d = new Donation();
 
        Category cat =  Category.valueOf((request.getCategory()).toUpperCase()) ;
        d.setDescription(request.getDescription());
        d.setAmount(request.getAmount());
        d.setCategory(cat);
        d.setRemoved(false);
        donationRepository.save(d);
     return true;

    
    }        
 

    public Donation getDonationByDescription(String description){
        return donationRepository.findByDescription(description);
    }
}