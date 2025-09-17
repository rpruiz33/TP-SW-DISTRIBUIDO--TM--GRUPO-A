package com.grpc.grpc_server.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.grpc.grpc_server.entities.User;
import com.grpc.grpc_server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grpc.grpc_server.MyServiceClass.CreateDonationAtEventRequest;
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
    public boolean registerDonationAtEvent(CreateDonationAtEventRequest request) {

        boolean result = false;

        Donation donation = donationRepository.findByDescription(request.getDescription());
        Event event = eventRepository.findByIdEvent(request.getIdEvent());
        User user = userRepository.findByEmailOrUsername(request.getUsername(),request.getUsername()).orElse(null);

        DonationsAtEvents dae= getDonationsAtEvents(event, donation);

        
        if(donation != null && user !=null){ //chequear que exista la donación y el usuario que la asigna

            if (donation.getAmount() > request.getQuantityDelivered()) { ///chequear que haya esa cantidad en el inventario

                if(dae == null){ // si no existe se crea donationAtEvent

                    DonationsAtEvents donationAtEvent = new DonationsAtEvents();
                    donationAtEvent.setEvent(event);
                    donationAtEvent.setDonation(donation);
                    donationAtEvent.setQuantityDelivered(request.getQuantityDelivered());

                    donationsAtEventsRepository.save(donationAtEvent);
                    
                    
                }else{ // si ya existe se modifican la cantidad entregada (quantityDelivered) y el stock (amount)
                    
                    dae.setQuantityDelivered(dae.getQuantityDelivered() + request.getQuantityDelivered());
                    donationsAtEventsRepository.save(dae);

                }

                // Restar stock
                donation.setAmount(donation.getAmount() - request.getQuantityDelivered());
                //Cambiar datos de auditoria
                donation.setDateModification(LocalDateTime.now());
                donation.setUserModification(user);

                donationRepository.save(donation);

                result = true;
            }
        }
        
        return result;
        
    }

    public DonationsAtEvents getDonationsAtEvents(Event event, Donation donation){

        return donationsAtEventsRepository.findByEventAndDonation(event, donation);
    }
}
