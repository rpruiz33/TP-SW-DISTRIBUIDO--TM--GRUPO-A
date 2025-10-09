package com.grpc.grpc_server.repositories.grpc;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.grpc.Donation;
import com.grpc.grpc_server.entities.grpc.DonationsAtEvents;
import com.grpc.grpc_server.entities.grpc.Event;

@Repository
public interface DonationsAtEventsRepository extends JpaRepository<DonationsAtEvents, Long> {

    void deleteByEvent(Event deleteEvent);

    DonationsAtEvents findByEventAndDonation(Event event, Donation donation);

    @Query("SELECT dae FROM DonationsAtEvents dae WHERE dae.event.idEvent = :idEvent")
    List<DonationsAtEvents> findAllDonationsAtEventsByIdEvent(int idEvent);
}
