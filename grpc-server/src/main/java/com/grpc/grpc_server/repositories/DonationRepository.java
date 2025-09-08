package com.grpc.grpc_server.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.Donation;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {


    @Query("SELECT DISTINCT d FROM Donation d JOIN FETCH d.events e JOIN FETCH e.event")
    List<Donation> findAllWithEvents();

}
