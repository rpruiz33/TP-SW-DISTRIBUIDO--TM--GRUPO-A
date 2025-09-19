package com.grpc.grpc_server.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grpc.grpc_server.MyServiceClass.DonationAtEventRequest;
import com.grpc.grpc_server.MyServiceClass.GetAllDonationsAtEventRequest;
import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.entities.DonationsAtEvents;
import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.repositories.DonationRepository;
import com.grpc.grpc_server.repositories.DonationsAtEventsRepository ;
import com.grpc.grpc_server.repositories.EventRepository;
import com.grpc.grpc_server.services.DonationsAtEventsService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class DonationsAtEventsServiceImpl implements DonationsAtEventsService {
    
     @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private DonationsAtEventsRepository donationsAtEventsRepository;

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean registerDonationAtEvent(DonationAtEventRequest request) {

        boolean result = false;

        Donation donation = donationRepository.findByDescription(request.getDescription());
        Event event = eventRepository.findByIdEvent(request.getIdEvent());
        User user = userRepository.findByEmailOrUsername(request.getUsername(),request.getUsername()).orElse(null);

        DonationsAtEvents dae= getDonationsAtEvents(event, donation);

        
        if(donation != null && user !=null && event != null){ //chequear que exista la donación, el usuario y el evento

            if(dae == null){ //chequear que no exista la relacion

                if (donation.getAmount() > request.getQuantityDelivered()) { ///chequear que haya esa cantidad en el inventario

                

                    ///setteo y guardado en la bd
                    DonationsAtEvents donationAtEvent = new DonationsAtEvents();
                    donationAtEvent.setEvent(event);
                    donationAtEvent.setDonation(donation);
                    donationAtEvent.setQuantityDelivered(request.getQuantityDelivered());
                    donationsAtEventsRepository.save(donationAtEvent);
                    
                    // Restar stock
                    donation.setAmount(donation.getAmount() - request.getQuantityDelivered());

                    //Setteo de auditoria y guardado en bd
                    donation.setDateModification(LocalDateTime.now());
                    donation.setUserModification(user);
                    donationRepository.save(donation);

                    result = true;
                }

            }
            
        }

        return result;
        
    }

    public DonationsAtEvents getDonationsAtEvents(Event event, Donation donation){

        return donationsAtEventsRepository.findByEventAndDonation(event, donation);
    }

    @Transactional
    public boolean updateDonationAtEvent(DonationAtEventRequest request){

        boolean result = false;

        Donation donation = donationRepository.findByDescription(request.getDescription());
        Event event = eventRepository.findByIdEvent(request.getIdEvent());
        User user = userRepository.findByEmailOrUsername(request.getUsername(),request.getUsername()).orElse(null);

        DonationsAtEvents dae= getDonationsAtEvents(event, donation);

        
        if(donation != null && user !=null && event != null){ //chequear que exista la donación, el usuario y el evento

            if(dae != null){ //chequear que exista la relacion

                int newQuantityDelivered = request.getQuantityDelivered();

                if (donation.getAmount() > newQuantityDelivered) { ///chequear que haya esa cantidad en el inventario

                    int currentQuantityDelivered = dae.getQuantityDelivered();

                    if(currentQuantityDelivered < newQuantityDelivered ){

                        // Restar stock
                        donation.setAmount(donation.getAmount() - (newQuantityDelivered-currentQuantityDelivered));

                    }else{

                        // Restar stock
                        donation.setAmount(donation.getAmount() + (currentQuantityDelivered-newQuantityDelivered));
                    }

                    //Setteo de auditoria y guardado en bd en donation
                    donation.setDateModification(LocalDateTime.now());
                    donation.setUserModification(user);
                    donationRepository.save(donation);

                    ///Setteo y guardado en la bd en DonationsAtEvents
                
                    dae.setQuantityDelivered(newQuantityDelivered);
                    donationsAtEventsRepository.save(dae);

                    result = true;
                }

            }
            
        }


        return result;
    }


    public List<DonationsAtEvents> getAllDonationsAtEvent(GetAllDonationsAtEventRequest request){
        
        List<DonationsAtEvents> allDonationsAtEventsByEvent = null;

        allDonationsAtEventsByEvent = donationsAtEventsRepository.findAllDonationsAtEventsByIdEvent(request.getIdEvent());

        return allDonationsAtEventsByEvent;
    }
}
