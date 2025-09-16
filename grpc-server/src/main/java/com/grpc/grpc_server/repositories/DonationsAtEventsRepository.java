package com.grpc.grpc_server.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.entities.DonationsAtEvents;
import com.grpc.grpc_server.entities.Event;

@Repository
public interface DonationsAtEventsRepository extends JpaRepository<DonationsAtEvents, Long> {

    void deleteByEvent(Event deleteEvent);

    DonationsAtEvents findByEventAndDonation(Event event, Donation donation);
}
